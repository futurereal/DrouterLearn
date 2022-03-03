package com.viomi.ovensocommon.db;

/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.viomi.common.ApplicationUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is the backend. The database. This used to be done by the OpenHelper.
 * The fact that this has very few comments emphasizes its coolness.  In a real
 * app, consider exporting the schema to help you with migrations.
 */

@Database(entities = {VideoInfo.class, MessageEntity.class, UserInfoDb.class, CustomModeEntity.class, RecommendRecipe.class}, version = 4, exportSchema = true)
public abstract class ViomiRoomDatabase extends RoomDatabase {
    private static final String TAG = "ViomiRoomDatabase";
    public static final String DATABASE_NAME = "viomi_database";

    public abstract MessageInfoDao messageInfoDao();

    public abstract VideoInfoDao videoInfo();

    public abstract UserInfoDao userInfoDao();

    public abstract CustomModeDao customModeDao();

    public abstract RecommendRecipeDao recommendRecipeDao();

    // marking the instance as volatile to ensure atomic access to the variable
    private static volatile ViomiRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static ViomiRoomDatabase getDatabase() {
        if (INSTANCE == null) {
            synchronized (ViomiRoomDatabase.class) {
                if (INSTANCE == null) {
                    Log.i(TAG, "getDatabase: createInstance");
                    INSTANCE = Room.databaseBuilder(ApplicationUtils.getContext(),
                            ViomiRoomDatabase.class, DATABASE_NAME)
                            .addCallback(RoomDatabaseCallback)
                            .addMigrations()
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Override the onCreate method to populate the database.
     * For this sample, we clear the database every time it is created.
     */
    private static final Callback RoomDatabaseCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                ViomiRoomDatabase currentDatabase = getDatabase();
//                currentDatabase.customModeDao().delete();
                Log.i(TAG, "onCreate: update origin data");
//                currentDatabase.databaseCreated.postValue(true)
            });
        }
    };
}
