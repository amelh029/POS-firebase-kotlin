package com.example.myapplication.view.viewModel

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.myapplication.data.source.domain.GetProductVariantOptions
import com.example.myapplication.data.source.local.entity.room.bridge.VariantMix
import com.example.myapplication.data.source.local.entity.room.bridge.VariantProduct
import com.example.myapplication.data.source.local.entity.room.master.Category
import com.example.myapplication.data.source.local.entity.room.master.Product
import com.example.myapplication.data.source.local.entity.room.master.Reserves
import com.example.myapplication.data.source.local.entity.room.master.Variant
import com.example.myapplication.data.source.local.entity.room.master.VariantOption
import com.example.myapplication.data.source.repository.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ProductViewModel (
    private val variantsRepository: VariantsRepository,
    private val variantOptionsRepository: VariantOptionsRepository,
    private val categoriesRepository: CategoriesRepository,
    private val productsRepository: ProductsRepository,
    private val productVariantsRepository: ProductVariantsRepository,
    private val getProductVariantOptions: GetProductVariantOptions,
    private val variantMixesRepository: VariantMixesRepository
) : ViewModel() {

    companion object : ViewModelFromFactory<ProductViewModel>() {
        fun getMainViewModel(activity: FragmentActivity): ProductViewModel {
            return buildViewModel(activity, ProductViewModel::class.java)
        }
    }

    fun getProduct(idProduct: Long) = productsRepository.getProductById(idProduct)
    fun getProductWithCategory(idProduct: Long) =
        productsRepository.getProductWithCategory(idProduct)

    fun getAllProducts() = productsRepository
        .getAllProductWithCategories()
        .map {
            it.groupBy { product -> product.category }
                .filterKeys { category -> category.isActive }
        }

    suspend fun isProductHasVariant(idProduct: Long) = productVariantsRepository
        .isProductHasVariants(idProduct)

    fun getProductVariantOptions(idProduct: Long) = getProductVariantOptions.invoke(idProduct)

    fun getProductVariantCount(idProduct: Long) = getProductVariantOptions.invoke(idProduct).map {
        it?.sumOf { variant ->
            variant.options.size
        } ?: 0
    }

    fun getVariantProduct(
        idProduct: Long,
        idVariantOption: Long
    ) = productVariantsRepository.getVariantProduct(idProduct, idVariantOption)

    fun getVariantsProductById(idProduct: Long) =
        productVariantsRepository.getVariantsProductById(idProduct)

    fun getVariantProductById(idProduct: Long) =
        productVariantsRepository.getVariantProductById(idProduct)

    fun insertVariantProduct(data: VariantProduct) {
        viewModelScope.launch {
            productVariantsRepository.insertVariantProduct(data)
        }
    }

    fun removeVariantProduct(data: VariantProduct) {
        viewModelScope.launch {
            productVariantsRepository.removeVariantProduct(data)
        }
    }

    fun getVariantMixProductById(
        idVariant: Long,
        idProduct: Long
    ) = variantMixesRepository.getVariantMixProductById(idVariant, idProduct)

    fun getVariantMixProduct(idVariant: Long) =
        variantMixesRepository.getVariantMixProduct(idVariant)

    fun insertVariantMix(data: VariantMix) {
        viewModelScope.launch {
            variantMixesRepository.insertVariantMix(data)
        }
    }

    fun removeVariantMix(data: VariantMix) {
        viewModelScope.launch {
            variantMixesRepository.removeVariantMix(data)
        }
    }

    fun getProductWithCategories(category: Long) =
        productsRepository.getProductWithCategories(category)

    suspend fun insertProduct(data: Product) = productsRepository.insertProduct(data)

    fun updateProduct(data: Product) {
        viewModelScope.launch {
            productsRepository.updateProduct(data)
        }
    }

    fun getCategories(query: SimpleSQLiteQuery) = categoriesRepository.getCategories(query)


    fun insertCategory(data: Category) {
        viewModelScope.launch {
            categoriesRepository.insertCategory(data)
        }
    }

    fun updateCategory(data: Category) {
        viewModelScope.launch {
            categoriesRepository.updateCategory(data)
        }
    }



    val variants = variantsRepository.getVariants()

    fun insertVariant(data: Variant) {
        viewModelScope.launch {
            variantsRepository.insertVariant(data)
        }
    }

    fun updateVariant(data: Variant) {
        viewModelScope.launch {
            variantsRepository.updateVariant(data)
        }
    }

    fun getVariantOptions(query: SupportSQLiteQuery) =
        variantOptionsRepository.getVariantOptions(query)

    fun insertVariantOption(data: VariantOption) {
        viewModelScope.launch {
            variantOptionsRepository.insertVariantOption(data)
        }
    }

    fun updateVariantOption(data: VariantOption) {
        viewModelScope.launch {
            variantOptionsRepository.updateVariantOption(data)
        }
    }
}