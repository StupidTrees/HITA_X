package com.stupidtree.hitax.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.hitax.data.AppDatabase
import com.stupidtree.hitax.data.model.timetable.TermSubject
import com.stupidtree.hitax.ui.timetable.detail.TeacherInfo
import com.stupidtree.hitax.utils.ColorTools

class SubjectRepository(application: Application) {
    private val eventItemDao = AppDatabase.getDatabase(application).eventItemDao()
    private val subjectDao = AppDatabase.getDatabase(application).subjectDao()


    /**
     * 获取所有科目及其进度
     */
    fun getSubjects(timetableId: String): LiveData<List<TermSubject>> {
        return subjectDao.getSubjects(timetableId)
    }


    fun getSubjectById(subjectId: String): LiveData<TermSubject> {
        return subjectDao.getSubjectById(subjectId)
    }

    fun getSubjectColors(subjectId: String): LiveData<TermSubject> {
        return subjectDao.getSubjectById(subjectId)
    }

    fun getTeachersInfo(timetableId: String): LiveData<MutableList<TeacherInfo>> {
        return Transformations.map(eventItemDao.getTeachersOfTimetable(timetableId)) {
            val result = mutableListOf<TeacherInfo>()
            val map = mutableMapOf<String, TeacherInfo?>()
            for (t in it) {
                if (map[t.name] == null) {
                    t.name?.let { it2 -> map[it2] = t }
                } else {
                    map[t.name]?.subjectName = map[t.name]?.subjectName + " " + t.subjectName
                }
            }
            for (x in map.values) {
                x?.let { it1 -> result.add(it1) }
            }
            return@map result
        }
    }

    fun getTeachersOfSubject(timetableId: String, subjectId: String): LiveData<List<String>> {
        return eventItemDao.getTeachersOfSubject(timetableId, subjectId)
    }

    /**
     * 动作：保存科目信息
     */
    fun actionSaveSubjectInfo(subject: TermSubject) {
        Thread {
            subjectDao.saveSubjectSync(subject)
        }.start()
    }

    fun actionResetSubjectColors(timetableId: String) {
        Thread {
            val subjects = subjectDao.getSubjectsSync(timetableId)
            for (s in subjects) {
                s.color = ColorTools.randomColorMaterial()
            }
            subjectDao.saveSubjectsSync(subjects)
        }.start()
    }


    /**
     * 动作：删除科目及其事件
     */
    fun actionDeleteSubjects(subjects:List<TermSubject>){
        Thread{
            subjectDao.deleteSubjectsSync(subjects)
            val ids = mutableListOf<String>()
            for(s in subjects){
                ids.add(s.id)
            }
            eventItemDao.deleteEventsFromSubjectsSync(ids)
        }.start()
    }

    /**
     * 计算某一科目的进度
     * @return pair.first = 已完成数目,pair.second = 总数目
     *
     */
    fun getProgressOfSubject(subjectId: String, ts: Long): LiveData<Pair<Int, Int>> {
        val res = MediatorLiveData<Pair<Int, Int>>()
        res.addSource(eventItemDao.countClassesOfSubject(subjectId)) { total ->
            res.addSource(eventItemDao.countClassesBeforeTimeOfSubject(subjectId, ts)) { finished ->
                res.value = Pair(finished, total)
            }
        }
        return res
    }

    companion object {
        private var instance: SubjectRepository? = null
        fun getInstance(application: Application): SubjectRepository {
            if (instance == null) instance = SubjectRepository(application)
            return instance!!
        }
    }
}