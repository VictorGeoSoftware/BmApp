package com.briel.marnisos.brielapp.ui.views.pricetable

/**
 * Proposal columns are a fixed 200.dp wide, so a long commercial name either wrapped to a
 * second line and broke the row alignment across the table, or lost its tail — which is
 * exactly where these names differ ("... A CO 5" vs "... A CO 3").
 *
 * Truncating in the middle keeps both the brand at the head and the variant at the tail.
 */
internal fun String.middleEllipsize(
    maxLength: Int = PROPOSAL_TITLE_MAX_LENGTH,
    headLength: Int = PROPOSAL_TITLE_HEAD_LENGTH,
    tailLength: Int = PROPOSAL_TITLE_TAIL_LENGTH,
): String {
    if (length <= maxLength) return this
    if (headLength + tailLength >= length) return this

    return take(headLength) + ELLIPSIS + takeLast(tailLength)
}

private const val ELLIPSIS = "..."

/** Longest title rendered as-is; anything above this is ellipsized. */
internal const val PROPOSAL_TITLE_MAX_LENGTH = 17

/** Characters kept before the ellipsis. */
internal const val PROPOSAL_TITLE_HEAD_LENGTH = 7

/** Characters kept after the ellipsis — the tail carries the variant, so it gets one more. */
internal const val PROPOSAL_TITLE_TAIL_LENGTH = 8
