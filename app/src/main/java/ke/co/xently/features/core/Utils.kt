package ke.co.xently.features.core

fun String.cleansedForNumberParsing(): String {
    return trim().replace(",", "")
}