package com.stupidtree.hitax.data.source.web.additional

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stupidtree.component.data.DataState
import com.stupidtree.hitax.data.source.web.service.AdditionalService
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*

class AdditionalSource : AdditionalService {
    @SuppressLint("SimpleDateFormat")
    override fun getLectures(
        pageSize: Int,
        pageOffset: Int
    ): LiveData<DataState<List<Map<String, String>>>> {
        val ret = MutableLiveData<DataState<List<Map<String, String>>>>()
        Thread {
            val res = mutableListOf<Map<String, String>>()
            try {
                val d =
                    Jsoup.connect("http://www.hitsz.edu.cn/article/id-78.html?maxPageItems=$pageSize&keywords=&pager.offset=$pageOffset")
                        .header(
                            "User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36"
                        )
                        .header("X-Requested-With", "XMLHttpRequest")
                        .get()
                val e = d.select("ul[class^=lecture_n]")
                val ee = e.select("li")
                for (x in ee) {
                    val lecture: MutableMap<String, String> = HashMap()
                    val title = x.select("a").text()
                    val link = x.select("a").attr("href")
                    val host: String = if (x.select("div[class^=lecture_bottom]")
                            .select("div:contains(人)").size > 0
                    ) x.select("div[class^=lecture_bottom]")
                        .select("div:contains(人)")[1].text() else ""
                    val place: String = if (x.select("div[class^=lecture_bottom]")
                            .select("div:contains(讲座地点)").size > 0
                    ) x.select("div[class^=lecture_bottom]")
                        .select("div:contains(讲座地点)")[1].text() else ""
                    val time: String = if (x.select("div[class^=lecture_bottom]")
                            .select("div:contains(讲座时间)").size > 0
                    ) x.select("div[class^=lecture_bottom]")
                        .select("div:contains(讲座时间)")[1].text() else ""

                    val releaseTime = x.select("[class=date_t]").text()
                    val view = x.select("[class=view]").text()
                    val image = x.select("img").attr("src")
                    val date = x.select("[class=date]").text().replace(" ", "")
                    val cal = try {
                        val tm = SimpleDateFormat("yyyy-MM-dd").parse(releaseTime)!!
                        val c = Calendar.getInstance()
                        c.time = tm
                        val month = date.split("月")[0].trim().replace("月", "").toInt()
                        val day = date.split("月")[1].trim().replace("日", "").toInt()
                        c.set(Calendar.MONTH, month - 1)
                        c.set(Calendar.DAY_OF_MONTH, day)
                        "!" + c.timeInMillis.toString()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "#$date"
                    }
                    lecture["title"] = title
                    lecture["host"] = host
                    lecture["place"] = place
                    lecture["time"] = time
                    lecture["view"] = view
                    lecture["date"] = cal
                    lecture["picture"] = image
                    lecture["link"] = link
                    res.add(lecture)
                }
                ret.postValue(DataState(res))
            } catch (e: Exception) {
                ret.postValue(DataState(DataState.STATE.FETCH_FAILED))
            }
        }.start()
        return ret
    }

    override fun getNewsMeta(link: String): LiveData<DataState<Map<String, String>>> {
        val ret = MutableLiveData<DataState<Map<String, String>>>()
        Thread {
            val m: MutableMap<String, String> = HashMap()
            val d = Jsoup.connect("http://www.hitsz.edu.cn$link").get()
            try {
                val text = d.select("[class=detail]").select("[class=edittext]").toString()
                m["text"] = text
                val time = d.select("[class=tip]")[0].text() + "浏览量"
                m["time"] = time
                ret.postValue(DataState(m))
            } catch (e: Exception) {
                e.printStackTrace()
                ret.postValue(DataState(DataState.STATE.FETCH_FAILED))
            }

        }.start()
        return ret
    }
}