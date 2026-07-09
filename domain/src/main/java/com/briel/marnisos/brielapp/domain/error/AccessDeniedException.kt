package com.briel.marnisos.brielapp.domain.error

/**
 * Thrown when the backend rejects an authenticated user for authorization
 * reasons, e.g. the account is not on the access allowlist or (later) the
 * account is already bound to a different phone.
 *
 * Callers must sign the user out and show a "contact the administration team"
 * message, since the Firebase credential itself is valid and would otherwise
 * persist across app launches.
 */
class AccessDeniedException(message: String?) : Exception(message)
