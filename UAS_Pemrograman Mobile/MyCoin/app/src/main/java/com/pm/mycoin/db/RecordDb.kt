package com.pm.mycoin.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Record::class, Debt::class], version = 3)
abstract class RecordDb : RoomDatabase() {
    abstract val recordDao: RecordDAO

    companion object {
        @Volatile
        private var db: RecordDb? = null

        fun getDb(application: Application): RecordDb? {
            if (db == null) {
                synchronized(RecordDb::class.java) {
                    if (db == null) {
                        db = Room.databaseBuilder(
                            application.applicationContext,
                            RecordDb::class.java, "record_db"
                        )
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }

            return db
        }
    }
}