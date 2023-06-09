package com.example.myapplication.data.source.local.entity.room.bridge

import androidx.room.*
import com.example.myapplication.data.source.local.entity.room.master.Order
import com.example.myapplication.data.source.local.entity.room.master.Promo
import com.example.myapplication.data.source.local.room.AppDatabase


@Entity(
    tableName = OrderPromo.DB_NAME,
    foreignKeys = [
        ForeignKey(
            entity = Order::class,
            parentColumns = [Order.NO],
            childColumns = [Order.NO],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Promo::class,
            parentColumns = [Promo.ID],
            childColumns = [Promo.ID],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = [OrderPromo.ID]),
        Index(value = [Order.NO]),
        Index(value = [Promo.ID])
    ]
)
data class OrderPromo(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    var id: Long,

    @ColumnInfo(name = Order.NO)
    var orderNO: String,

    @ColumnInfo(name = Promo.ID)
    var idPromo: Long,

    @ColumnInfo(name = PROMO)
    var totalPromo: Long,

    @ColumnInfo(name = AppDatabase.UPLOAD)
    var isUpload: Boolean
) {

    companion object {
        const val ID = "id_order_promo"
        const val PROMO = "total_promo"

        const val DB_NAME = "order_promo"

        fun newPromo(
            orderNo: String,
            promo: Promo,
            totalPromo: Long
        ) = OrderPromo(
            id = 0L,
            orderNO = orderNo,
            idPromo = promo.id,
            totalPromo = totalPromo,
            isUpload = false
        )
    }
}
