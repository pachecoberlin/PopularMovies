package de.pacheco.popularmovies.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class MovieDB extends RoomDatabase {
    private static final String DATABASE_NAME = "popularMovies";
    private static final Object LOCK = new Object();
    private static MovieDB sInstance;

    public static MovieDB getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(), MovieDB.class,
                        MovieDB.DATABASE_NAME).build();
            }
        }
        return sInstance;
    }

    public abstract MovieDAO moviesDAO();
}
