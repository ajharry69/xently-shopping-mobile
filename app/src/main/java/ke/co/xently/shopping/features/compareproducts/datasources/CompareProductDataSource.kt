package ke.co.xently.shopping.features.compareproducts.datasources

import ke.co.xently.shopping.features.compareproducts.models.CompareProduct

interface CompareProductDataSource<TRequest : CompareProduct, TResponse : CompareProduct> {
    suspend fun compareProducts(request: TRequest): TResponse
}