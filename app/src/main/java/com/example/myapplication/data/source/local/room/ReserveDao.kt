package com.example.myapplication.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.myapplication.data.source.local.entity.room.helper.ReservesWithCategory
import com.example.myapplication.data.source.local.entity.room.master.Reserves
import com.example.myapplication.data.source.local.entity.room.master.ReservesCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ReserveDao {
    @RawQuery(observedEntities = [Reserves::class])
    fun getReserves(query: SupportSQLiteQuery): Flow<List<Reserves>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReserves(data: Reserves): Long

    @Update
    fun updateReserves(data: Reserves)

    @Query("SELECT * FROM ${Reserves.DB_NAME} WHERE ${Reserves.ID} = :ReservesId")
    suspend fun getReserves(ReservesId: Long): Reserves

    @Query("SELECT * FROM ${Reserves.DB_NAME} WHERE ${Reserves.ID} = :ReservesId")
    fun getReservesAsFlow(ReservesId: Long): Flow<Reserves>


    @Transaction
    @Query("SELECT * FROM ${Reserves.DB_NAME} WHERE ${ReservesCategory.ID} = :reservesCategory")
    fun getReservesWithCategories(reservesCategory: Long): Flow<List<ReservesWithCategory>>

    @Transaction
    @Query("SELECT * FROM ${Reserves.DB_NAME} WHERE ${Reserves.ID} = :reservesId")
    fun getReservesWithCategory(reservesId: Long): Flow<ReservesWithCategory?>

    @Transaction
    @Query("SELECT * FROM ${Reserves.DB_NAME}")
    fun getAllReservesWithCategories(): Flow<List<ReservesWithCategory>>

    @Query("UPDATE ${Reserves.DB_NAME} SET ${Reserves.MEASURE} " +
            "= ((SELECT ${Reserves.MEASURE} FROM ${Reserves.DB_NAME}" +
            " WHERE ${Reserves.ID} = :ReservesId)+ :measure)" +
            " WHERE ${Reserves.ID} = :ReservesId")
    fun increaseReservesMeassure(ReservesId: Long, measure: Long)

    @Query("UPDATE ${Reserves.DB_NAME} SET ${Reserves.QUANTITY} = ((SELECT ${Reserves.QUANTITY}" +
            " FROM ${Reserves.DB_NAME} WHERE ${Reserves.ID} = :ReservesId) - :quantity)" +
            " WHERE ${Reserves.ID} = :ReservesId")
    fun  decreaseReservesQuantity(ReservesId: Long, quantity : String)


}