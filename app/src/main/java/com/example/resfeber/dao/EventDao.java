package com.example.resfeber.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.resfeber.model.Event;

import java.util.List;

@Dao
public interface EventDao {

    @Query("SELECT * FROM events")
    List<Event> selectAll();

    @Query("SELECT * FROM events WHERE title_event LIKE :name  || '%'")
    List<Event> selectWithTitle(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEvent(Event event);

    @Query("DELETE FROM events")
    void deleteData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Event> eventList);
}
