package com.briel.marnisos.brielapp.data.repository

import com.briel.marnisos.brielapp.data.local.CurrentUserConditionsLocalDataSource
import com.briel.marnisos.brielapp.data.local.LastCompletedJobIdLocalDataSource
import com.briel.marnisos.brielapp.data.mappers.PriceMapper.mapToDomain
import com.briel.marnisos.brielapp.data.network.PriceTableApi
import com.briel.marnisos.brielapp.data.utils.ComparatorReportPdfMapper.toData
import com.briel.marnisos.brielapp.data.utils.ConsumptionReportMapper.mapToDomain
import com.briel.marnisos.brielapp.data.utils.JobMapper.mapToDomain
import com.briel.marnisos.brielapp.data.utils.UserConsumptionUtils.mapUserConsumptionResponseToDomain
import com.briel.marnisos.brielapp.domain.models.ComparatorReportPdfModel
import com.briel.marnisos.brielapp.domain.models.ConsumptionReportModel
import com.briel.marnisos.brielapp.domain.models.CurrentUserConditionsModel
import com.briel.marnisos.brielapp.domain.models.JobStatusModel
import com.briel.marnisos.brielapp.domain.models.JobSubmissionModel
import com.briel.marnisos.brielapp.domain.models.PriceTablesInformationModel
import com.briel.marnisos.brielapp.domain.models.UserConsumptionModel
import kotlinx.coroutines.flow.Flow
import java.io.File

interface Repository {
    suspend fun getPriceTables(): Result<PriceTablesInformationModel>
    suspend fun getUserConsumptionData(): Result<UserConsumptionModel>

    suspend fun submitConsumptionReportJob(pdfFile: File): Result<JobSubmissionModel>
    suspend fun submitConsumptionReportJobByCups(cupsCode: String): Result<JobSubmissionModel>
    suspend fun getJobStatus(jobId: String): Result<JobStatusModel>
    suspend fun getJobResult(jobId: String): Result<ConsumptionReportModel>
    suspend fun refreshConsumptionReport(jobId: String): Result<ConsumptionReportModel>
    suspend fun generateComparatorReportPdf(reportModel: ComparatorReportPdfModel): Result<ByteArray>

    suspend fun persistLastCompletedJobId(jobId: String)
    suspend fun getLastCompletedJobId(): String?
    suspend fun clearLastCompletedJobId()

    suspend fun setUserOnline(name: String, email: String): Result<Unit>
    suspend fun setUserOffline(name: String, email: String): Result<Unit>
    suspend fun incrementProposalResponseCounter(name: String, email: String): Result<Unit>

    fun observeCurrentUserConditions(): Flow<CurrentUserConditionsModel?>
    suspend fun persistCurrentUserConditions(currentUserConditions: CurrentUserConditionsModel)
    suspend fun clearCurrentUserConditions()
}

// Provide Repository using injected api
fun buildRepository(
    priceTableApi: PriceTableApi,
    lastCompletedJobIdLocalDataSource: LastCompletedJobIdLocalDataSource,
    currentUserConditionsLocalDataSource: CurrentUserConditionsLocalDataSource
): Repository = RepositoryImpl(
    priceTableApi = priceTableApi,
    lastCompletedJobIdLocalDataSource = lastCompletedJobIdLocalDataSource,
    currentUserConditionsLocalDataSource = currentUserConditionsLocalDataSource
)

private class RepositoryImpl(
    private val priceTableApi: PriceTableApi,
    private val lastCompletedJobIdLocalDataSource: LastCompletedJobIdLocalDataSource,
    private val currentUserConditionsLocalDataSource: CurrentUserConditionsLocalDataSource
) : Repository {
    override suspend fun getPriceTables(): Result<PriceTablesInformationModel> {
        return priceTableApi.getPriceTableResults().map { response ->
            PriceTablesInformationModel(
                priceTables = response.results.map { it.extractedTables.mapToDomain() },
                iva = response.iva,
                impuestoElectrico = response.impuestoElectrico
            )
        }
    }

    override suspend fun getUserConsumptionData(): Result<UserConsumptionModel> {
        return priceTableApi.getUserConsumptionData().map { response ->
            response.mapUserConsumptionResponseToDomain()
        }
    }

    override suspend fun submitConsumptionReportJob(pdfFile: File): Result<JobSubmissionModel> {
        return priceTableApi.submitConsumptionReportJob(pdfFile).map { response ->
            response.mapToDomain()
        }
    }

    override suspend fun submitConsumptionReportJobByCups(cupsCode: String): Result<JobSubmissionModel> {
        return priceTableApi.submitConsumptionReportJobByCups(cupsCode).map { response ->
            response.mapToDomain()
        }
    }

    override suspend fun getJobStatus(jobId: String): Result<JobStatusModel> {
        return priceTableApi.getJobStatus(jobId).map { response ->
            response.mapToDomain()
        }
    }

    override suspend fun getJobResult(jobId: String): Result<ConsumptionReportModel> {
        return priceTableApi.getJobResult(jobId).map { response ->
            response.mapToDomain()
        }
    }

    override suspend fun refreshConsumptionReport(jobId: String): Result<ConsumptionReportModel> {
        return priceTableApi.refreshConsumptionReport(jobId).map { response ->
            response.mapToDomain()
        }
    }

    override suspend fun generateComparatorReportPdf(reportModel: ComparatorReportPdfModel): Result<ByteArray> {
        return priceTableApi.generateComparatorReportPdf(reportModel.toData())
    }

    override suspend fun persistLastCompletedJobId(jobId: String) {
        lastCompletedJobIdLocalDataSource.persist(jobId)
    }

    override suspend fun getLastCompletedJobId(): String? {
        return lastCompletedJobIdLocalDataSource.getValidOrNull()
    }

    override suspend fun clearLastCompletedJobId() {
        lastCompletedJobIdLocalDataSource.clear()
    }

    override suspend fun setUserOnline(name: String, email: String): Result<Unit> {
        return priceTableApi.setUserOnline(name = name, email = email)
    }

    override suspend fun setUserOffline(name: String, email: String): Result<Unit> {
        return priceTableApi.setUserOffline(name = name, email = email)
    }

    override suspend fun incrementProposalResponseCounter(name: String, email: String): Result<Unit> {
        return priceTableApi.incrementProposalResponseCounter(name = name, email = email)
    }

    override fun observeCurrentUserConditions(): Flow<CurrentUserConditionsModel?> {
        return currentUserConditionsLocalDataSource.observe()
    }

    override suspend fun persistCurrentUserConditions(currentUserConditions: CurrentUserConditionsModel) {
        currentUserConditionsLocalDataSource.persist(currentUserConditions)
    }

    override suspend fun clearCurrentUserConditions() {
        currentUserConditionsLocalDataSource.clearData()
    }
}
