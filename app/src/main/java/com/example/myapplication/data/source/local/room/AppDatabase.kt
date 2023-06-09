package com.example.myapplication.data.source.local.room

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myapplication.data.source.local.entity.room.bridge.*
import com.example.myapplication.data.source.local.entity.room.master.*

@Database(
    entities = [
        Category::class,
        ReservesCategory::class,
        Customer::class,
        Order::class,
        Purchase::class,
        PurchaseProduct::class,
        Payment::class,
        Product::class,
        Variant::class,
        Outcome::class,
        Supplier::class,
        User::class,
        OrderDetail::class,
        OrderPayment::class,
        OrderProductVariant::class,
        OrderProductVariantMix::class,
        OrderMixProductVariant::class,
        VariantMix::class,
        VariantProduct::class,
        VariantOption::class,
        Store::class,
        Promo::class,
        OrderPromo::class,
        Reserves::class
               ],
    version = 1,
    /*autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ]

     */
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun posDao(): POSDao
    abstract fun paymentsDao(): PaymentsDao
    abstract fun suppliersDao(): SuppliersDao
    abstract fun customersDao(): CustomersDao
    abstract fun variantsDao(): VariantsDao
    abstract fun variantOptionsDao(): VariantOptionsDao
    abstract fun categoriesDao(): CategoriesDao
    abstract fun reservecategoriesDao(): ReservesCategoryDao
    abstract fun outcomesDao(): OutcomesDao
    abstract fun productsDao(): ProductsDao
    abstract fun productVariantsDao(): ProductVariantsDao
    abstract fun purchasesDao(): PurchasesDao
    abstract fun ordersDao(): OrdersDao
    abstract fun variantMixesDao(): VariantMixesDao
    abstract fun storeDao(): StoreDao
    abstract fun promoDao(): PromosDao
    abstract fun reservesDao(): ReserveDao

    companion object {

        const val DB_NAME = "pos_db"
        const val UPLOAD = "upload"
        const val MAIN = "main"

        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `${Promo.DB_NAME}` (`${Promo.ID}` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `${Promo.NAME}` TEXT NOT NULL, " +
                            "`${Promo.DESC}` TEXT NOT NULL, `${Promo.CASH}` INTEGER DEFAULT 0 NOT NULL, `${Promo.VALUE}` INTEGER," +
                            "`${Promo.STATUS}` INTEGER DEFAULT 0 NOT NULL, `${UPLOAD}` INTEGER DEFAULT 0 NOT NULL)"
                )
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_promo_id_promo` ON `${Promo.DB_NAME}` (`${Promo.ID}`)")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        DB_NAME
                    )
                        .addMigrations(MIGRATION_4_5)
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE!!
        }
    }
}

