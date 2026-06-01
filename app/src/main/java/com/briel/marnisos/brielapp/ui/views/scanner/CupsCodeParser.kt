package com.briel.marnisos.brielapp.ui.views.scanner

/**
 * Client-side CUPS validator. Mirrors `CupsValidation` on the backend so the
 * scanner never confirms a code that the API would reject.
 *
 * Format (Real Decreto 1110/2007):
 *   ES + DDDD + AAAAAAAAAAAA + CC [+ FF]
 *
 * - 4 distribuidora digits
 * - 12 supply-point digits
 * - 2 control letters (mod-23 checksum over the 16 digits using
 *   "TRWAGMYFPDXBNJZSQVHLCKE")
 * - optional 2-char frontier suffix: digit + letter
 */
internal object CupsCodeParser {
    private const val CONTROL_ALPHABET = "TRWAGMYFPDXBNJZSQVHLCKE"

    // Loose regex used to find candidate substrings inside noisy OCR output.
    // Allows whitespace (including newlines), punctuation, and letters that OCR
    // may substitute for digits (O, I, L, S, B, Z, G).
    private val candidateRegex = Regex("ES[\\s\\-.,:;/|A-Z0-9]{18,28}", RegexOption.DOT_MATCHES_ALL)

    // Strict structural regex applied after normalization.
    private val structureRegex = Regex("^ES(\\d{16})([A-Z]{2})([0-9][A-Z])?$")

    fun normalize(raw: String): String =
        raw.uppercase().filter { it.isLetterOrDigit() }

    /**
     * Fixes common OCR mis-reads in the 16-digit supply-point portion of a
     * normalized (uppercase, alphanumeric-only) CUPS string.
     *
     * Positions 2-17 must be digits, so letter look-alikes are replaced:
     *   O → 0, I → 1, l → 1 (already uppercase after normalize), S → 5, B → 8, Z → 2, G → 6
     * Positions 18-19 (control code) must be letters, so digit look-alikes are replaced:
     *   0 → O, 1 → I, 5 → S, 8 → B, 2 → Z
     */
    private fun fixOcrErrors(normalized: String): String {
        if (normalized.length < 20 || !normalized.startsWith("ES")) return normalized
        val sb = StringBuilder(normalized.length)
        sb.append("ES")
        // Digit zone: positions 2..17
        for (i in 2 until minOf(18, normalized.length)) {
            sb.append(
                when (normalized[i]) {
                    'O' -> '0'
                    'I', 'L' -> '1'
                    'S' -> '5'
                    'B' -> '8'
                    'Z' -> '2'
                    'G' -> '6'
                    else -> normalized[i]
                }
            )
        }
        // Control-code zone: positions 18..19
        for (i in 18 until minOf(20, normalized.length)) {
            sb.append(
                when (normalized[i]) {
                    '0' -> 'O'
                    '1' -> 'I'
                    '5' -> 'S'
                    '8' -> 'B'
                    '2' -> 'Z'
                    else -> normalized[i]
                }
            )
        }
        // Optional frontier suffix: positions 20..21 (digit + letter) – pass through
        if (normalized.length > 20) {
            sb.append(normalized.substring(20))
        }
        return sb.toString()
    }

    fun isValid(raw: String): Boolean {
        val normalized = fixOcrErrors(normalize(raw))
        val match = structureRegex.matchEntire(normalized) ?: return false
        val supplyDigits = match.groupValues[1]
        val controlCode = match.groupValues[2]
        return computeControlCode(supplyDigits) == controlCode
    }

    /**
     * Attempts to extract a valid CUPS code from a normalized candidate that
     * may have trailing garbage (e.g. "TARIFA" glued onto the end by OCR).
     * Tries 22 chars first (with frontier suffix), then 20 (without).
     */
    private fun tryTrimToValid(normalized: String): String? {
        val fixed = fixOcrErrors(normalized)
        // Try exact length first (already 20 or 22)
        if (isValid(fixed)) return fixed
        // Try truncating to 22 (with frontier suffix), then 20 (without)
        for (len in intArrayOf(22, 20)) {
            if (fixed.length > len) {
                val trimmed = fixed.substring(0, len)
                if (isValid(trimmed)) return trimmed
            }
        }
        return null
    }

    fun extractBestCandidate(text: String): String? {
        val upper = text.uppercase()
        candidateRegex.findAll(upper)
            .mapNotNull { tryTrimToValid(normalize(it.value)) }
            .firstOrNull()
            ?.let { return it }

        // Fallback: normalize the full text and search again (handles cases
        // where separators between ES and the digits confused the first pass).
        val cleaned = normalize(text)
        return candidateRegex.findAll(cleaned)
            .mapNotNull { tryTrimToValid(normalize(it.value)) }
            .firstOrNull()
    }

    private fun computeControlCode(sixteenDigits: String): String? {
        if (sixteenDigits.length != 16 || !sixteenDigits.all { it.isDigit() }) return null
        val n = sixteenDigits.toLong()
        val mod = (n % 529L).toInt()
        val first = mod / 23
        val second = mod % 23
        return "${CONTROL_ALPHABET[first]}${CONTROL_ALPHABET[second]}"
    }
}
