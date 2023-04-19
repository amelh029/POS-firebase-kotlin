package com.example.myapplication.utils.config

import java.util.ArrayList

class ProductUtils {
    companion object{
        fun find(array: ArrayList<ProductOrderDetail>, detail: ProductOrderDetail?): Int? {
            for ((i, v) in array.withIndex()) {
                if (v.product == null) continue
                if (
                    isEqual(v.product, detail?.product)
                    &&
                    isEqual(v.variants, detail?.variants)
                    &&
                    isEqual(v.mixProducts, detail?.mixProducts)
                ) {
                    return i
                }
            }
            return null
        }

        private fun isEqual(any1: Any?, any2: Any?): Boolean {
            return any1 == any2
        }
    }
}