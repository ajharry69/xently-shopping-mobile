package ke.co.xently.products.exceptions

class ProductNotFoundException(override val message: String) : RuntimeException(message)