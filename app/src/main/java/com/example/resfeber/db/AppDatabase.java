package com.example.resfeber.db;

import androidx.room.RoomDatabase;

import com.example.resfeber.dao.EventDao;
import com.example.resfeber.model.Event;

@androidx.room.Database(version = 1, entities = {Event.class}, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EventDao eventDao();
}
