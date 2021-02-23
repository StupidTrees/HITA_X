package com.stupidtree.hita.data.source.web

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stupidtree.hita.data.source.web.service.TeacherService
import com.stupidtree.hita.ui.base.DataState
import com.stupidtree.hita.ui.search.teacher.TeacherSearched
import com.stupidtree.hita.utils.HTMLUtils
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.util.*

object TeacherWebSource : TeacherService {
    override fun getTeacherProfile(
        teacherId: String,
        teacherUrl: String
    ): LiveData<DataState<Map<String, String>>> {
        val result = MutableLiveData<DataState<Map<String, String>>>()
        Thread {
            try {
                val teacherProfile = mutableMapOf<String, String>()
                val d: Document = Jsoup.connect("http://faculty.hitsz.edu.cn/$teacherUrl")
                    .header(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36"
                    )
                    .data("id", teacherId)
                    .get()
                val description: String = HTMLUtils.getStringValueByClass(d, "user-describe")
                val post: String = HTMLUtils.getStringValueByClass(d, "user-post")
                val label: String = HTMLUtils.getStringValueByClass(d, "user-label")
                val position: String = HTMLUtils.getStringValueByClass(d, "user-position")
                teacherProfile["description"] = description
                teacherProfile["post"] = post
                teacherProfile["label"] = label
                teacherProfile["position"] = position
                for (e in HTMLUtils.getElementsInClassByTag(d, "cont", "li")) {
                    val text = e.text()
                    when {
                        text.contains("电话") -> teacherProfile["phone"] = text.replace(
                            "电话".toRegex(),
                            ""
                        )
                        text.contains("地址") -> teacherProfile["address"] = text.replace(
                            "地址".toRegex(),
                            ""
                        )
                        text.contains("邮箱") -> teacherProfile["email"] =
                            text.replace("邮箱".toRegex(), "")
                    }
                }
                result.postValue(DataState(teacherProfile.toMap()))
            } catch (e: Exception) {
                result.postValue(DataState(DataState.STATE.FETCH_FAILED))
            }
        }.start()
        return result
    }

    override fun getTeacherPages(teacherId: String): LiveData<DataState<Map<String, String>>> {
        val res = MutableLiveData<DataState<Map<String, String>>>()
        Thread {
            try {
                val infoToAdd = mutableMapOf<String, String>()
                val teachersPage: Document =
                    Jsoup.connect("http://faculty.hitsz.edu.cn/TeacherHome/teacherBody.do")
                        .header(
                            "User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36"
                        )
                        .header("X-Requested-With", "XMLHttpRequest")
                        .data("id", teacherId)
                        .post()
                //System.out.println(teachersPage);
                val tabs = teachersPage.getElementsByAttributeValueContaining("data-class", "tab")
                    .select("li")
                for (e in tabs) {
                    if (!e.toString().contains("ptaben") && !e.toString().contains("pTabEn")) {
                        val id = e.attr("data-class")
                        val part = teachersPage.getElementById(id)
                        if (part != null && part.getElementsByTag("table").size > 0) {
                            infoToAdd[e.text()] = part.getElementsByTag("table").first().toString()
                        }
                    }
                }
                res.postValue(DataState(infoToAdd))
            } catch (e: Exception) {
                res.postValue(DataState(DataState.STATE.FETCH_FAILED))
            }
        }.start()
        return res
    }

    override fun searchTeachers(text: String): LiveData<DataState<List<TeacherSearched>>> {
        val result = MutableLiveData<DataState<List<TeacherSearched>>>()
        Thread {
            val txt = text.replace("老师|教师|teacher|Teacher|先生|女士".toRegex(), "")
            val res: MutableList<TeacherSearched> = mutableListOf()
            if (!TextUtils.isEmpty(txt)) {
                val keys = txt.split("[,，]".toRegex()).toTypedArray()
                for (k in keys) {
                    if(k.isEmpty()) continue
                    var d: Document?
                    d = try {
                        Jsoup.connect("http://faculty.hitsz.edu.cn/hompage/findTeachersByName.do")
                            .cookies(mapOf())
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.87 Safari/537.36")
                            .data("userName", k)
                            .data("X-Requested-With", "XMLHttpRequest").post()
                    } catch (e: IOException) {
                        result.postValue(DataState(DataState.STATE.FETCH_FAILED))
                        return@Thread
                    }
                    try {
                        val json = d?.getElementsByTag("body")?.text()
                        val jo = json?.let { JSONObject(json) }
                        val ja = jo?.getJSONArray("rows")
                        ja?.let{ it ->
                            for (i in 0 until it.length()) {
                                val teacher = it.getJSONObject(i)
                                val obj = TeacherSearched()
                                obj.name =  teacher.optString("userName")
                                obj.id = teacher.optString("id")
                                obj.url =teacher.optString("url")
                                obj.department = teacher.optString("department")
                                res.add(obj)
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        result.postValue(DataState(DataState.STATE.FETCH_FAILED))
                        return@Thread
                    }
                }
                result.postValue(DataState(res))
            }else{
                result.postValue(DataState(DataState.STATE.NOTHING))
            }
        }.start()
        return result
    }
}