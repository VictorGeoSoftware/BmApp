package com.briel.marnisos.brielapp.data.repository

import com.briel.marnisos.brielapp.domain.models.ConsumptionReportModel
import com.briel.marnisos.brielapp.domain.models.ConsumptionStudyFailure
import com.briel.marnisos.brielapp.domain.models.ConsumptionStudyStateModel
import com.briel.marnisos.brielapp.domain.models.ConsumptionStudyStep
import com.briel.marnisos.brielapp.domain.models.JobStatusType
import com.briel.marnisos.brielapp.domain.monitoring.CrashErrorCategory
import com.briel.marnisos.brielapp.domain.monitoring.CrashReporter
import com.briel.marnisos.brielapp.domain.repository.AuthRepository
import com.briel.marnisos.brielapp.domain.repository.ConsumptionSessionRepository
import com.briel.marnisos.brielapp.domain.repository.ConsumptionStudyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

/**
 * Application-scoped implementation of [ConsumptionStudyRepository].
 *
 * Owns the submit -> poll -> fetch-result pipeline that previously lived inside
 * ComparatorViewModel, so it is no longer cancelled when the user navigates away.
 */
internal class ConsumptionStudyRepositoryImpl(
    private val repository: Repository,
    private val consumptionSessionRepository: ConsumptionSessionRepository,
    private val authRepository: AuthRepository,
    private val crashReporter: CrashReporter,
    private val applicationScope: CoroutineScope,
) : ConsumptionStudyRepository {

    private val _studyState = MutableStateFlow<ConsumptionStudyStateModel>(
        value = ConsumptionStudyStateModel.Idle,
    )
    override val studyState: StateFlow<ConsumptionStudyStateModel> = _studyState.asStateFlow()

    private var runningStudy: Job? = null

    override fun startFromPdf(pdfFile: File) {
        startStudy(initialStep = ConsumptionStudyStep.UPLOADING_BILL) {
            repository.submitConsumptionReportJob(pdfFile)
        }
    }

    override fun startFromCups(cupsCode: String) {
        startStudy(initialStep = ConsumptionStudyStep.SUBMITTING_CUPS) {
            repository.submitConsumptionReportJobByCups(cupsCode)
        }
    }

    private fun startStudy(
        initialStep: ConsumptionStudyStep,
        submit: suspend () -> Result<com.briel.marnisos.brielapp.domain.models.JobSubmissionModel>,
    ) {
        runningStudy?.cancel()
        runningStudy = applicationScope.launch {
            crashReporter.setUseCaseContext("consumption_study")
            resetActiveStudy()
            _studyState.value = ConsumptionStudyStateModel.InProgress(initialStep)

            submit()
                .onSuccess { submission -> pollJobStatus(submission.jobId) }
                .onFailure { error ->
                    fail(error, ConsumptionStudyFailure.SUBMIT_FAILED, "submit_consumption_study")
                }
        }
    }

    override suspend fun discardActiveStudy() {
        runningStudy?.cancel()
        runningStudy = null
        resetActiveStudy()
        _studyState.value = ConsumptionStudyStateModel.Idle
    }

    private suspend fun resetActiveStudy() {
        consumptionSessionRepository.clear()
        repository.clearCurrentUserConditions()
        repository.clearLastCompletedJobId()
    }

    private suspend fun pollJobStatus(jobId: String) {
        var attempts = 0

        while (attempts < MAX_POLL_ATTEMPTS) {
            delay(POLL_DELAY_MILLIS)
            attempts++

            val statusResult = repository.getJobStatus(jobId)
            val jobStatus = statusResult.getOrElse { error ->
                fail(error, ConsumptionStudyFailure.STATUS_CHECK_FAILED, "get_job_status")
                return
            }

            when (jobStatus.status) {
                JobStatusType.COMPLETED -> {
                    _studyState.value =
                        ConsumptionStudyStateModel.InProgress(ConsumptionStudyStep.FETCHING_RESULTS)
                    fetchJobResult(jobId)
                    return
                }

                JobStatusType.FAILED -> {
                    fail(
                        throwable = IllegalStateException("Job processing failed for submitted report"),
                        failure = ConsumptionStudyFailure.PROCESSING_FAILED,
                        operation = "poll_job_status_failed",
                    )
                    return
                }

                JobStatusType.PROCESSING -> {
                    _studyState.value =
                        ConsumptionStudyStateModel.InProgress(ConsumptionStudyStep.PROCESSING)
                }

                JobStatusType.PENDING -> {
                    _studyState.value =
                        ConsumptionStudyStateModel.InProgress(ConsumptionStudyStep.QUEUED)
                }
            }
        }

        fail(
            throwable = IllegalStateException("Job processing timeout reached"),
            failure = ConsumptionStudyFailure.TIMEOUT,
            operation = "poll_job_status_timeout",
        )
    }

    private suspend fun fetchJobResult(jobId: String) {
        repository.getJobResult(jobId)
            .onSuccess { report ->
                repository.persistLastCompletedJobId(jobId)
                publishReport(jobId, report)
                _studyState.value = ConsumptionStudyStateModel.Idle
            }
            .onFailure { error ->
                if (error.isJobExpiredOrNotFound()) {
                    repository.clearLastCompletedJobId()
                }
                fail(error, ConsumptionStudyFailure.RESULT_FETCH_FAILED, "get_job_result")
            }
    }

    override suspend fun restoreLastCompletedStudy() {
        val jobId = repository.getLastCompletedJobId() ?: return

        repository.getJobResult(jobId)
            .onSuccess { report -> publishReport(jobId, report) }
            .onFailure { error ->
                if (error.isJobExpiredOrNotFound()) {
                    repository.clearLastCompletedJobId()
                    return
                }
                crashReporter.recordNonFatal(
                    throwable = error,
                    category = CrashErrorCategory.BACKEND,
                    operation = "restore_last_completed_study",
                )
            }
    }

    override suspend fun refreshActiveStudy() {
        val jobId = consumptionSessionRepository.session.value?.jobId ?: return

        repository.refreshConsumptionReport(jobId)
            .onSuccess { report -> publishReport(jobId, report) }
            .onFailure { error ->
                if (error.isJobExpiredOrNotFound()) {
                    repository.clearLastCompletedJobId()
                    return
                }
                crashReporter.recordNonFatal(
                    throwable = error,
                    category = CrashErrorCategory.BACKEND,
                    operation = "refresh_consumption_report",
                )
            }
    }

    override fun consumeFailure() {
        if (_studyState.value is ConsumptionStudyStateModel.Failed) {
            _studyState.value = ConsumptionStudyStateModel.Idle
        }
    }

    private suspend fun publishReport(jobId: String, report: ConsumptionReportModel) {
        consumptionSessionRepository.updateFromReport(jobId = jobId, report = report)
        trackProposalResponseReceived()
    }

    private suspend fun trackProposalResponseReceived() {
        val authUser = authRepository.getCurrentUser() ?: return
        val email = authUser.email?.trim().orEmpty()
        if (email.isBlank()) return

        val name = authUser.displayName?.takeIf { it.isNotBlank() } ?: email.substringBefore('@')
        repository.incrementProposalResponseCounter(name = name, email = email)
    }

    private fun fail(
        throwable: Throwable,
        failure: ConsumptionStudyFailure,
        operation: String,
    ) {
        crashReporter.recordNonFatal(
            throwable = throwable,
            category = CrashErrorCategory.ASYNC_JOB,
            operation = operation,
        )
        _studyState.value = ConsumptionStudyStateModel.Failed(failure)
    }

    private fun Throwable.isJobExpiredOrNotFound(): Boolean {
        val errorMessage = message.orEmpty()
        return errorMessage.contains("404") ||
            errorMessage.contains("not found", ignoreCase = true)
    }

    private companion object {
        const val MAX_POLL_ATTEMPTS = 60
        const val POLL_DELAY_MILLIS = 3_000L
    }
}
