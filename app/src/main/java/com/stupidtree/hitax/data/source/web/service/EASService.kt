package com.stupidtree.hitax.data.source.web.service

import androidx.lifecycle.LiveData
import com.stupidtree.component.data.DataState
import com.stupidtree.hitax.data.model.eas.CourseItem
import com.stupidtree.hitax.data.model.eas.CourseScoreItem
import com.stupidtree.hitax.data.model.eas.EASToken
import com.stupidtree.hitax.data.model.eas.TermItem
import com.stupidtree.hitax.data.model.timetable.TermSubject
import com.stupidtree.hitax.data.model.timetable.TimePeriodInDay
import com.stupidtree.hitax.ui.eas.classroom.BuildingItem
import com.stupidtree.hitax.ui.eas.classroom.ClassroomItem
import java.util.*

interface EASService {
    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @param code 验证码，可空
     * @return 登录结果
     */
    fun login(username: String, password: String, code: String?): LiveData<DataState<EASToken>>

    /**
     * 检查登录状态
     */
    fun loginCheck(token:EASToken):LiveData<DataState<Boolean>>

    /**
     * 获取学年学期
     * @param token 登录凭证
     */
    fun getAllTerms(token: EASToken):LiveData<DataState<List<TermItem>>>;

    /**
     * 获取学年学期开始日期
     */
    fun getStartDate(token: EASToken,term:TermItem):LiveData<DataState<Calendar>>;

    /**
     * 获取某学期的已选课程
     */
    fun getSubjectsOfTerm(token: EASToken, term: TermItem): LiveData<DataState<MutableList<TermSubject>>>
    /**
     * 获取个人总课表
     * @param term 学期
     * @param token 登录凭证
     */
    fun getTimetableOfTerm(term:TermItem, token: EASToken):LiveData<DataState<List<CourseItem>>>


    /**
     * 获取某学期的课表结构
     */
    fun getScheduleStructure(term: TermItem,token: EASToken):LiveData<DataState<MutableList<TimePeriodInDay>>>


    /**
     * 获取教学楼列表
     */
    fun getTeachingBuildings(token: EASToken):LiveData<DataState<List<BuildingItem>>>

    /**
     * 查询空教室
     */
     fun queryEmptyClassroom(
        token: EASToken,
        term: TermItem,
        building: BuildingItem,
        weeks: List<String>
    ): LiveData<DataState<List<ClassroomItem>>>

    /**
     * 获取最终成绩
     */
     fun getPersonalScores(
         term: TermItem,
         token: EASToken,
         testType: TestType
     ):LiveData<DataState<List<CourseScoreItem>>>

    enum class TestType(val value:String){
        ALL("-1"), NORMAL("0"), RESIT("1"), RETAKE("2")
    }
}