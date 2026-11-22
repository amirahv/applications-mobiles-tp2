package com.example.evenementsavmym.donnees.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.evenementsavmym.modele.Evenement

@Database(
    entities = [Evenement::class],
    version = 1,
    exportSchema = false
)
abstract class BaseEvenements : RoomDatabase() {

    abstract fun evenementDao(): EvenementDao

    companion object {
        @Volatile
        private var instance: BaseEvenements? = null

        fun obtenirInstance(context: Context): BaseEvenements {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    BaseEvenements::class.java,
                    "evenements.db"
                ).build().also {
                    instance = it
                }
            }
        }
    }
}