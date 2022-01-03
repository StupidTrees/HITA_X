package com.stupidtree.hitax.data.source.web.eas

import android.annotation.SuppressLint
import android.app.Application
import android.os.Handler
import android.os.HandlerThread
import android.text.TextUtils
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.stupidtree.hitax.data.model.eas.CourseItem
import com.stupidtree.hitax.data.model.eas.EASToken
import com.stupidtree.hitax.data.model.eas.TermItem
import com.stupidtree.hitax.data.model.timetable.TermSubject
import com.stupidtree.hitax.data.model.timetable.TimeInDay
import com.stupidtree.hitax.data.model.timetable.TimePeriodInDay
import com.stupidtree.hitax.data.source.web.service.EASService
import com.stupidtree.component.data.DataState
import com.stupidtree.hitax.data.model.eas.CourseScoreItem
import com.stupidtree.hitax.ui.eas.classroom.BuildingItem
import com.stupidtree.hitax.ui.eas.classroom.ClassroomItem
import com.stupidtree.hitax.utils.JsonUtils
import com.stupidtree.hitax.utils.TextTools
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.io.IOException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SetJavaScriptEnabled")
class EASJSource internal constructor(val application: Application) : EASService {
    private val defaultRequestHeader: MutableMap<String, String>
    private val timeout = 5000
    private val hostName = "http://jw.hitsz.edu.cn"
    private var webView: WebView? = null
    private val mHandler: Handler
    private val liveDataMap = mutableMapOf<String, LiveData<*>>()


    @WorkerThread
    private fun getWebView(): WebView {
        if (webView == null) {
            webView = WebView(application)
            val settings = webView?.settings
            settings?.javaScriptEnabled = true
            webView?.setWillNotDraw(true)
            webView?.addJavascriptInterface(JsBridge(), "innerBridge")
            webView?.addJavascriptInterface(BridgeCallback(), "innerCallback")
            webView?.loadData(
                URLEncoder.encode("<html><body></body></html>", "utf-8"),
                "text/html",
                "utf-8"
            )
            webView?.evaluateJavascript(initCode) {
                Log.e("init", it)
            }
        }
        return webView!!
    }

    class JsBridge {
        @JavascriptInterface
        fun doHttp(
            url: String,
            headers: String,
            cookies: String,
            data: String,
            method: String
        ): String? {
            return try {
                val cookiesMap = JsonUtils.jsonStringToMap(cookies)
                val dataMap = JsonUtils.jsonStringToMap(data)
                val headersMap = JsonUtils.jsonStringToMap(headers)
                val res = Jsoup.connect(url)
                    .cookies(cookiesMap)
                    .data(dataMap)
                    .headers(headersMap)
                    .method(if (method == "POST") Connection.Method.POST else Connection.Method.GET)
                    .execute()
                val m = mutableMapOf<String, String>()
                m["cookies"] = res.cookies().toString()
                m["body"] = res.body().toString()
                return Gson().toJson(m)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        @JavascriptInterface
        fun log(content: String) {
            Log.e("bridge", content)
        }


    }

    class BridgeCallback {
        @JavascriptInterface
        fun onLogInResult(uuid: String, success: String, cookies: String) {
            Log.e("res", uuid + "," + cookies)
        }
    }

    init {
        val nt = HandlerThread("net")
        nt.start()
        mHandler = Handler(nt.looper)
        defaultRequestHeader = HashMap()
        defaultRequestHeader["Accept"] = "*/*"
        defaultRequestHeader["Connection"] = "keep-alive"
        defaultRequestHeader["User-Agent"] =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36"

    }

    /**
     * 登录
     */
    override fun login(
        username: String,
        password: String,
        code: String?
    ): LiveData<DataState<EASToken>> {
        val res = MutableLiveData<DataState<EASToken>>()
        mHandler.post {
            val wb = getWebView()
            val uuid = UUID.randomUUID().toString()
            liveDataMap[uuid] = res
            wb.evaluateJavascript("login(\"$uuid\")\n") {
                Log.e("eva", it)
            }
        }
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
                    .post()
                val json = s.getElementsByTag("body").text()
                if (json.contains("session已失效")) {
                    res.postValue(DataState(false))
                } else {
                    val jo = JsonUtils.getJsonObject(json)
                    val login = jo?.has("XH") ?: false
                    res.postValue(DataState(login))
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
                    .timeout(timeout)
                    .headers(defaultRequestHeader)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .post()
                val json = s.getElementsByTag("body").first().text()
                val jsonList = JsonUtils.getJsonObject(json)?.optJSONArray("content")
                jsonList?.let { ja ->
                    for (i in 0 until ja.length()) {
                        val jo = ja.optJSONObject(i)
                        val m = mutableMapOf<String, String>()
                        jo?.let {
                            for (key in it.keys()) {
                                m[key.replace("\"".toRegex(), "")] =
                                    it.getString(key).toString().replace("\"".toRegex(), "")
                            }
                            val term =
                                TermItem(
                                    m["xn"] ?: "",
                                    m["xnmc"] ?: "", m["xq"] ?: "", m["xqmc"] ?: ""
                                )
                            term.isCurrent = m["sfdqxq"] == "1"
                            terms.add(term)
                        }
                    }
                    res.postValue(DataState(terms, DataState.STATE.SUCCESS))
                } ?: run {
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
            try {
                val s = Jsoup.connect("$hostName/Xiaoli/queryMonthList")
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
                val json = s.getElementsByTag("body").text()
                val monthList: JSONArray? = JsonUtils.getJsonObject(json)?.optJSONArray("monlist")
                val firstMon: JSONObject? = monthList?.optJSONObject(0)
                val year = firstMon?.optInt("yy")
                val month = firstMon?.optInt("mm")
                val firstMonDays: JSONArray? = firstMon?.optJSONArray("dszlist")
                var index = 0
                for (i in 0 until (firstMonDays?.length() ?: 0)) {
                    val aWeek: JSONObject? = firstMonDays?.optJSONObject(i)
                    val attr = aWeek?.optString("xldjz", "")
                    if (!attr.isNullOrEmpty() && attr != "null") break
                    index++
                }
                val result = Calendar.getInstance()
                result.firstDayOfWeek = Calendar.MONDAY
                result[Calendar.YEAR] = year ?: 1970
                result[Calendar.MONTH] = (month ?: 12) - 1
                result[Calendar.WEEK_OF_MONTH] = index + 1
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
                val r = Jsoup.connect("$hostName/Xsxk/queryYxkc")
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
                val json = JsonUtils.getJsonObject(r.body())
                val yxkc: JSONArray? = json?.optJSONArray("yxkcList")
                yxkc?.let {
                    for (i in 0 until it.length()) {
                        val subject: JSONObject? = yxkc.optJSONObject(i)
                        val s = TermSubject()
                        s.code = subject?.optString("kcdm")
                        // s.id = JsonUtils.getStringData(subject, "kcid")
                        s.name = subject?.optString("kcmc") ?: ""
                        when (subject?.optString("kcxzmc")) {
                            "必修" -> s.type = TermSubject.TYPE.COM_A
                            "限选" -> s.type = TermSubject.TYPE.OPT_A
                            "任选" -> s.type = TermSubject.TYPE.OPT_B
                        }
                        if (subject?.optString("xkfsdm")?.toLowerCase(Locale.ROOT)
                                ?.contains("mooc") == true
                        ) {
                            s.type = TermSubject.TYPE.MOOC
                        }
                        s.school = subject?.optString("kkyxmc")
                        s.credit = subject?.optString("xf")?.toFloat() ?: 0f
                        s.key = subject?.optString("kcid")
                        s.field = subject?.optString("kclbmc")
                        result.add(s)
                    }
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
                val r = Jsoup.connect("$hostName/xszykb/queryxszykbzong")
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
                val jsonList = JsonUtils.getJsonArray(json)
                for (i in 0 until (jsonList?.length() ?: 0)) {
                    val jo = jsonList?.optJSONObject(i)
                    val tm: String? = jo?.optString("KEY")
                    val courseItem = CourseItem()
                    if (!tm.isNullOrEmpty() && tm.contains("xq") && tm.contains("jc")) {
                        try {
                            val twoInfo = tm.split("_".toRegex()).toTypedArray()
                            courseItem.dow = twoInfo[0][2] - '0'
                            val beginS = twoInfo[1].replace("jc".toRegex(), "")
                            if (!TextUtils.isEmpty(beginS) && TextTools.isNumber(beginS)) {
                                courseItem.begin = beginS.toInt() * 2 - 1
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    courseItem.last = 2
                    analyseBlockText(courseItem, jo?.optString("SKSJ"))
                    if (!result.contains(courseItem) && courseItem.begin in 1..12) {
                        result.add(courseItem)
                    }
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
                val r = Jsoup.connect("$hostName/component/queryKbjg")
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
                val content = JsonUtils.getJsonObject(json)?.optJSONArray("content")
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                content?.let {
                    for (i in 0 until it.length()) {
                        val tp: JSONObject? = it.optJSONObject(i)
                        val from = Calendar.getInstance()
                        val to = Calendar.getInstance()
                        val fromTxt: String =
                            tp?.optString("kssj", "1970-01-01 00:00:00") ?: "1970-01-01 00:00:00"
                        val toTxt: String =
                            tp?.optString("jssj", "1970-01-01 00:00:00") ?: "1970-01-01 00:00:00"
                        from.timeInMillis =
                            dateFormatter.parse(fromTxt)?.time ?: 0
                        to.timeInMillis =
                            dateFormatter.parse(toTxt)?.time ?: 0
                        result.add(TimePeriodInDay(TimeInDay(from), TimeInDay(to)))
                    }
                    res.postValue(DataState(result))
                } ?: run {
                    res.postValue(DataState(DataState.STATE.FETCH_FAILED))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                res.postValue(DataState(DataState.STATE.FETCH_FAILED, e.message))
            }
        }.start()
        return res
    }

    /**
     * 获取最终成绩
     */
    override fun getPersonalScores(
        term: TermItem,
        token: EASToken,
        testType: EASService.TestType
    ): LiveData<DataState<List<CourseScoreItem>>> {
        val res = MutableLiveData<DataState<List<CourseScoreItem>>>()
        Thread {
            val result: MutableList<CourseScoreItem> = ArrayList()
            try {
                val r = Jsoup.connect("$hostName/cjgl/grcjcx/grcjcx")
                    .timeout(timeout)
                    .cookies(token.cookies)
                    .headers(defaultRequestHeader)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("Content-Type","application/json")
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .requestBody(
                        """{"xn":"""" + term.yearCode +
                                """","xq":""" + term.termCode +
                                ""","kcmc":null""" + ""","cxbj":""" + testType.value +
                                ""","pylx":1,"current":1,"pageSize":300}"""
                    )
                    .method(Connection.Method.POST).execute()
                val json = r.body()
                val content = JsonUtils.getJsonObject(json)?.optJSONObject("content")?.optJSONArray("list")
                content?.let {
                    for (i in 0 until it.length()) {
                        val tp: JSONObject = it.optJSONObject(i)
                        val item = CourseScoreItem();
                        item.assessMethod = tp.optString("khfs", "null")
                        item.courseCategory = tp.optString("kclb", "null")
                        item.courseCode = tp.optString("kcdm", "null")
                        item.courseName = tp.optString("kcmc","null")
                        item.courseProperty = tp.optString("kcxz","null")
                        item.credits = tp.optInt("xf", -1)
                        item.finalScores = tp.optInt("zzcj",-1)
                        item.hours = tp.optInt("xs", -1)
                        item.schoolName = tp.optString("yxmc","null")
                        item.termName = tp.optString("xnxqmc","null")
                        result.add(item)
                    }
                    res.postValue(DataState(result))
                } ?: run {
                    res.postValue(DataState(DataState.STATE.FETCH_FAILED))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                res.postValue(DataState(DataState.STATE.FETCH_FAILED, e.message))
            }
        }.start()
        return res
    }

    override fun getTeachingBuildings(token: EASToken): LiveData<DataState<List<BuildingItem>>> {
        val result = MutableLiveData<DataState<List<BuildingItem>>>()
        Thread {
            val res = mutableListOf<BuildingItem>()
            try {
                val r = Jsoup.connect("$hostName/pksd/queryjxlList")
                    .cookies(token.cookies).headers(defaultRequestHeader)
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .execute()
                val ja = JsonUtils.getJsonArray(r.body())
                for (i in 0 until (ja?.length() ?: 0)) {
                    val jo = ja?.optJSONObject(i)
                    val hm = BuildingItem()
                    hm.name = jo?.optString("MC")
                    hm.id = jo?.optString("DM").toString()
                    res.add(hm)
                }
                result.postValue(DataState(res))
            } catch (e: Exception) {
                result.postValue(DataState(DataState.STATE.FETCH_FAILED))
            }
        }.start()
        return result
    }

    override fun queryEmptyClassroom(
        token: EASToken,
        term: TermItem,
        building: BuildingItem,
        weeks: List<String>
    ): LiveData<DataState<List<ClassroomItem>>> {
        val result = MutableLiveData<DataState<List<ClassroomItem>>>()
        Thread {
            val res: MutableList<ClassroomItem> = ArrayList()
            val sb = java.lang.StringBuilder()
            val weekZeros = "000000000000000000000000000000000000000000000000".toCharArray()
            try {
                for (i in weeks.indices) {
                    sb.append(weeks[i])
                    weekZeros[weeks[i].toInt()] = '1'
                    if (i != weeks.size - 1) sb.append(",")
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            try {
                val r = Jsoup.connect("$hostName/cdkb/querycdzyleftzhou")
                    .cookies(token.cookies).headers(defaultRequestHeader)
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .data("qsjsz", sb.toString())
                    .data("pxn", term.yearCode)
                    .data("pxq", term.termCode)
                    .data("jxl", building.id)
                    .data("zc", String(weekZeros))
                    .execute()
                val ja = JsonUtils.getJsonArray(r.body())
                for (i in 0 until (ja?.length() ?: 0)) {
                    val classroom = ClassroomItem()
                    val jo = ja?.optJSONObject(i)
                    classroom.name = jo?.optString("MC").toString()
                    classroom.id = jo?.optString("DM").toString()
                    classroom.capacity = try {
                        jo?.optString("ZWS")?.toInt() ?: 0
                    } catch (e: Exception) {
                        0
                    }
                    classroom.specialClassroom = jo?.optString("SFJTJS")
//                hm.addProperty("jtjs", )
//                hm.addProperty("kj", jo?.optString("SFKJ"))
                    res.add(classroom)
                }
                val r2 = Jsoup.connect("$hostName/cdkb/querycdzyrightzhou")
                    .cookies(token.cookies).headers(defaultRequestHeader)
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .data("qsjsz", sb.toString())
                    .data("pxn", term.yearCode)
                    .data("pxq", term.termCode)
                    .data("jxl", building.id)
                    .data("zc", String(weekZeros))
                    .execute()
                val jar = JsonUtils.getJsonArray(r2.body())
                for (i in 0 until (jar?.length() ?: 0)) {
                    val jo = jar?.optJSONObject(i)
                    for (j in res) {
                        if (j.id == jo?.optString("CDDM")) {
                            jo.remove("CDDM")
                            j.scheduleList.add(jo)
                            break
                        }
                    }
                }
                result.postValue(DataState(res))
            } catch (e: IOException) {
                e.printStackTrace()
                result.postValue(DataState(DataState.STATE.FETCH_FAILED))
            }
        }.start()
        return result
    }

    companion object {
        const val initCode = "JsBridge = {};\n" +
                "JsBridge.doHttp = function (url, headers, cookies, data, method) {\n" +
                "    let res = innerBridge.doHttp(url, JSON.stringify(headers), JSON.stringify(cookies), JSON.stringify(data), method);\n" +
                "    if (res === undefined || res === null) {\n" +
                "        return null;\n" +
                "    } else {\n" +
                "        const jo = JSON.parse(res);\n" +
                "        const s = document.createElement('div');\n" +
                "        s.innerHTML = jo.body;\n" +
                "        innerBridge.log(s.childNodes)\n" +
                "        return {\n" +
                "            doc: s.childNodes,\n" +
                "            cookies: jo['cookies'],\n" +
                "            body: jo['body']\n" +
                "        };\n" +
                "    }\n" +
                "};\n" +
                "\n" +
                "BridgeCallback = {};\n" +
                "BridgeCallback.onLogInResult = function (uuid, state, cookies) {\n" +
                "    innerCallback.onLogInResult(uuid, state, JSON.stringify(cookies));\n" +
                "};\n" +
                "\n" +
                "\n" +
                "\n" +
                "function login(uuid, username, password) {\n" +
                "    const header = {\n" +
                "        \"Accept\": \"*/*\",\n" +
                "        \"Connection\": \"keep-alive\",\n" +
                "        \"User-Agent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36\"\n" +
                "    };\n" +
                "    let res = JsBridge.doHttp(\"http://jw.hitsz.edu.cn/cas\", JSON.stringify(header), \"{}\", \"{}\", \"GET\");\n" +
                "    let d = res.doc\n" +
                "    let x1 = d.getElementsByTagName(\"input\").getElementsByName(\"lt\")\n" +
                "    if(x1.length>0){\n" +
                "        let lt = x1[0]['value']\n" +
                "        innerBridge.log(lt)\n" +
                "    }\n" +
                "    // var lt = d.select(\"input[name=lt]\")[0].getAtt\"value\")\n" +
                "\n" +
                "    // var execution = d.select(\"input[name=execution]\").first().attr(\"value\")\n" +
                "    // var eventId = d.select(\"input[name=_eventId]\").first().attr(\"value\")\n" +
                "    // var BridgeCallback.onLogInResult(uuid, \"success\", res.cookies);\n" +
                "}"
    }


}