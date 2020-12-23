package com.stupidtree.hita.data.model.timetable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Timestamp;

@Entity(tableName = "timetable")
public class Timetable {
    @PrimaryKey
    String id;
    String name;//课表名称

    String key;//适配教务的课表key
    String code;//适配教务的课表code

    Timestamp startTime;//开始时间
    Timestamp endTime;//结束时间

    Timestamp createdAt;//创建时间
    Timestamp updatedAt;//更新时间
}
