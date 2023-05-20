package com.example.myapplication.data.source.local.entity.room.master

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.myapplication.data.source.local.room.AppDatabase.Companion.UPLOAD
import com.example.myapplication.data.source.remote.response.helper.RemoteClassUtils
import com.example.myapplication.view.ui.DropdownItem
import com.google.firebase.firestore.QuerySnapshot
import java.io.Serializable


@Entity(
    tableName = ReservesCategory.DB_NAME,
    indices = [
        Index(value = [ReservesCategory.ID])
    ]
)
data class ReservesCategory(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    var id: Long,

    @ColumnInfo(name = NAMECATRES)
    override var name: String,

    @ColumnInfo(name = DESCRESCAT)
    var desc: String,

    @ColumnInfo(name = STOCKRES)
    var isStock: Boolean,

    @ColumnInfo(name = STATUSRESCAT)
    var isActive: Boolean,

    @ColumnInfo(name = UPLOAD)
    var isUploaded: Boolean,

    ): Serializable, DropdownItem{

    fun isNewReservesCategory() = id == 0L

    companion object : RemoteClassUtils<ReservesCategory> {
        const val ID = "id_reserves_category"
        const val STATUSRESCAT = "status_cat_res"
        const val STOCKRES = "stock_res"
        const val NAMECATRES = "name"
        const val DESCRESCAT = "desc"

        const val DB_NAME = "category_reserves"

        const val ALL = 2
        const val ACTIVE = 1

        fun getFilterCarRes(state: Int, isStock: Boolean): SimpleSQLiteQuery {
            return filtercr(state, isStock)
        }

        fun getFilterCarRees(state: Int): SimpleSQLiteQuery {
            return filtercr(state, false)
        }

        fun createNewReservesCategory(
            name_cat_res: String,
            desc_cat_res: String
        ) = ReservesCategory(
            name_cat_res = name_cat_res,
            desc_cat_res = desc_cat_res,
            isStock = false,
            isActive = true

        )

        private fun filtercr(state: Int, isStock: Boolean): SimpleSQLiteQuery {
            val queryres = StringBuilder().append("SELECT * FROM ")
            queryres.append(DB_NAME)
            when (state) {
                ACTIVE ->{
                    queryres.append(" WHERE ")
                        .append(STATUSRESCAT)
                        .append(" = ")
                        .append(ACTIVE)
                    if(isStock){
                        queryres.append(" AND ")
                            .append(STOCKRES)
                            .append(" = ")
                            .append("1")
                    }
                }
            }
            return SimpleSQLiteQuery(queryres.toString())
        }

        override fun toHashMap(data: ReservesCategory): HashMap<String, Any?> {
            return hashMapOf(
                ID to data.id,
                NAMECATRES to data.name,
                DESCRESCAT to data.desc,
                STOCKRES to data.isStock,
                STATUSRESCAT to data.isActive,
                UPLOAD to data.isUploaded
            )
        }

        override fun toListClass(result: QuerySnapshot): List<ReservesCategory> {
            val array: ArrayList<ReservesCategory> = ArrayList()
            for (document in result){
                val reservesCategory = ReservesCategory(
                    document.data[ID] as Long,
                    document.data[NAMECATRES] as String,
                    document.data[DESCRESCAT] as String,
                    document.data[STOCKRES] as Boolean,
                    document.data[STATUSRESCAT] as Boolean,
                    document.data[UPLOAD] as Boolean
                )
                array.add(reservesCategory)
            }
            return array
        }
    }




    @Ignore
    constructor(
        id: Long,
        name: String,
        desc: String,
        isStock: Boolean,
        isActive: Boolean
    ) : this(id, name, desc, isStock, isActive, false)

    @Ignore
    constructor(name_cat_res: String, desc_cat_res: String, isStock: Boolean, isActive: Boolean) : this(
        0,
        name_cat_res,
        desc_cat_res,
        isStock,
        isActive,
        false
    )


}