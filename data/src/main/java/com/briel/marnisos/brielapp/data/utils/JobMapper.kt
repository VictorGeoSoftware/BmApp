package com.briel.marnisos.brielapp.data.utils

import com.briel.marnisos.brielapp.data.model.prices.JobStatus
import com.briel.marnisos.brielapp.data.model.prices.JobStatusResponse
import com.briel.marnisos.brielapp.data.model.prices.JobSubmissionResponse
import com.briel.marnisos.brielapp.domain.models.JobStatusModel
import com.briel.marnisos.brielapp.domain.models.JobStatusType
import com.briel.marnisos.brielapp.domain.models.JobSubmissionModel

object JobMapper {
    fun JobSubmissionResponse.mapToDomain(): JobSubmissionModel {
        return JobSubmissionModel(
            jobId = this.jobId,
            status = this.status
        )
    }

    fun JobStatusResponse.mapToDomain(): JobStatusModel {
        return JobStatusModel(
            jobId = this.jobId,
            status = this.status.mapToDomain()
        )
    }

    private fun JobStatus.mapToDomain(): JobStatusType {
        return when (this) {
            JobStatus.PENDING -> JobStatusType.PENDING
            JobStatus.PROCESSING -> JobStatusType.PROCESSING
            JobStatus.COMPLETED -> JobStatusType.COMPLETED
            JobStatus.FAILED -> JobStatusType.FAILED
        }
    }
}
