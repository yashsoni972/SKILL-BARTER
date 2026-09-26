package com.yashsoni.skillbarter.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {UserEntity.class, MessageEntity.class}, version = 1, exportSchema = false)
public abstract class SkillBarterDatabase extends RoomDatabase {
    private static volatile SkillBarterDatabase INSTANCE;

    public abstract SkillBarterDao skillBarterDao();

    public static SkillBarterDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SkillBarterDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SkillBarterDatabase.class, "skill_barter_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
