package com.ana.translator.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.ana.translator.model.Favorites;

import java.util.List;

@Dao
public interface querying {

    @androidx.room.Query("Select * from Favorites")
    LiveData<List<Favorites>> getAll();

    @Insert
    void addNewfavorites(Favorites... histories);

    @Delete
    void deletefavorites(Favorites... histories);
}
