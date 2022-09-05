package com.stupidtree.hitax

import android.app.Application
import android.util.Log
import androidx.annotation.WorkerThread
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.stupidtree.hitax.data.AppDatabase
import com.stupidtree.hitax.data.model.GsonBuilderUtil
import com.stupidtree.hitax.data.model.timetable.EventItem
import com.stupidtree.hitax.data.model.timetable.TermSubject
import com.stupidtree.hitax.data.model.timetable.Timetable
import com.stupidtree.stupiduser.data.repository.LocalUserRepository
import com.stupidtree.sync.StupidSync
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class HApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getDatabase(this@HApplication)
        val timetableDao = database.timetableDao()
        val subjectDao = database.subjectDao()
        val eventDao = database.eventItemDao()
        StupidSync.init(this, object : StupidSync.PushDelegate {

            @WorkerThread
            override fun getDataForIds(key: String, ids: List<String>): List<JsonObject> {
                return when (key) {
                    "timetable" -> {
                        val list = mutableListOf<JsonObject>()
                        for (tt in timetableDao.getTimetablesInIdsSync(ids)) {
                            val jo = Gson().toJsonTree(tt).asJsonObject
                            jo.addProperty("startTime", tt.startTime.time)
                            jo.addProperty("endTime", tt.endTime.time)
                            list.add(jo)
                        }
                        list
                    }
                    "subject" -> {
                        val list = mutableListOf<JsonObject>()
                        for (tt in subjectDao.getSubjectsInIdsSync(ids)) {
                            val jo = Gson().toJsonTree(tt).asJsonObject
                            list.add(jo)
                        }
                        return list
                    }
                    "event" -> {
                        val list = mutableListOf<JsonObject>()
                        for (tt in eventDao.getEventInIdsSync(ids)) {
                            val jo = Gson().toJsonTree(tt).asJsonObject
                            jo.addProperty("from", tt.from.time)
                            jo.addProperty("to", tt.to.time)
                            list.add(jo)
                        }
                        return list

                    }
                    else -> listOf()
                }
            }

            override fun saveData(key: String, ids: List<String>, data: List<String>) {
                when (key) {
                    "timetable" -> {
                        val tts = mutableMapOf<String, Timetable>()
                        for (a in data) {
                            val tt = GsonBuilderUtil.create().fromJson(a, Timetable::class.java)
                            tts[tt.id] = tt
                        }
                        val toAdd = mutableListOf<Timetable>()
                        for (id in ids) {
                            tts[id]?.let { toAdd.add(it) }
                        }
                        Log.e("save_timetable", toAdd.toString())
                        timetableDao.saveTimetablesSync(toAdd)
                    }
                    "subject" -> {
                        val tts = mutableMapOf<String, TermSubject>()
                        for (a in data) {
                            val tt =  GsonBuilderUtil.create().fromJson(a, TermSubject::class.java)
                            tts[tt.id] = tt
                        }
                        val toAdd = mutableListOf<TermSubject>()
                        for (id in ids) {
                            tts[id]?.let { toAdd.add(it) }
                        }
                        subjectDao.saveSubjectsSync(toAdd)
                    }
                    "event" -> {
                        val tts = mutableMapOf<String, EventItem>()
                        for (a in data) {
                            val tt =  GsonBuilderUtil.create().fromJson(a, EventItem::class.java)
                            tts[tt.id] = tt
                        }
                        val toAdd = mutableListOf<EventItem>()
                        for (id in ids) {
                            tts[id]?.let { toAdd.add(it) }
                        }
                        eventDao.saveEvents(toAdd)
                    }
                }
            }

            override fun deleteData(key: String, ids: List<String>) {
                when (key) {
                    "timetable" -> {
                        timetableDao.deleteTimetablesInIdsSync(ids)
                    }
                    "event" -> {
                        eventDao.deleteEventsInIdsSync(ids)
                    }
                    "subject" -> {
                        subjectDao.deleteSubjectsInIdsSync(ids)
                    }
                }
            }

            override fun clearData(key: String) {
                when (key) {
                    "timetable" -> {
                        timetableDao.clear()
                    }
                    "event" -> {
                        eventDao.clear()
                    }
                    "subject" -> {
                        subjectDao.clear()
                    }
                }
            }

        })
        StupidSync.setUID(LocalUserRepository.getInstance(this).getLoggedInUser().id)
        handleSSLHandshake()
    }

    private fun handleSSLHandshake() {
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate?> {
                    return arrayOfNulls(0)
                }

                override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {}
            })
            val sc = SSLContext.getInstance("TLS")

            // trustAllCerts信任所有的证书
            sc.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
            HttpsURLConnection.setDefaultHostnameVerifier { hostname, session -> true }
        } catch (ignored: Exception) {
        }
    }


}