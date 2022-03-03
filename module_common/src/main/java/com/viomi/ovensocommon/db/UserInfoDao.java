package com.viomi.ovensocommon.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * @description:
 * @data:2021/9/22
 */
@Dao
public interface UserInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(UserInfoDb userInfoDb);

    @Delete
    int delete(UserInfoDb userInfoDb);

    @Query("SELECT * FROM tb_userinfo")
    UserInfoDb getUserInfo();
}
