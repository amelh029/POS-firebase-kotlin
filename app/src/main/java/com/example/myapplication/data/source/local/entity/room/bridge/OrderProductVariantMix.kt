package com.example.myapplication.data.source.local.entity.room.bridge

import androidx.room.*
import com.example.myapplication.data.source.local.entity.room.master.Product
import com.example.myapplication.data.source.local.room.AppDatabase.Companion.UPLOAD
import com.example.myapplication.data.source.remote.response.helper.RemoteClassUtils
import com.google.firebase.firestore.QuerySnapshot
import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap


@Entity(
    tableName = OrderProductVariantMix.DB_NAME,
    foreignKeys = [
        ForeignKey(
            entity = OrderDetail::class,
            parentColumns = [OrderDetail.ID],
            childColumns = [OrderDetail.ID],
            onDelete = ForeignKey.CASCADE),
        ForeignKey(
            entity = Product::class,
            parentColumns = [Product.ID],
            childColumns = [Product.ID],
            onDelete = ForeignKey.CASCADE)
    ],
    indices = [
        Index(value = [OrderProductVariantMix.ID]),
        Index(value = [OrderDetail.ID]),
        Index(value = [Product.ID])
    ]
)
data class OrderProductVariantMix(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    var id: Long,

    @ColumnInfo(name = OrderDetail.ID)
    var idOrderDetail: Long,

    @ColumnInfo(name = Product.ID)
    var idProduct: Long,

    @ColumnInfo(name = AMOUNT)
    var amount: Int,

    @ColumnInfo(name = UPLOAD)
    var isUpload: Boolean
): Serializable {
    companion object: RemoteClassUtils<OrderProductVariantMix> {
        const val ID = "id_order_product_variant_mix"
        const val AMOUNT = "amount"

        const val DB_NAME = "order_product_variant_mix"

        override fun toHashMap(data: OrderProductVariantMix): HashMap<String, Any?> {
            return hashMapOf(
                ID to data.id,
                OrderDetail.ID to data.idOrderDetail,
                Product.ID to data.idProduct,
                AMOUNT to data.amount,
                UPLOAD to data.isUpload
            )
        }

        override fun toListClass(result: QuerySnapshot): List<OrderProductVariantMix> {
            val array: ArrayList<OrderProductVariantMix> = ArrayList()
            for (document in result){
                val mix = OrderProductVariantMix(
                    document.data[ID] as Long,
                    document.data[OrderDetail.ID] as Long,
                    document.data[Product.ID] as Long,
                    (document.data[AMOUNT] as Long).toInt(),
                    document.data[UPLOAD] as Boolean
                )
                array.add(mix)
            }
            return array
        }
    }

    @Ignore
    constructor(idOrderDetail: Long, idVariantMix: Long, amount: Int): this(0, idOrderDetail, idVariantMix, amount, false)
}
