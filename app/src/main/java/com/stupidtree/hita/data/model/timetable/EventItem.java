package com.stupidtree.hita.data.model.timetable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Timestamp;

@Entity(tableName = "events")
public class EventItem {
    public enum TYPE{CLASS,EXAM,OTHER}
    @PrimaryKey
    String id;
    TYPE type;
    String name;//名称

    String place;//地点
    String teacher;//教师
    String subjectId;//科目id
    String timetableId;//课表的id


    Timestamp from;//开始时间
    Timestamp to;//结束时间


    Timestamp createdAt;//创建时间
    Timestamp updatedAt;//更新时间
}
