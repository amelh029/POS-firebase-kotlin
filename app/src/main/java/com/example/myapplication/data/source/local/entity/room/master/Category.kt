package com.example.myapplication.data.source.local.entity.room.master

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.myapplication.data.source.local.room.AppDatabase.Companion.UPLOAD
import com.example.myapplication.data.source.remote.response.helper.RemoteClassUtils
import com.example.myapplication.view.ui.DropdownItem
import com.google.firebase.firestore.QuerySnapshot
import java.io.Serializable


@Entity(
    tableName = Category.DB_NAME,
    indices = [
        Index(value = [Category.ID])
    ]
)
data class Category(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    var id: Long,

    @ColumnInfo(name = NAME)
    override var name: String,

    @ColumnInfo(name = DESC)
    var desc: String,

    @ColumnInfo(name = STOCK)
    var isStock: Boolean,

    @ColumnInfo(name = STATUS)
    var isActive: Boolean,

    @ColumnInfo(name = UPLOAD)
    var isUploaded: Boolean
) : Serializable, DropdownItem {

    fun isNewCategory() = id == 0L
    companion object : RemoteClassUtils<Category> {
        const val ID = "id_category"
        const val STATUS = "status"
        const val STOCK = "stock"
        const val NAME = "name"
        const val DESC = "desc"

        const val DB_NAME = "category"

        const val ALL = 2
        const val ACTIVE = 1

        fun getFilter(state: Int, isStock: Boolean): SimpleSQLiteQuery {
            return filter(state, isStock)
        }

        fun getFilter(state: Int): SimpleSQLiteQuery {
            return filter(state, false)
        }

        fun createNewCategory(
            name: String,
            desc: String
        ) = Category(
            name = name,
            desc = desc,
            isStock = false,
            isActive = true
        )

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

        override fun toHashMap(data: Category): HashMap<String, Any?> {
            return hashMapOf(
                ID to data.id,
                NAME to data.name,
                DESC to data.desc,
                STOCK to data.isStock,
                STATUS to data.isActive,
                UPLOAD to data.isUploaded
            )
        }

        override fun toListClass(result: QuerySnapshot): List<Category> {
            val array: ArrayList<Category> = ArrayList()
            for (document in result) {
                val category = Category(
                    document.data[ID] as Long,
                    document.data[NAME] as String,
                    document.data[DESC] as String,
                    document.data[STOCK] as Boolean,
                    document.data[STATUS] as Boolean,
                    document.data[UPLOAD] as Boolean
                )
                array.add(category)
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
    constructor(name: String, desc: String, isStock: Boolean, isActive: Boolean) : this(
        0,
        name,
        desc,
        isStock,
        isActive,
        false
    )
}
