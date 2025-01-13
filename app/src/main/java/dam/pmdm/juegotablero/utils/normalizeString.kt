package dam.pmdm.juegotablero.utils

import java.text.Normalizer

fun String.normalizeString(): String {
    val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
    return Regex("\\p{Mn}").replace(normalized, "")
}

