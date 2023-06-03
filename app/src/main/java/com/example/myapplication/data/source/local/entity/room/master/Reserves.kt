package com.example.myapplication.data.source.local.entity.room.master

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.myapplication.data.source.local.entity.room.master.Category.Companion.ACTIVE
import com.example.myapplication.data.source.local.entity.room.master.Category.Companion.STATUS
import com.example.myapplication.data.source.local.entity.room.master.Category.Companion.STOCK
import com.example.myapplication.data.source.local.room.AppDatabase.Companion.UPLOAD
import com.example.myapplication.data.source.remote.response.helper.RemoteClassUtils
import com.example.myapplication.view.ui.DropdownItem
import com.google.firebase.firestore.QuerySnapshot
import java.io.Serializable


@Entity(
    tableName = Reserves.DB_NAME,
    foreignKeys = [ForeignKey(
        entity = ReservesCategory::class,
        parentColumns = [ReservesCategory.ID],
        childColumns = [ReservesCategory.ID],
        onDelete = CASCADE
    )],
    indices = [
        Index(value = [Reserves.ID]),
        Index(value = [ReservesCategory.ID])
    ]
)
data class Reserves(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    var id: Long,

    @ColumnInfo(name = NAME)
    var name: String,

    @ColumnInfo(name = ReservesCategory.ID)
    var reservesCategory: Long,

    @ColumnInfo(name = MEASURE)
    var meassure: String,

    @ColumnInfo(name = QUANTITY)
    var quantity: Long,

    @ColumnInfo(name = STATUS)
    var isActive: Boolean,


    @ColumnInfo(name = UPLOAD)
    var isUploaded: Boolean


): Serializable {
    

    companion object: RemoteClassUtils<Reserves> {
        const val ID = "id_reserves"
        const val NAME = "reserves_name"
        const val MEASURE = "measure"
        const val QUANTITY = "quantity"
        const val STATUS = "status"
        const val UPLOAD = "upload"

        const val DB_NAME = "reserves"

        const val ALL = 2
        const val ACTIVE = 1

        fun createNewReserves(
            name: String,
            reservesCategory: Long,
            measure: String,
            quantity: Long
        ) = Reserves(
            name = name,
            reservesCategory = reservesCategory,
            meassure = measure,
            quantity = quantity,
            id = 0,
            isUploaded = true,
            isActive = true
        )

        override fun toHashMap(data: Reserves): HashMap<String, Any?> {
            return hashMapOf(
                ID to data.id,
                NAME to data.name,
                ReservesCategory.ID to data.reservesCategory,
                MEASURE to data.meassure,
                QUANTITY to data.quantity,
                STATUS to data.isActive,
                UPLOAD to data.isUploaded
            )
        }

        override fun toListClass(result: QuerySnapshot): List<Reserves> {
            val array: ArrayList<Reserves> = ArrayList()
            for (document in result) {
                val reserves = Reserves(
                    document.data[ID] as Long,
                    document.data[NAME] as String,
                    document.data[ReservesCategory.ID] as Long,
                    document.data[MEASURE] as String,
                    document.data[QUANTITY] as Long,
                    document.data[STATUS] as Boolean,
                    document.data[UPLOAD] as Boolean


                )
                array.add(reserves)
            }
            return array
        }


        fun getFilter(state: Int): SimpleSQLiteQuery {
            return filter(state, false)
        }

        private fun filter(state: Int, isStock: Boolean): SimpleSQLiteQuery {
            val query = StringBuilder().append("SELECT * FROM ")
            query.append(DB_NAME)
            when (state) {
                ACTIVE -> {
                    query.append(" WHERE ")
                        .append(STATUS)
                        .append(" = ").append(ACTIVE)
                    if (isStock) {
                        query.append(" AND ")
                            .append(STOCK)
                            .append(" = ").append("1")
                    }
                }
            }
            return SimpleSQLiteQuery(query.toString())
        }
    }

    //@Ignore
    //constructor(id: Long, name: String, reservesCategory: Long, meassure: String, quantity: Long, isActive: Boolean, isUploaded: Boolean): this( id, name, reservesCategory, meassure, quantity, isActive, isUploaded, false)

}
