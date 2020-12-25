package com.stupidtree.hita.data.source.web.eas

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.*
import com.stupidtree.hita.data.model.eas.CourseItem
import com.stupidtree.hita.data.model.eas.EASException
import com.stupidtree.hita.data.model.eas.EASToken
import com.stupidtree.hita.data.model.eas.TermItem
import com.stupidtree.hita.data.source.web.service.EASService
import com.stupidtree.hita.ui.base.DataState
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*

class EASource internal constructor() : EASService {
    private val defaultRequestHeader: MutableMap<String, String>
    private val timeout = 5000
    private val hostName = "http://jw.hitsz.edu.cn"


    /**
     * 登录
     */
    override fun login(
        username: String,
        password: String,
        code: String?
    ): LiveData<DataState<EASToken>> {
        val res = MutableLiveData<DataState<EASToken>>()
        Thread {
            try {
                val hc = Jsoup.connect("$hostName/cas").headers(defaultRequestHeader)
                val cookies = HashMap(hc.execute().cookies())
                val lt: String
                val execution: String
                val eventId: String
                val d = hc.cookies(cookies).get()
                lt = d.select("input[name=lt]").first().attr("value")
                execution = d.select("input[name=execution]").first().attr("value")
                eventId = d.select("input[name=_eventId]").first().attr("value")
                val c2 =
                    Jsoup.connect("https://sso.hitsz.edu.cn:7002/cas/login?service=http%3A%2F%2Fjw.hitsz.edu.cn%2FcasLogin")
                        .cookies(cookies)
                        .headers(defaultRequestHeader)
                        .ignoreContentType(true)
                val page = c2.cookies(cookies)
                    .data("username", username)
                    .data("password", password)
                    .data("lt", lt)
                    .data("rememberMe", "on")
                    .data("execution", execution)
                    .data("_eventId", eventId).post()
                cookies.putAll(c2.response().cookies())
                val login = page.toString().contains("qxdm")
                if (login) {
                    val easToken = EASToken() //登录成功，创建tokrn
                    easToken.cookies = cookies //设置cookies
                    res.postValue(DataState(easToken, DataState.STATE.SUCCESS))
                } else {
                    res.postValue(DataState(DataState.STATE.FETCH_FAILED))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                res.postValue(DataState(DataState.STATE.FETCH_FAILED))
            }
        }.start()
        return res
    }

    /**
     * 获取学年学期
     */
    override fun getAllTerms(token: EASToken): LiveData<DataState<List<TermItem>>> {
        val res = MutableLiveData<DataState<List<TermItem>>>()
        Thread {
            val terms = arrayListOf<TermItem>()
            try {
                val s = Jsoup.connect("http://jw.hitsz.edu.cn/component/queryXnxq")
                    .cookies(token.cookies)
                    .headers(defaultRequestHeader)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .post()
                val json = s.getElementsByTag("body").first().text()
                val jsonList = JsonParser().parse(json).asJsonObject["content"].asJsonArray
                for (je in jsonList) {
                    val m: MutableMap<String, String> = HashMap()
                    for ((key, value) in je.asJsonObject.entrySet()) {
                        m[key.replace("\"".toRegex(), "")] =
                            value.toString().replace("\"".toRegex(), "")
                    }
                    Log.e("map", m.toString());
                    val term = m["xn"]?.let { m["xnmc"]?.let { it1 ->
                        m["xq"]?.let { it2 ->
                            m["xqmc"]?.let { it3 ->
                                TermItem(it,
                                    it1, it2, it3
                                )
                            }
                        }
                    } }
                    term?.let { terms.add(it) }
                }
                res.postValue(DataState(terms, DataState.STATE.SUCCESS))
            } catch (e: IOException) {
                res.postValue(DataState(DataState.STATE.FETCH_FAILED))
            }
        }.start()
        return res;
    }

    /**
     * 获取学期开始日期
     */
    override fun getStartDate(token: EASToken, term: TermItem): LiveData<DataState<Calendar>> {
        var res = MutableLiveData<DataState<Calendar>>()
        Thread{
            val s = Jsoup.connect("http://jw.hitsz.edu.cn/Xiaoli/queryMonthList")
                .timeout(timeout)
                .cookies(token.cookies)
                .headers(defaultRequestHeader)
                .header("X-Requested-With", "XMLHttpRequest")
                .data("zyw", "zh")
                .data("pxn", term.yearCode)
                .data("pxq", term.termCode)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .post()
            try {
                val json = s.getElementsByTag("body").text()
                val monthList = JsonParser().parse(json).asJsonObject["monlist"].asJsonArray
                if (monthList.size() == 0) throw EASException.newDialogMessageExpection("该学期尚未开放！")
                val firstMon = monthList[0].asJsonObject
                val year = firstMon["yy"].asInt
                val month = firstMon["mm"].asInt
                val firstMonDays = firstMon["dszlist"].asJsonArray
                var i: Int
                i = 0
                while (i < firstMonDays.size()) {
                    val aWeek = firstMonDays[i].asJsonObject
                    val attr = aWeek["xldjz"]
                    if (attr != JsonNull.INSTANCE) break
                    i++
                }
                val result = Calendar.getInstance()
                result[Calendar.YEAR] = year
                result[Calendar.MONTH] = month - 1
                result[Calendar.WEEK_OF_MONTH] = i + 1
                result[Calendar.DAY_OF_WEEK] = Calendar.MONDAY
                res.postValue(DataState(result))
            } catch (e: Exception) {
                res.postValue(DataState(DataState.STATE.FETCH_FAILED,e.message))
            }
        }.start()
        return res
    }

    override fun getTimetableOfTerm(
        term: TermItem,
        token: EASToken
    ): LiveData<DataState<List<CourseItem>>> {
        return MutableLiveData()
    }

    init {
        defaultRequestHeader = HashMap()
        defaultRequestHeader["Accept"] = "*/*"
        defaultRequestHeader["Connection"] = "keep-alive"
        defaultRequestHeader["User-Agent"] =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36"
    }
}