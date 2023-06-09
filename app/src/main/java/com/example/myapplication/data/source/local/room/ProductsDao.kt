package com.example.myapplication.data.source.local.room

import androidx.room.*
import com.example.myapplication.data.source.local.entity.room.helper.ProductWithCategory
import com.example.myapplication.data.source.local.entity.room.master.Category
import com.example.myapplication.data.source.local.entity.room.master.Product
import kotlinx.coroutines.flow.Flow


@Dao
interface ProductsDao {

    @Query("SELECT * FROM ${Product.DB_NAME} WHERE ${Product.ID} = :idProduct")
    suspend fun getProduct(idProduct: Long): Product

    @Query("SELECT * FROM ${Product.DB_NAME} WHERE ${Product.ID} = :idProduct")
    fun getProductAsFlow(idProduct: Long): Flow<Product>

    @Transaction
    @Query("SELECT * FROM ${Product.DB_NAME} WHERE ${Category.ID} = :category")
    fun getProductWithCategories(category: Long): Flow<List<ProductWithCategory>>

    @Transaction
    @Query("SELECT * FROM ${Product.DB_NAME} WHERE ${Product.ID} = :productId")
    fun getProductWithCategory(productId: Long): Flow<ProductWithCategory?>

    @Transaction
    @Query("SELECT * FROM ${Product.DB_NAME}")
    fun getAllProductWithCategories(): Flow<List<ProductWithCategory>>

    @Query("UPDATE ${Product.DB_NAME} SET ${Product.STOCK}" +
            " = ((SELECT ${Product.STOCK} FROM ${Product.DB_NAME} WHERE ${Product.ID} = :idProduct) +" +
            " :amount) WHERE ${Product.ID} = :idProduct")
    fun increaseProductStock(idProduct: Long, amount: Int)

    @Query("UPDATE ${Product.DB_NAME} SET ${Product.STOCK} = ((SELECT ${Product.STOCK} FROM ${Product.DB_NAME} WHERE ${Product.ID} = :idProduct) - :amount) WHERE ${Product.ID} = :idProduct")
    fun decreaseProductStock(idProduct: Long, amount: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(data: Product): Long

    @Update
    fun updateProduct(data: Product)
}
