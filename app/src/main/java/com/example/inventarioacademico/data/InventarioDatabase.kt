package com.example.inventarioacademico.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Equipo::class, Prestamo::class], version = 2, exportSchema = false)
abstract class InventarioDatabase : RoomDatabase() {

    abstract fun inventarioDao(): InventarioDao

    companion object {
        @Volatile
        private var INSTANCE: InventarioDatabase? = null

        fun getDatabase(context: Context): InventarioDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InventarioDatabase::class.java,
                    "inventario_database"
                )
                    .fallbackToDestructiveMigration() // Elimina y recrea la BD si cambias la estructura en el futuro
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}