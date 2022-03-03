package com.viomi.ovensocommon.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @data:2021/9/22
 */
@Dao
public interface MessageInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(MessageEntity messageEntity);

    @Delete
    int deleteMessage(MessageEntity messageEntity);

    @Query("SELECT * FROM tb_message")
    List<MessageEntity> getMessageList();

    @Query("SELECT * FROM tb_message where video_index = :videoIndex")
    MessageEntity getMessageByVideoIndex(String videoIndex);

    /*@Delete
    void deleteVideoInfo(MessageEntity messageEntity);*/

}
