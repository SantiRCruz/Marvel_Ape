package com.example.mapes.data.localdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mapes.data.localdb.character.CharacterDao
import com.example.mapes.data.localdb.favorite.FavoriteDao
import com.example.marvelapes.data.localdb.user.UserDao
import com.example.mapes.data.models.entities.character.CharacterEntity
import com.example.mapes.data.models.entities.favorite.FavoriteEntity
import com.example.marvelapes.data.models.entities.user.UserEntity

@Database(
    entities = [
        UserEntity::class,
        CharacterEntity::class,
        FavoriteEntity::class,
               ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun UserDao(): UserDao
    abstract fun CharacterDao(): CharacterDao
    abstract fun FavoriteDao(): FavoriteDao

    companion object {

        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            INSTANCE = INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "marvel_database"
            ).build()
            return INSTANCE!!
        }

    }
}