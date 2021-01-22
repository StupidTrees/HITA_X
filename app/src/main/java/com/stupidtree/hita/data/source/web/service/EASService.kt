package com.stupidtree.hita.data.source.web.service

import androidx.lifecycle.LiveData
import com.stupidtree.hita.data.model.eas.CourseItem
import com.stupidtree.hita.data.model.eas.EASToken
import com.stupidtree.hita.data.model.eas.TermItem
import com.stupidtree.hita.data.model.timetable.TermSubject
import com.stupidtree.hita.data.model.timetable.TimePeriodInDay
import com.stupidtree.hita.ui.base.DataState
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


}