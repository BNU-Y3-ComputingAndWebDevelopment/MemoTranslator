package com.ana.translator.Database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ana.translator.model.Favorites;

@androidx.room.Database(entities = {Favorites.class},version = 1)
public abstract class Database extends RoomDatabase {
    private static Database INSTANCE;
    public abstract querying userDao();

    public static Database getLocalDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), Database.class, "favorites")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
