package com.stupidtree.hita.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Timestamp;

@Entity(tableName = "subject")
public class Subject {
    //必修-考试，必修-考察，选修-专选，选修-任选，慕课
    public enum TYPE{COM_A,COM_B,OPT_A,OPT_B,MOOC}

    @PrimaryKey
    String id;//id
    String name;//名称
    String timetableId;//所属课表的id
    TYPE type;//课程类型
    float credit;//学分
    String school;//开课院系
    boolean countInSPA;//是否计入平均学分绩

    String code;//适配教务的课程代码
    String key;//适配教务的课程标识


    Timestamp createdAt;//创建时间
    Timestamp updatedAt;//更新时间
}
