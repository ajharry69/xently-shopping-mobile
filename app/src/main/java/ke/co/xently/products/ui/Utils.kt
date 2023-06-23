package ke.co.xently.products.ui

fun String.cleansedForNumberParsing(): String {
    return trim().replace(",", "")
}