package com.briel.marnisos.brielapp.domain.usecases

/**
 * Returns the stable per-install device identifier (phone UUID) used to bind a
 * single phone to an account.
 */
fun interface GetDeviceIdUseCase {
    operator fun invoke(): String
    companion object Factory
}
