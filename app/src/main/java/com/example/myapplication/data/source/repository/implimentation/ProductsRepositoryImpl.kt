package com.example.myapplication.data.source.repository.implimentation

import com.example.myapplication.data.source.local.entity.room.master.Product
import com.example.myapplication.data.source.local.room.ProductsDao
import com.example.myapplication.data.source.repository.ProductsRepository

class ProductsRepositoryImpl (
    private val dao: ProductsDao
) : ProductsRepository {

    companion object {
        @Volatile
        private var INSTANCE: ProductsRepositoryImpl? = null
        fun getInstance(
            dao: ProductsDao
        ): ProductsRepositoryImpl {
            if (INSTANCE == null) {
                synchronized(ProductsRepositoryImpl::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = ProductsRepositoryImpl(dao = dao)
                    }
                }
            }
            return INSTANCE!!
        }
    }

    override fun getProductWithCategories(category: Long) = dao.getProductWithCategories(category)
    override fun getAllProductWithCategories() = dao.getAllProductWithCategories()
    override fun getProductWithCategory(productId: Long) = dao.getProductWithCategory(productId)
    override fun getProductById(productId: Long) = dao.getProductAsFlow(productId)
    override suspend fun insertProduct(data: Product) = dao.insertProduct(data)
    override suspend fun updateProduct(data: Product) {
        dao.updateProduct(data)
    }
}