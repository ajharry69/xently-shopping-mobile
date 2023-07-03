package ke.co.xently.features.compareproducts.datasources

import ke.co.xently.features.compareproducts.models.CompareProduct

interface CompareProductDataSource<TRequest : CompareProduct, TResponse : CompareProduct> {
    suspend fun getCompareProducts(request: TRequest): List<TResponse>
}