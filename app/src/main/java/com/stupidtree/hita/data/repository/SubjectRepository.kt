package com.stupidtree.hita.data.repository

import android.app.Application
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stupidtree.hita.data.AppDatabase
import com.stupidtree.hita.data.model.timetable.TermSubject
import com.stupidtree.hita.ui.timetable.subject.TeacherInfo
import com.stupidtree.hita.utils.ColorBox
import com.stupidtree.hita.utils.TimeUtils

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
                s.color = ColorBox.randomColorMaterial()
            }
            subjectDao.saveSubjectsSync(subjects)
        }.start()
    }

    /**
     * 计算某一科目的进度
     * @return pair.first = 已完成数目,pair.second = 总数目
     *
     */
    fun getProgressOfSubject(subjectId: String, ts: Long): LiveData<Pair<Int, Int>> {
        val res = MutableLiveData<Pair<Int, Int>>()
        Thread {
            val total = eventItemDao.countClassesOfSubjectSync(subjectId)
            val finished = eventItemDao.countClassesBeforeTimeOfSubjectSync(
                subjectId,
                ts
            )
            res.postValue(Pair(finished, total))
        }.start()
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