package com.hulusimsek.a2_countries.service

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hulusimsek.a2_countries.model.Country

@Dao
interface CountryDao {

    @Insert
    suspend fun insertAll(vararg countries : Country) : List<Long>
    // Insert -> INSERT INTO
    // suspend -> coroutine, pause & resume
    // vararg -> multiplle country objects
    // List<Long> -> primary keys

    @Query("SELECT * FROM country")
    suspend fun getAllCountries() : List<Country>


    @Query("SELECT * FROM country WHERE uuid = :countryId")
    suspend fun getCountry(countryId : Int) : Country

    @Query("DELETE FROM country")
    suspend fun deleteAllCountries()
}