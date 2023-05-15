package com.example.myapplication.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.myapplication.data.source.local.entity.room.master.Reserves
import kotlinx.coroutines.flow.Flow

@Dao
interface ReserveDao {
    @RawQuery(observedEntities = [Reserves::class])
    fun getReserves(query: SupportSQLiteQuery): Flow<List<Reserves>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insetReserves(data: Reserves)

    @Update
    fun updateReserves(data: Reserves)

    @Query("SELECT * FROM ${Reserves.DB_NAME} WHERE ${Reserves.ID} = :ReservesId")
    suspend fun getReserves(ReservesId: Long): Reserves

    @Query("SELECT * FROM ${Reserves.DB_NAME} WHERE ${Reserves.ID} = :reservesId")
    fun getReservesAsFlow(reservesId: Long): Flow<Reserves>

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