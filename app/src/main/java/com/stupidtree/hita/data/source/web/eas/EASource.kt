package com.stupidtree.hita.data.source.web.eas

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.*
import com.stupidtree.hita.data.model.eas.CourseItem
import com.stupidtree.hita.data.model.eas.EASException
import com.stupidtree.hita.data.model.eas.EASToken
import com.stupidtree.hita.data.model.eas.TermItem
import com.stupidtree.hita.data.model.timetable.TermSubject
import com.stupidtree.hita.data.model.timetable.TimeInDay
import com.stupidtree.hita.data.model.timetable.TimePeriodInDay
import com.stupidtree.hita.data.source.web.service.EASService
import com.stupidtree.hita.ui.base.DataState
import com.stupidtree.hita.utils.JsonUtils
import com.stupidtree.hita.utils.TextTools
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.io.IOException
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*
import javax.security.auth.Subject

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
                    easToken.username = username
                    easToken.password = password
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
     * 检查登录状态
     */
    override fun loginCheck(token: EASToken): LiveData<DataState<Boolean>> {
        val res = MutableLiveData<DataState<Boolean>>()
        Thread {
            try {
                val s = Jsoup.connect("$hostName/UserManager/queryxsxx")
                    .timeout(timeout)
                    .cookies(token.cookies)
                    .headers(defaultRequestHeader)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .post();
                try {
                    val json = s.getElementsByTag("body").text()
                    if (json.contains("session已失效")) {
                        res.postValue(DataState(false))
                    } else {
                        val jo = JsonParser().parse(json).asJsonObject;
                        val login = jo.has("XH")
                        res.postValue(DataState(login))
                    }
                } catch (e: JsonSyntaxException) {
                    res.postValue(DataState(false))
                }
            } catch (e: IOException) {
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
                val s = Jsoup.connect("$hostName/component/queryXnxq")
                    .cookies(token.cookies)
                    .headers(defaultRequestHeader)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .post()
                val json = s.getElementsByTag("body").first().text()
                val jsonList = try {
                    JsonParser().parse(json).asJsonObject["content"].asJsonArray
                } catch (e: Exception) {
                    null
                }
                if (jsonList != null) {
                    for (je in jsonList) {
                        val m: MutableMap<String, String> = HashMap()
                        for ((key, value) in je.asJsonObject.entrySet()) {
                            m[key.replace("\"".toRegex(), "")] =
                                    value.toString().replace("\"".toRegex(), "")
                        }
                        val term = m["xn"]?.let {
                            m["xnmc"]?.let { it1 ->
                                m["xq"]?.let { it2 ->
                                    m["xqmc"]?.let { it3 ->
                                        TermItem(
                                                it,
                                                it1, it2, it3
                                        )
                                    }
                                }
                            }
                        }

                        term?.let {
                            it.isCurrent = m["sfdqxq"] == "1"
                            terms.add(it)
                        }
                    }
                    res.postValue(DataState(terms, DataState.STATE.SUCCESS))
                }else{
                    res.postValue(DataState(DataState.STATE.NOT_LOGGED_IN))
                }

            } catch (e: IOException) {
                res.postValue(DataState(DataState.STATE.FETCH_FAILED))
            }
        }.start()
        return res
    }

    /**
     * 获取学期开始日期
     */
    override fun getStartDate(token: EASToken, term: TermItem): LiveData<DataState<Calendar>> {
        val res = MutableLiveData<DataState<Calendar>>()
        Thread {
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
                var i = 0
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
                res.postValue(DataState(DataState.STATE.FETCH_FAILED, e.message))
            }
        }.start()
        return res
    }

    /**
     * 获取课程
     */
    override fun getSubjectsOfTerm(
        token: EASToken,
        term: TermItem
    ): LiveData<DataState<MutableList<TermSubject>>> {
        val res = MutableLiveData<DataState<MutableList<TermSubject>>>()
        Thread {
            val result: MutableList<TermSubject> = ArrayList()
            try {
                val r = Jsoup.connect("http://jw.hitsz.edu.cn/Xsxk/queryYxkc")
                    .timeout(timeout)
                    .cookies(token.cookies)
                    .headers(defaultRequestHeader)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .data("p_pylx", "1")
                    .data("p_sfgldjr", "0")
                    .data("p_sfredis", "0")
                    .data("p_sfsyxkgwc", "0")
                    .data("p_xn", term.yearCode) //学年
                    .data("p_xq", term.termCode) //学期
                    .data("p_xnxq", term.getCode()) //学年学期
                    .data("p_dqxn", term.yearCode) //当前学年
                    .data("p_dqxq", term.termCode) //当前学期
                    .data("p_dqxnxq", term.getCode()) //当前学年学期
                    .data("p_xkfsdm", "yixuan") //已选
                    .data("p_sfhlctkc", "0")
                    .data("p_sfhllrlkc", "0")
                    .data("p_sfxsgwckb", "1")
                    .method(Connection.Method.POST).execute()
                val json = r.body()
                val yxkc = JsonParser().parse(json).asJsonObject["yxkcList"].asJsonArray
                for (je in yxkc) {
                    val subject = je.asJsonObject
                    val s = TermSubject()
                    s.code = JsonUtils.getStringData(subject, "kcdm")
                    // s.id = JsonUtils.getStringData(subject, "kcid")
                    s.name = JsonUtils.getStringData(subject, "kcmc").toString()
                    when (JsonUtils.getStringData(subject, "kcxzmc")) {
                        "必修" -> s.type = TermSubject.TYPE.COM_A
                        "限选" -> s.type = TermSubject.TYPE.OPT_A
                        "任选" -> s.type = TermSubject.TYPE.OPT_B
                    }
                    if (JsonUtils.getStringData(subject, "xkfsdm")?.toLowerCase(Locale.ROOT)
                            ?.contains("mooc") == true
                    ) {
                        s.type = TermSubject.TYPE.MOOC
                    }
                    s.school = JsonUtils.getStringData(subject, "kkyxmc")
                    s.credit = JsonUtils.getStringData(subject, "xf")?.toFloat() ?: 0f
                    s.key = JsonUtils.getStringData(subject, "kcid")
                    s.field = JsonUtils.getStringData(subject, "kclbmc")
                    //m["teacher"] = JsonUtils.getStringData(subject, "dgjsmc")
                    //m["xnxq"] = xn + xq
                    result.add(s)
                }
                res.postValue(DataState(result))
            } catch (e: IOException) {
                res.postValue(DataState(DataState.STATE.FETCH_FAILED, e.message))
            }
        }.start()
        return res
    }

    /**
     * 获取个人课表
     */
    override fun getTimetableOfTerm(
        term: TermItem,
        token: EASToken
    ): LiveData<DataState<List<CourseItem>>> {
        val res = MutableLiveData<DataState<List<CourseItem>>>()
        Thread {
            val result: MutableList<CourseItem> = ArrayList()
            try {
                val r = Jsoup.connect("http://jw.hitsz.edu.cn/xszykb/queryxszykbzong")
                    .timeout(timeout)
                    .cookies(token.cookies)
                    .headers(defaultRequestHeader)
                    .header("X-Requested-With", "XMLHttpRequest") //.data("zc", "2")
                    .data("xn", term.yearCode)
                    .data("xq", term.termCode)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .method(Connection.Method.POST)
                    .execute()
                val json = r.body()
                try {
                    val jsonList = JsonParser().parse(json).asJsonArray
                    for (je in jsonList) {
                        val jo = je.asJsonObject
                        val tm: String? = JsonUtils.getStringData(jo, "KEY")
                        val courseItem = CourseItem()
                        if (!TextUtils.isEmpty(tm) && tm?.contains("xq") == true && tm.contains("jc")) {
                            val twoInfo = tm.split("_".toRegex()).toTypedArray()
                            try {
                                courseItem.dow = twoInfo[0][2] - '0'
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                            try {
                                val beginS = twoInfo[1].replace("jc".toRegex(), "")
                                if (!TextUtils.isEmpty(beginS) && TextTools.isNumber(beginS)) {
                                    courseItem.begin = beginS.toInt() * 2 - 1
                                }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                        courseItem.last = 2
                        analyseBlockText(courseItem, JsonUtils.getStringData(jo, "SKSJ"))
                        // System.out.println(map);
                        if (!result.contains(courseItem) && courseItem.begin in 1..12) {
                            result.add(courseItem)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    res.postValue(DataState(DataState.STATE.FETCH_FAILED, e.message))
                }
                res.postValue(DataState(result, DataState.STATE.SUCCESS))
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                res.postValue(DataState(DataState.STATE.FETCH_FAILED, e.message))
            }
        }.start()
        return res
    }


    private fun analyseBlockText(result: CourseItem, txt: String?) {
        if (TextUtils.isEmpty(txt)) return
        var text = txt!!
        var name: String? = null
        var teacher: String? = null
        var classroom: String? = null
        var specificTime: String? = null
        var weekText: String? = null
        text = text.replace("\n".toRegex(), "")
        val inBoxes: MutableList<String> = ArrayList()
        val outBoxes: MutableList<String> = ArrayList()
        var sb = StringBuilder()
        var inBox = false
        for (i in text.indices) {
            when {
                text[i] == '[' -> {
                    if (!inBox && sb.isNotEmpty()) {
                        outBoxes.add(sb.toString())
                    }
                    sb = StringBuilder()
                    inBox = true
                }
                text[i] == ']' -> {
                    if (inBox && sb.isNotEmpty()) {
                        inBoxes.add(sb.toString())
                    }
                    sb = StringBuilder()
                    inBox = false
                }
                else -> {
                    sb.append(text[i])
                }
            }
        }

        val toRemove: MutableList<String> = ArrayList()
        for (info in inBoxes) {
            if (TextTools.containsNumber(info) && info.contains("周") ||
                TextTools.isNumber(
                    info.replace(",".toRegex(), "").replace("-".toRegex(), "").replace(
                        "单".toRegex(),
                        ""
                    ).replace("双".toRegex(), "")
                )
            ) {
                weekText = info
                toRemove.add(info)
            } else if (TextTools.containsNumber(info) && info.contains("节")) {
                specificTime = info.replace("节".toRegex(), "")
                toRemove.add(info)
            }
        }
        inBoxes.removeAll(toRemove)
        if (inBoxes.size > 1) {
            teacher = inBoxes[0]
            classroom = inBoxes[inBoxes.size - 1]
        } else if (inBoxes.size == 1) {
            classroom = inBoxes[0]
        }
        if (outBoxes.size > 0) name = outBoxes[0]
        if (weekText != null) {
            result.weeks.clear()
            for (week in weekText.split(",".toRegex()).toTypedArray()) {
                var wk = week
                var pairW = false
                var singW = false
                when {
                    wk.contains("单") -> {
                        singW = true
                        wk = wk.replace("单".toRegex(), "").replace("周".toRegex(), "")
                    }
                    wk.contains("双") -> {
                        pairW = true
                        wk = wk.replace("双".toRegex(), "").replace("周".toRegex(), "")
                    }
                    else -> wk = wk.replace("周".toRegex(), "")
                }
                if (wk.contains("-")) {
                    val ft = wk.split("-".toRegex()).toTypedArray()
                    val from = ft[0].toInt()
                    val to = ft[1].toInt()
                    for (i in from..to) {
                        if (pairW && i % 2 != 0 || singW && i % 2 == 0) continue
                        if (!result.weeks.contains(i)) result.weeks.add(i)
                        //weeks.append(i+"").append(",");
                    }
                } else if (!result.weeks.contains(wk.toInt())) result.weeks.add(wk.toInt())
            }
        }
        if (specificTime != null && specificTime.contains("-")) {
            val spcf = specificTime.split("-".toRegex()).toTypedArray()
            if (spcf.isNotEmpty()) {
                result.begin = spcf[0].toInt()
            }
            if (spcf.size > 1) {
                result.last = (spcf[1].toInt() - spcf[0].toInt() + 1)
            }
        }
        result.name = name
        result.teacher = teacher
        result.classroom = classroom
    }


    /**
     * 获取课表结构
     */
    override fun getScheduleStructure(
        term: TermItem,
        token: EASToken
    ): LiveData<DataState<MutableList<TimePeriodInDay>>> {
        val res = MutableLiveData<DataState<MutableList<TimePeriodInDay>>>()
        Thread {
            val result: MutableList<TimePeriodInDay> = ArrayList()
            try {
                val r = Jsoup.connect("http://jw.hitsz.edu.cn/component/queryKbjg")
                    .timeout(timeout)
                    .cookies(token.cookies)
                    .headers(defaultRequestHeader)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .data("xn", term.yearCode) //学年
                    .data("xq", term.termCode) //学期
                    .method(Connection.Method.POST).execute()
                val json = r.body()
                val content = JsonParser().parse(json).asJsonObject["content"].asJsonArray
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                for (je in content) {
                    val tp = je.asJsonObject
                    val from = Calendar.getInstance()
                    from.timeInMillis = dateFormatter.parse(tp["kssj"].asString)!!.time
                    val to = Calendar.getInstance()
                    to.timeInMillis = dateFormatter.parse(tp["jssj"].asString)!!.time
                    result.add(TimePeriodInDay(TimeInDay(from), TimeInDay(to)))
                }
                res.postValue(DataState(result))
            } catch (e: Exception) {
                e.printStackTrace()
                res.postValue(DataState(DataState.STATE.FETCH_FAILED, e.message))
            }
        }.start()
        return res
    }


    init {
        defaultRequestHeader = HashMap()
        defaultRequestHeader["Accept"] = "*/*"
        defaultRequestHeader["Connection"] = "keep-alive"
        defaultRequestHeader["User-Agent"] =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36"
    }
}