package com.briel.marnisos.brielapp.domain.models

/** Failure of a comprehensive-report request; the message is the failure name only. */
class PremiumReportException(val failure: PremiumReportFailure) : Exception(failure.name)
