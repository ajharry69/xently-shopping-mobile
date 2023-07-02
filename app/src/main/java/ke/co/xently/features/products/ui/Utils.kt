package ke.co.xently.features.products.ui

fun String.cleansedForNumberParsing(): String {
    return trim().replace(",", "")
}