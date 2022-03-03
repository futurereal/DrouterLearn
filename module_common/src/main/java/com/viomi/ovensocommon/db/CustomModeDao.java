package com.viomi.ovensocommon.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Observable;

/**
 * @description:
 * @data:2021/9/22
 */
@Dao
public interface CustomModeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CustomModeEntity customModeEntity);

    @Delete
    void delete(CustomModeEntity customModeEntity);

    @Query("SELECT * FROM tb_custom_mode")
    Observable<List<CustomModeEntity>> getAllMode();

    @Query("SELECT * FROM tb_custom_mode  WHERE :indexName = index_name")
    CustomModeEntity getModesByIndexName(String indexName);

    //删全部
    @Query("DELETE FROM tb_custom_mode")
    void deleteAll();
}
