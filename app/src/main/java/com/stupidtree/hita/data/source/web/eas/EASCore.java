//package com.stupidtree.hita.data.source.web.eas;
//
//import android.content.SharedPreferences;
//import android.text.TextUtils;
//import android.util.Log;
//
//import androidx.annotation.WorkerThread;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonNull;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import com.google.gson.JsonSyntaxException;
//import com.stupidtree.hita.data.model.eas.EASException;
//import com.stupidtree.hita.utils.JsonUtils;
//
//import org.jsoup.Connection;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class EASCore {
//    private boolean login;
//    private HashMap<String, String> cookies;
//    private Map<String, String> defaultRequestHeader;
//    private int timeout;
//    private String hostName = "http://jw.hitsz.edu.cn";
//
//    public String getHostName() {
//        return hostName;
//    }
//
//    public EASCore() {
//        login = false;
//        timeout = 5000;
//        cookies = new HashMap<>();
//        defaultRequestHeader = new HashMap<String, String>();
//        defaultRequestHeader.put("Accept", "*/*");
//        defaultRequestHeader.put("Connection", "keep-alive");
//        defaultRequestHeader.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
//    }
//
//    public boolean hasLogin() {
//        return login;
//    }
//
//    @WorkerThread
//    public boolean login(String username, String password) throws EASException {
//        try {
//            Connection hc = Jsoup.connect("http://jw.hitsz.edu.cn/cas").headers(defaultRequestHeader);
//            cookies.clear();
//            cookies.putAll(hc.execute().cookies());
//            String lt, execution, eventId;
//            Document d = hc.cookies(cookies).get();
//            lt = d.select("input[name=lt]").first().attr("value");
//            execution = d.select("input[name=execution]").first().attr("value");
//            eventId = d.select("input[name=_eventId]").first().attr("value");
//            Connection c2 = Jsoup.connect("https://sso.hitsz.edu.cn:7002/cas/login?service=http%3A%2F%2Fjw.hitsz.edu.cn%2FcasLogin")
//                    .cookies(cookies)
//                    .headers(defaultRequestHeader)
//                    .ignoreContentType(true);
//            Document page = c2.cookies(cookies)
//                    .data("username", username)
//                    .data("password", password)
//                    .data("lt", lt)
//                    .data("rememberMe", "on")
//                    .data("execution", execution)
//                    .data("_eventId", eventId).post();
//            cookies.putAll(c2.response().cookies());
//            login = page.toString().contains("qxdm");
//            if (login) {
//                if (defaultSP == null) return login;
//                SharedPreferences.Editor edt = defaultSP.edit();
//                edt.putString("jw_cookie", new Gson().toJson(cookies));
//                edt.putString(username + ".password", password);
//                edt.apply();
//            } else logOut();
//            return login;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw EASException.getConnectErrorExpection();
//        }
//    }
//
//    @WorkerThread
//    public boolean loginCheck() throws EASException {
//        try {
//            Document s = Jsoup.connect("http://jw.hitsz.edu.cn/UserManager/queryxsxx")
//                    .timeout(timeout)
//                    .cookies(cookies)
//                    .headers(defaultRequestHeader)
//                    .header("X-Requested-With", "XMLHttpRequest")
//                    .ignoreContentType(true)
//                    .ignoreHttpErrors(true)
//                    .post();
//            try {
//                String json = s.getElementsByTag("body").text();
//                if (json.contains("session已失效")) return false;
//                JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
//                login = jo.has("XH");
//                if (!login) logOut();
//                return login;
//            } catch (JsonSyntaxException e) {
//                logOut();
//                return false;
//            }
//        } catch (IOException e) {
//            throw EASException.getConnectErrorExpection();
//        }
//    }
//
//    @WorkerThread
//    public Map<String, String> getBasicUserInfo() throws EASException {
//        Map<String, String> result = new HashMap<>();
//        try {
//            Document s = Jsoup.connect("http://jw.hitsz.edu.cn/UserManager/queryxsxx")
//                    .timeout(timeout)
//                    .cookies(cookies)
//                    .headers(defaultRequestHeader)
//                    .header("X-Requested-With", "XMLHttpRequest")
//                    .ignoreContentType(true)
//                    .ignoreHttpErrors(true)
//                    .post();
//            try {
//                String json = s.getElementsByTag("body").text();
//                JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
//                result.put("real_name", JsonUtils.getStringInfo(jo, "XM"));
//                result.put("school", JsonUtils.getStringInfo(jo, "YXMC"));
//                result.put("student_number", JsonUtils.getStringInfo(jo, "XH"));
//                result.put("grade", JsonUtils.getStringInfo(jo, "NJMC"));
//                return result;
//            } catch (JsonSyntaxException e) {
//                logOut();
//                throw EASException.getLoginFailedExpection();
//            }
//        } catch (IOException e) {
//            throw EASException.getConnectErrorExpection();
//        }
//    }
//
//    @WorkerThread
//    public List<Map<String, String>> getGRCJ(String xn, String xq) throws EASException {
//        List<Map<String, String>> result = new ArrayList<>();
//        try {
//            JsonObject requestPayLoad = new JsonObject();
//            requestPayLoad.addProperty("xn", xn);
//            requestPayLoad.addProperty("xq", xq);
//            requestPayLoad.addProperty("kcmc", (String) null);
//            requestPayLoad.addProperty("cxbj", "-1");
//            requestPayLoad.addProperty("pylx", "1");
//            requestPayLoad.addProperty("current", 1);
//            requestPayLoad.addProperty("pageSize", 100);
//            Document s = Jsoup.connect("http://jw.hitsz.edu.cn/cjgl/grcjcx/grcjcx")
//                    //.timeout(timeout)
//                    .cookies(cookies)
//                    .headers(defaultRequestHeader)
//                    .header("X-Requested-With", "XMLHttpRequest")
//                    .ignoreContentType(true)
//                    .ignoreHttpErrors(true)
//                    .header("Content-Type", "application/json;charset=UTF-8")
//                    .requestBody(requestPayLoad.toString())
//                    .post();
//            try {
//                String json = s.getElementsByTag("body").text();
//                JsonArray list = new JsonParser().parse(json).getAsJsonObject().get("content").getAsJsonObject().get("list").getAsJsonArray();
//                for (JsonElement je : list) {
//                    JsonObject jo = je.getAsJsonObject();
//                    Map<String, String> m = new HashMap<>();
//                    m.put("name", jo.get("kcmc").getAsString());
//                    m.put("code", jo.get("kcdm").getAsString());
//                    m.put("type", jo.get("kclb").getAsString());
//                    m.put("compulsory", jo.get("kcxz").getAsString());
//                    m.put("credit", jo.get("xf").getAsString());
//                    m.put("total_score", jo.get("zzcj").getAsString());
//                    m.put("final_score", jo.get("zzzscj").getAsString());
//                    m.put("school", jo.get("yxmc").getAsString());
//                    m.put("exam", jo.get("khfs").getAsString());
//                    m.put("all", String.valueOf(jo));
//                    result.add(m);
//                }
//                return result;
//            } catch (JsonSyntaxException e) {
//                logOut();
//                throw EASException.getLoginFailedExpection();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw EASException.getConnectErrorExpection();
//        }
//    }
//
//
//    public void logOut() {
//        login = false;
//        cookies.clear();
//        if (defaultSP != null) defaultSP.edit().putString("jw_cookie", null).apply();
//    }
//
//    @WorkerThread
//    public List<Map<String, String>> getXNXQ() throws EASException {
//        try {
//            List<Map<String, String>> result = new ArrayList<>();
//            Document s = Jsoup.connect("http://jw.hitsz.edu.cn/component/queryXnxq")
//                    .cookies(cookies)
//                    .headers(defaultRequestHeader)
//                    .header("X-Requested-With", "XMLHttpRequest")
//                    .ignoreContentType(true)
//                    .ignoreHttpErrors(true)
//                    .post();
//
//            try {
//                String json = s.getElementsByTag("body").first().text();
//                JsonArray jsonList = new JsonParser().parse(json).getAsJsonObject().get("content").getAsJsonArray();
//                for (JsonElement je : jsonList) {
//                    Map<String, String> m = new HashMap<>();
//                    for (Map.Entry<? extends String, ? extends JsonElement> e : je.getAsJsonObject().entrySet()) {
//                        m.put(e.getKey().replaceAll("\"", ""), e.getValue().toString().replaceAll("\"", ""));
//                    }
//                    result.add(m);
//                }
//                return result;
//            } catch (Exception e) {
//                logOut();
//                throw EASException.getLoginFailedExpection();
//            }
//        } catch (IOException e) {
//            throw EASException.getConnectErrorExpection();
//        }
//
//    }
//
//    @WorkerThread
//    public List<Map<String, String>> getChosenSubjectsInfo(String xn, String xq) throws EASException {
//        List<Map<String, String>> result = new ArrayList<>();
//        String text = "";
//        try {
//            Connection.Response r = Jsoup.connect("http://jw.hitsz.edu.cn/Xsxk/queryYxkc")
//                    .timeout(timeout)
//                    .cookies(cookies)
//                    .headers(defaultRequestHeader)
//                    .header("X-Requested-With", "XMLHttpRequest")
//                    .ignoreContentType(true)
//                    .ignoreHttpErrors(true)
//                    .data("p_pylx", "1")
//                    .data("p_sfgldjr", "0")
//                    .data("p_sfredis", "0")
//                    .data("p_sfsyxkgwc", "0")
//                    .data("p_xn", xn) //学年
//                    .data("p_xq", xq) //学期
//                    .data("p_xnxq", xn + xq) //学年学期
//                    .data("p_dqxn", xn) //当前学年
//                    .data("p_dqxq", xq) //当前学期
//                    .data("p_dqxnxq", xn + xq) //当前学年学期
//                    .data("p_xkfsdm", "yixuan") //已选
//                    .data("p_sfhlctkc", "0")
//                    .data("p_sfhllrlkc", "0")
//                    .data("p_sfxsgwckb", "1")
//                    .method(Connection.Method.POST).execute();
//            String json = r.body();
//            text = text + json;
//            JsonArray yxkc = new JsonParser().parse(json).getAsJsonObject().get("yxkcList").getAsJsonArray();
//            for (JsonElement je : yxkc) {
//                JsonObject subject = je.getAsJsonObject();
//                Map<String, String> m = new HashMap<>();
//                m.put("code", JsonUtils.getStringInfo(subject, "kcdm"));
//                m.put("id", JsonUtils.getStringInfo(subject, "kcid"));
//                m.put("name", JsonUtils.getStringInfo(subject, "kcmc"));
//                m.put("compulsory", JsonUtils.getStringInfo(subject, "kcxzmc"));
//                m.put("school", JsonUtils.getStringInfo(subject, "kkyxmc"));
//                m.put("credit", JsonUtils.getStringInfo(subject, "xf"));
//                m.put("period", JsonUtils.getStringInfo(subject, "xs"));
//                m.put("type", JsonUtils.getStringInfo(subject, "kclbmc"));
//                m.put("teacher", JsonUtils.getStringInfo(subject, "dgjsmc"));
//                m.put("xnxq", xn + xq);
//                //System.out.println(m);
//                result.add(m);
//            }
//
//            return result;
//        } catch (IOException e) {
//            new errorTableText("解析出错" + text, e).save(new SaveListener<String>() {
//                @Override
//                public void done(String s, BmobException e) {
//
//                }
//            });
//            throw EASException.getConnectErrorExpection();
//        }
//    }
//
//    @WorkerThread
//    public List<Map<String, String>> getGRKBData(String xn, String xq) throws EASException {
//        List<Map<String, String>> result = new ArrayList<>();
//        try {
//            Connection.Response r = Jsoup.connect("http://jw.hitsz.edu.cn/xszykb/queryxszykbzong")
//                    .timeout(timeout)
//                    .cookies(cookies)
//                    .headers(defaultRequestHeader)
//                    .header("X-Requested-With", "XMLHttpRequest")
//                    //.data("zc", "2")
//                    .data("xn", xn)
//                    .data("xq", xq)
//                    .ignoreContentType(true)
//                    .ignoreHttpErrors(true)
//                    .method(Connection.Method.POST)
//                    .execute();
//            String json = r.body();
//            String currentText = "";
//            try {
//                JsonArray jsonList = new JsonParser().parse(json).getAsJsonArray();
//                List<String> processedItems = new ArrayList<>();
//                for (JsonElement je : jsonList) {
//                    currentText = je.toString();
//                    JsonObject jo = je.getAsJsonObject();
//                    //JsonObject jo = new JsonParser().parse("{\"id\":null,\"xh\":null,\"xm\":null,\"xm_en\":null,\"xn\":null,\"xq\":null,\"zc\":null,\"qsjsz\":null,\"xqj\":null,\"jc\":null,\"jsjc\":null,\"ksjc\":null,\"zylx\":null,\"rwh\":null,\"czsj\":null,\"cddm\":null,\"cdmc\":null,\"cdmc_en\":null,\"skjs\":null,\"skjs_em\":null,\"kcdm\":null,\"kcmc\":null,\"kcmc_em\":null,\"ljkcdm\":null,\"ljkcmc\":null,\"ljkcmc_en\":null,\"glsjid\":null,\"xkfsdm\":null,\"zyxxms\":null,\"key\":\"xq3_jc\",\"kbxx\":\"【实验】通信电子线路实验\\n[13-13节][12-13周]\\n[(K403)通信工程综合实验室]\",\"kbxx_en\":null,\"xnxqmc\":null,\"xnxqmc_en\":null,\"bjmc\":null,\"bjmc_en\":null,\"sksj\":null,\"sksj_en\":null,\"sxbj\":null,\"xb\":\"8\"}").getAsJsonObject();
//                    String tm = JsonUtils.getStringInfo(jo, "KEY");
//                    Map<String, String> map = new HashMap<>();
//                    if (!TextUtils.isEmpty(tm) && tm.contains("xq") && tm.contains("jc")) {
//                        String[] twoInfo = tm.split("_");
//                        try {
//                            int dow = twoInfo[0].charAt(2) - '0';
//                            map.put("dow", String.valueOf(dow));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        try {
//                            String beginS = twoInfo[1].replaceAll("jc", "");
//                            if (!TextUtils.isEmpty(beginS) && TextTools.isNumber(beginS)) {
//                                int begin = Integer.parseInt(beginS) * 2 - 1;
//                                map.put("begin", String.valueOf(begin));
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    map.put("last", String.valueOf(2));
//                    analyseBlockText(map, JsonUtils.getStringInfo(jo, "SKSJ"));
//                    // System.out.println(map);
//                    if (!processedItems.contains(map.toString())) {
//                        processedItems.add(map.toString());
//                        if (map.get("begin") != null) {
//                            int begin = Integer.parseInt(map.get("begin"));
//                            if (begin > 0 && begin < 13) result.add(map);
//                        }
//                    }
//                    //String mainSTR = jo.get("kbxx").getAsString();
//                    //mainSTR = mainSTR.replaceAll("待生效", "");
//                    //String[] main = mainSTR.split("\n");
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                errorTableText et = new errorTableText(currentText, e);
//                et.save(new SaveListener<String>() {
//                    @Override
//                    public void done(String s, BmobException e) {
//                        if (e != null) {
//                            e.printStackTrace();
//                            Log.e("upload_error", e.toString());
//                        }
//                    }
//                });
//                e.printStackTrace();
//                throw EASException.newDialogMessageExpection("导入错误！已上传错误报告" + e.toString());
//            }
//            return result;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw EASException.getConnectErrorExpection();
//        }
//    }
//
//    private void analyseBlockText(Map<String, String> result, String text) throws Exception {
//        // text = "【实验】通信电子线路实验\\n[13-13节][12-13周]\\n[(K403)通信工程综合实验室]"
//        if (TextUtils.isEmpty(text)) return;
//        String name = null;
//        String teacher = null;
//        String classroom = null;
//        String specificTime = null;
//        String weekText = null;
//        text = text.replaceAll("\n", "");
//        List<String> inBoxes = new ArrayList<>();
//        List<String> outBoxes = new ArrayList<>();
//        StringBuilder sb = new StringBuilder();
//        boolean inBox = false;
//        for (int i = 0; i < text.length(); i++) {
//            if (text.charAt(i) == '[') {
//                if (!inBox && sb.length() > 0) {
//                    outBoxes.add(sb.toString());
//                }
//                sb = new StringBuilder();
//                inBox = true;
//            } else if (text.charAt(i) == ']') {
//                if (inBox && sb.length() > 0) {
//                    inBoxes.add(sb.toString());
//                }
//                sb = new StringBuilder();
//                inBox = false;
//            } else {
//                sb.append(text.charAt(i));
//            }
//        }
//
////        Log.e("inboxes", String.valueOf(inBoxes));
////        Log.e("outboxes", String.valueOf(outBoxes));
//        List<String> toRemove = new ArrayList<>();
//        for (String info : inBoxes) {
//            if (TextTools.containsNumber(info) && info.contains("周") ||
//                    TextTools.isNumber(info.replaceAll(",", "").replaceAll("-", "").replaceAll("单", "").replaceAll("双", ""))) {
//                weekText = info;
//                toRemove.add(info);
//            } else if (TextTools.containsNumber(info) && info.contains("节")) {
//                specificTime = info.replaceAll("节", "");
//                toRemove.add(info);
//            }
//        }
//        inBoxes.removeAll(toRemove);
//        if (inBoxes.size() > 1) {
//            teacher = inBoxes.get(0);
//            classroom = inBoxes.get(inBoxes.size() - 1);
//        } else if (inBoxes.size() == 1) {
//            classroom = inBoxes.get(0);
//        }
//        if (outBoxes.size() > 0) name = outBoxes.get(0);
//
//
//        if (weekText != null) {
//            StringBuilder weeks = new StringBuilder();
//            List<Integer> weekI = new ArrayList<>();
//            for (String wk : weekText.split(",")) {
//                boolean pairW = false;
//                boolean singW = false;
//                if (wk.contains("单")) {
//                    singW = true;
//                    wk = wk.replaceAll("单", "").replaceAll("周", "");
//                } else if (wk.contains("双")) {
//                    pairW = true;
//                    wk = wk.replaceAll("双", "").replaceAll("周", "");
//                } else wk = wk.replaceAll("周", "");
//                if (wk.contains("-")) {
//                    String[] ft = wk.split("-");
//                    int from = Integer.parseInt(ft[0]);
//                    int to = Integer.parseInt(ft[1]);
//                    for (int i = from; i <= to; i++) {
//                        if (pairW && i % 2 != 0 || singW && i % 2 == 0) continue;
//                        if (!weekI.contains(i)) weekI.add(i);
//                        //weeks.append(i+"").append(",");
//                    }
//                } else if (!weekI.contains(Integer.parseInt(wk)))
//                    weekI.add(Integer.parseInt(wk));
//            }
//            for (int i = 0; i < weekI.size(); i++) {
//                weeks.append(weekI.get(i) + "").append(i == weekI.size() - 1 ? "" : ",");
//            }
//            result.put("weeks", weeks.toString());
//        }
//
//        if (specificTime != null && specificTime.contains("-")) {
//            String[] spcf = specificTime.split("-");
//            if (spcf.length > 0) {
//                result.put("begin", String.valueOf(Integer.parseInt(spcf[0])));
//            }
//            if (spcf.length > 1) {
//                result.put("last", String.valueOf(Integer.parseInt(spcf[1]) - Integer.parseInt(spcf[0]) + 1));
//            }
//
//        }
//        result.put("name", name);
//        result.put("teacher", teacher);
//        result.put("classroom", classroom);
//
//    }
//
//    @WorkerThread
//    public Calendar getFirstDateOfCurriculum(String xn, String xq) throws EASException {
//        try {
//            Document s = Jsoup.connect("http://jw.hitsz.edu.cn/Xiaoli/queryMonthList")
//                    .timeout(timeout)
//                    .cookies(cookies)
//                    .headers(defaultRequestHeader)
//                    .header("X-Requested-With", "XMLHttpRequest")
//                    .data("zyw", "zh")
//                    .data("pxn", xn)
//                    .data("pxq", xq)
//                    .ignoreContentType(true)
//                    .ignoreHttpErrors(true)
//                    .post();
//            try {
//                String json = s.getElementsByTag("body").text();
//                JsonArray monthList = new JsonParser().parse(json).getAsJsonObject().get("monlist").getAsJsonArray();
//                if (monthList.size() == 0) throw EASException.newDialogMessageExpection("该学期尚未开放！");
//                JsonObject firstMon = monthList.get(0).getAsJsonObject();
//                int year = firstMon.get("yy").getAsInt();
//                int month = firstMon.get("mm").getAsInt();
//                JsonArray firstMonDays = firstMon.get("dszlist").getAsJsonArray();
//                int i;
//                for (i = 0; i < firstMonDays.size(); i++) {
//                    JsonObject aWeek = firstMonDays.get(i).getAsJsonObject();
//                    JsonElement attr = aWeek.get("xldjz");
//                    if (!attr.equals(JsonNull.INSTANCE)) break;
//                }
//                Calendar result = Calendar.getInstance();
//                result.set(Calendar.YEAR, year);
//                result.set(Calendar.MONTH, month - 1);
//                result.set(Calendar.WEEK_OF_MONTH, i + 1);
//                result.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
//                // System.out.println("YY:" + year + " MM:" + month + "  DD:" + result.get(Calendar.DAY_OF_MONTH));
//                return result;
//            } catch (JsonSyntaxException e) {
//                throw EASException.getLoginFailedExpection();
//            }
//        } catch (IOException e) {
//            throw EASException.getConnectErrorExpection();
//        }
//    }
//
//    @WorkerThread
//    public List<String> getTeacherOfChosenSubjects(String xn, String xq) throws EASException {
//        Log.e("gTO", "xn:" + xn);
//        List<String> result = new ArrayList<>();
//        try {
//            Document d = Jsoup.connect("http://jw.hitsz.edu.cn/Xsxk/queryYxkc")
//                    .timeout(timeout)
//                    .cookies(cookies)
//                    .headers(defaultRequestHeader)
//                    .header("X-Requested-With", "XMLHttpRequest")
//                    .header("RoleCode", "01")
//                    .ignoreContentType(true)
//                    .ignoreHttpErrors(true)
//                    .data("p_pylx", "1")
//                    .data("p_sfgldjr", "0")
//                    .data("p_sfredis", "0")
//                    .data("p_sfsyxkgwc", "0")
//                    .data("p_xn", xn) //学年
//                    .data("p_xq", xq) //学期
//                    .data("p_xnxq", xn + xq) //学年学期
//                    .data("p_dqxn", xn) //当前学年
//                    .data("p_dqxq", xq) //当前学期
//                    .data("p_dqxnxq", xn + xq) //当前学年学期
//                    .data("p_xkfsdm", "yixuan") //已选
//                    .data("p_sfhlctkc", "1")
//                    .data("p_sfhllrlkc", "1")
//                    .data("p_sfxsgwckb", "1")
//                    .post();
//            for (Element e : d.getElementsByTag("a")) {
//                String onclick = e.attr("onclick");
//                int from = onclick.indexOf("queryJsxx");
//                if (from >= 0) {
//                    String id = onclick.substring(onclick.indexOf("('", from) + 2, onclick.indexOf("')", from));
//                    result.add(id);
//
//                }
//            }
//            return result;
//        } catch (IOException e) {
//            throw EASException.getConnectErrorExpection();
//        }
//    }
//
//    @WorkerThread
//    public Map<String, String> getTeacherData(String teacherId) throws EASException {
//        Map<String, String> result = new HashMap<>();
//        try {
//            Document s = Jsoup.connect("http://jw.hitsz.edu.cn/Szgl/querySzglOneByjsid")
//                    .timeout(timeout)
//                    .cookies(cookies)
//                    .headers(defaultRequestHeader)
//                    .data("zghid", teacherId)
//                    .header("X-Requested-With", "XMLHttpRequest")
//                    .ignoreContentType(true)
//                    .ignoreHttpErrors(true)
//                    .post();
//            try {
//                String json = s.getElementsByTag("body").text();
//                JsonObject t = new JsonParser().parse(json).getAsJsonObject();
//                result.put("name", JsonUtils.getStringInfo(t, "jsxm"));
//                result.put("phone", JsonUtils.getStringInfo(t, "lxdh"));
//                result.put("email", JsonUtils.getStringInfo(t, "dzyx"));
//                result.put("detail", JsonUtils.getStringInfo(t, "jsjj"));
//                result.put("school", JsonUtils.getStringInfo(t, "glyxmc"));
//                result.put("gender", JsonUtils.getStringInfo(t, "xbm"));
//                result.put("id", teacherId);
//                return result;
//            } catch (JsonSyntaxException e) {
//                throw EASException.getFormatErrorException();
//            }
//        } catch (IOException e) {
//            throw EASException.getConnectErrorExpection();
//        }
//    }
//
//    @WorkerThread
//    public Map<String, String> getXKColumnTitles() throws EASException {
//        Map<String, String> valueToTitleMap = new HashMap<>();
//        Connection.Response r = null;
//
//        try {
//            Document getColunmLinkPage = Jsoup.connect("http://jw.hitsz.edu.cn/Xsxk/query/1")
//                    .cookies(cookies).headers(defaultRequestHeader).get();
//            Elements scripts = getColunmLinkPage.getElementsByTag("script");
//            String link = null;//"/pub/xkgl/xsxk/xsxkColumn-8c80d4e0fc6626ecbd098d98665e1a64.js";
//            for (Element e : scripts) {
//                if (e.attr("src").contains("xsxkColumn")) link = e.attr("src");
//            }
//            Log.e("link", "xx" + link);
//            r = Jsoup.connect("http://jw.hitsz.edu.cn" + link)
//                    .headers(defaultRequestHeader)
//                    .cookies(cookies)
//                    .ignoreHttpErrors(true)
//                    .ignoreContentType(true)
//                    .method(Connection.Method.GET)
//                    .execute();
//            String jsCodes = r.body();
//            JsonElement je = JsonUtils.jsToJson(jsCodes);
//            System.out.println(je);
//            for (Map.Entry<String, JsonElement> entry : je.getAsJsonObject().entrySet()) {
//                if (entry.getValue().isJsonObject()) {
//                    JsonElement zh = entry.getValue().getAsJsonObject().get("zh");
//                    if (!JsonNull.INSTANCE.equals(zh) && zh != null && zh.isJsonArray()) {
//                        for (JsonElement ma : zh.getAsJsonArray()) {
//                            if (!ma.isJsonObject() || !ma.getAsJsonObject().has("key") || !ma.getAsJsonObject().has("title"))
//                                continue;
//                            Matcher m2 = Pattern.compile("\\s*|\t|\r|\n").matcher(ma.getAsJsonObject().get("key").getAsString());
//                            valueToTitleMap.put(
//                                    m2.replaceAll(""),
//                                    ma.getAsJsonObject().get("title").getAsString()
//                            );
//                        }
//                    }
//                }
//            }
//
//
//            return valueToTitleMap;
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw EASException.getConnectErrorExpection();
//        }
//
//    }
//
//    @WorkerThread
//    public List<Map<String, String>> getXKList(String xn, String xq, String type, boolean filter_novacancy, boolean filter_conflict) throws EASException {
//        List<Map<String, String>> res = new ArrayList<>();
//        try {
//            Connection.Response content = Jsoup.connect("http://jw.hitsz.edu.cn/Xsxk/queryKxrw")
//                    .headers(defaultRequestHeader).method(Connection.Method.POST)
//                    .cookies(cookies)
//                    .header("X-Requested-With", "XMLHttpRequest")
//                    .ignoreContentType(true)
//                    .data("p_pylx", "1")
//                    .data("p_sfgldjr", "0")
//                    .data("p_sfredis", "0")
//                    .data("p_sfsyxkgwc", "0")
//                    .data("p_xn", xn)
//                    .data("p_xq", xq)
//                    .data("p_xnxq", xn + xq)
////                    .data("p_dqxn", xn)
////                    .data("p_dqxq", xq)
////                    .data("p_dqxnxq", xn + xq)
//                    .data("p_xkfsdm", type)
//                    .data("p_sfhlctkc", filter_conflict ? "1" : "0")
//                    .data("p_sfhllrlkc", filter_novacancy ? "1" : "0")
//                    .data("p_sfxsgwckb", "1")
//                    .data("pageNum", "1")
//                    .data("pageSize", "100")
//                    .execute();
//            //Log.e("filters",filter_conflict+","+filter_novacancy);
//            try {
//                String contentS = content.body();
//                //System.out.println(contentS);
//                JsonObject jo = new JsonParser().parse(contentS).getAsJsonObject();
//                if (jo.has("xsxkPage")) {
//                    JsonObject page = jo.get("xsxkPage").getAsJsonObject();
//                    Map<String, String> headerMap = new HashMap<String, String>();
//                    headerMap.put("header", "true");
//                    headerMap.put("page", page.toString());
//                    res.add(headerMap);
//                }
//                if (!jo.has("kxrwList")) return res;
//                JsonArray ja = jo.get("kxrwList").getAsJsonObject().get("list").getAsJsonArray();
//                for (JsonElement subject : ja) {
//                    Map<String, String> m = new HashMap<String, String>();
//                    for (Map.Entry<String, JsonElement> value : subject.getAsJsonObject().entrySet()) {
//                        if (!value.getValue().equals(JsonNull.INSTANCE))
//                            m.put(value.getKey(), value.getValue().getAsString());
//                    }
//                    //System.out.println(m);
//                    res.add(m);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw EASException.getFormatErrorException();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw EASException.getConnectErrorExpection();
//        }
//
//        return res;
//    }
//
//    @WorkerThread
//    public List<Map<String, String>> getYXList(String xn, String xq) throws EASException {
//        List<Map<String, String>> result = new ArrayList<>();
//        try {
//            Connection.Response r = Jsoup.connect("http://jw.hitsz.edu.cn/Xsxk/queryYxkc")
//                    .timeout(timeout)
//                    .cookies(cookies)
//                    .headers(defaultRequestHeader)
//                    .header("X-Requested-With", "XMLHttpRequest")
//                    .ignoreContentType(true)
//                    .ignoreHttpErrors(true)
//                    .data("p_pylx", "1")
//                    .data("p_sfgldjr", "0")
//                    .data("p_sfredis", "0")
//                    .data("p_sfsyxkgwc", "0")
//                    .data("p_xn", xn) //学年
//                    .data("p_xq", xq) //学期
//                    .data("p_xnxq", xn + xq) //学年学期
//                    .data("p_dqxn", xn) //当前学年
//                    .data("p_dqxq", xq) //当前学期
//                    .data("p_dqxnxq", xn + xq) //当前学年学期
//                    .data("p_xkfsdm", "yixuan") //已选
//                    .data("p_sfhlctkc", "0")
//                    .data("p_sfhllrlkc", "0")
//                    .data("p_sfxsgwckb", "1")
//                    .method(Connection.Method.POST)
//                    .execute();
//            String json = r.body();
//            JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
//            if (jo.has("xsxkPage")) {
//                JsonObject page = jo.get("xsxkPage").getAsJsonObject();
//                Map<String, String> headerMap = new HashMap<String, String>();
//                headerMap.put("header", "true");
//                headerMap.put("page", page.toString());
//                result.add(headerMap);
//            }
//            JsonArray yxkc = new JsonParser().parse(json).getAsJsonObject().get("yxkcList").getAsJsonArray();
//            for (JsonElement je : yxkc) {
//                JsonObject subject = je.getAsJsonObject();
//                Map<String, String> m = new HashMap<String, String>();
//                for (Map.Entry<String, JsonElement> value : subject.getAsJsonObject().entrySet()) {
//                    if (!value.getValue().equals(JsonNull.INSTANCE))
//                        m.put(value.getKey(), value.getValue().getAsString());
//                }
//                //System.out.println(m);
////                m.put("code", JsonUtils.getStringInfo(subject, "kcdm"));
////                m.put("name", JsonUtils.getStringInfo(subject, "kcmc"));
////                m.put("compulsory", JsonUtils.getStringInfo(subject, "kcxzmc"));
////                m.put("school", JsonUtils.getStringInfo(subject, "kkyxmc"));
////                m.put("credit", JsonUtils.getStringInfo(subject, "xf"));
////                m.put("period", JsonUtils.getStringInfo(subject, "xs"));
////                m.put("type", JsonUtils.getStringInfo(subject, "kclbmc"));
////                m.put("teacher", JsonUtils.getStringInfo(subject, "dgjsmc"));
////                m.put("xnxq", xn + xq);
//                //System.out.println(m);
//                result.add(m);
//            }
//
//            return result;
//        } catch (IOException e) {
//            throw EASException.getConnectErrorExpection();
//        }
//    }
//
//
//    @WorkerThread
//    public String xkOrTkAction(String xn, String xq, String type, String subjectType, String subjectId) throws EASException {
//        try {
//            String url = type.equals("tk") ? "http://jw.hitsz.edu.cn/Xsxk/tuike" : "http://jw.hitsz.edu.cn/Xsxk/addGouwuche";
//            Connection.Response r = Jsoup.connect(url)
//                    .headers(defaultRequestHeader)
//                    .cookies(cookies)
//                    .ignoreHttpErrors(true)
//                    .data("p_pylx", "1")
//                    .data("p_sfgldjr", "0")
//                    .data("p_sfredis", "0")
//                    .data("p_sfsyxkgwc", "0")
//                    .data("p_xktjz", "rwtjzyx")
//                    .data("p_xn", xn)
//                    .data("p_xq", xq)
//                    .data("p_xnxq", xn + xq)
////                    .data("p_dqxn",xn)
////                    .data("p_dqxq",xq)
////                    .data("p_dqxnxq",xn+xq)
//                    .data("p_xkfsdm", subjectType)
//                    .data("p_id", subjectId)
//                    .data("p_sfhlctkc", "0")
//                    .data("p_sfhllrlkc", "0")
//                    .data("p_sfxsgwckb", "1")
//                    .ignoreContentType(true)
//                    .method(Connection.Method.POST)
//                    .execute();
//            try {
//                String json = r.body();
//                Log.e("选课结果", json);
//                JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
//                return JsonUtils.getStringInfo(jo, "message");
//            } catch (JsonSyntaxException e) {
//                e.printStackTrace();
//                throw EASException.getFormatErrorException();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw EASException.getConnectErrorExpection();
//        }
////        pageNum: 2
////        pageSize: 13
//    }
//
//    @WorkerThread
//    public Map<String, Object> getSubjectDetail(String subjectId) throws EASException {
//        Map<String, Object> result = new HashMap<String, Object>();
//        try {
//            Connection.Response r = Jsoup.connect("http://jw.hitsz.edu.cn/kck/kcxxwh/xsckView")
//                    .cookies(cookies).headers(defaultRequestHeader)
//                    .method(Connection.Method.POST)
//                    .ignoreContentType(true)
//                    .data("kcsqid", "")
//                    .data("kcid", subjectId).execute();
//            String json = r.body();
//            try {
//                JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
//                JsonObject content = jo.get("content").getAsJsonObject();
//                List<Map<String, String>> xs = new ArrayList<>();
//                if (content.has("kcxxbgbEntity")) {
//                    JsonObject basicInfoRaw = jo.get("content").getAsJsonObject().get("kcxxbgbEntity").getAsJsonObject();
//                    Map<String, String> basicInfo = new HashMap<>();
//                    basicInfo.put("name", JsonUtils.getStringInfo(basicInfoRaw, "kcmc"));
//                    basicInfo.put("name_en", JsonUtils.getStringInfo(basicInfoRaw, "kcywmc"));
//                    basicInfo.put("credit", JsonUtils.getStringInfo(basicInfoRaw, "xf"));
//                    basicInfo.put("type", JsonUtils.getStringInfo(basicInfoRaw, "kclbmc"));
//                    basicInfo.put("department", JsonUtils.getStringInfo(basicInfoRaw, "kkxymc"));
//                    basicInfo.put("code", JsonUtils.getStringInfo(basicInfoRaw, "kcdm"));
//                    basicInfo.put("tag", JsonUtils.getStringInfo(basicInfoRaw, "bz"));
//                    basicInfo.put("lang", JsonUtils.getStringInfo(basicInfoRaw, "skyymc"));
//                    result.put("basicInfo", basicInfo);
//
//                    //显示学时信息在这个块里面
//                    Map<String, String> zxs = new HashMap<>();
//                    zxs.put("key", "总学时");
//                    zxs.put("value", JsonUtils.getStringInfo(basicInfoRaw, "xszxs"));
//                    Map<String, String> llks = new HashMap<>();
//                    llks.put("key", "显示理论课时");
//                    llks.put("value", JsonUtils.getStringInfo(basicInfoRaw, "xsllxs"));
//                    Map<String, String> syks = new HashMap<>();
//                    syks.put("key", "显示实验课时");
//                    syks.put("value", JsonUtils.getStringInfo(basicInfoRaw, "xssyxs"));
//                    xs.add(zxs);
//                    xs.add(llks);
//                    xs.add(syks);
//                }
//
//                if (content.has("kcdgbentity")) {
//                    Map<String, String> description = new HashMap<>();
//                    JsonObject descriptionRaw = jo.get("content").getAsJsonObject().get("kcdgbentity").getAsJsonObject();
//                    description.put("ch", JsonUtils.getStringInfo(descriptionRaw, "kczwjj"));
//                    description.put("en", JsonUtils.getStringInfo(descriptionRaw, "kcywjj"));
//                    description.put("file_ch_url", JsonUtils.getStringInfo(descriptionRaw, "kczwdgurl"));
//                    description.put("file_ch_name", JsonUtils.getStringInfo(descriptionRaw, "kczwdgwjm"));
//                    description.put("file_en_url", JsonUtils.getStringInfo(descriptionRaw, "kcywdgurl"));
//                    description.put("file_en_name", JsonUtils.getStringInfo(descriptionRaw, "kcywdgwjm"));
//                    result.put("description", description);
//                }
//                if (content.has("xsList")) {
//                    JsonArray xsRaw = jo.get("content").getAsJsonObject().get("xsList").getAsJsonArray();
//                    for (JsonElement je : xsRaw) {
//                        Map<String, String> xsjo = new HashMap<String, String>();
//                        xsjo.put("key", JsonUtils.getStringInfo(je.getAsJsonObject(), "mc"));
//                        xsjo.put("value", JsonUtils.getStringInfo(je.getAsJsonObject(), "xsxs"));
//                        xs.add(xsjo);
//                    }
//                }
//                result.put("xs", xs);
//                if (content.has("kctdwhEntityList")) {
//                    List<Map<String, String>> team = new ArrayList<>();
//                    JsonArray teamRaw = jo.get("content").getAsJsonObject().get("kctdwhEntityList").getAsJsonArray();
//                    for (JsonElement je : teamRaw) {
//                        Map<String, String> xsjo = new HashMap<String, String>();
//                        xsjo.put("id", JsonUtils.getStringInfo(je.getAsJsonObject(), "id"));
//                        xsjo.put("name", JsonUtils.getStringInfo(je.getAsJsonObject(), "jsxm"));
//                        xsjo.put("responsible", JsonUtils.getStringInfo(je.getAsJsonObject(), "sffzrmc"));
//                        xsjo.put("file", JsonUtils.getStringInfo(je.getAsJsonObject(), "filename"));
//                        xsjo.put("downloadFlag", JsonUtils.getStringInfo(je.getAsJsonObject(), "downflag"));
//                        team.add(xsjo);
//                    }
//                    result.put("team", team);
//                }
//                // Log.e("test", String.valueOf(result));
//                return result;
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw EASException.getFormatErrorException();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw EASException.getConnectErrorExpection();
//        }
//
//    }
//
//
//    /**
//     * 获取必修课进度信息
//     * 参数：xnxq:截止学年学期 eg:2019-20202
//     * 返回：Map
//     * required_number(credit) 要求门数/学分
//     * done_number(credit) 已完成门数/学分
//     **/
//    Map<String, String> getBXProgress(String xnxq) throws EASException {
//        Map<String, String> result = new HashMap<>();
//
//        Connection.Response r = null;
//        String xjid = null; //学籍id
//        String xh = null; //学号
//        String fah = null;//方案号
//        String nj = null;//年级
//        String pylx = null;//培养类型
//        /*获取上述信息*/
//        try {
//            r = Jsoup.connect("http://jw.hitsz.edu.cn/cjgl/cjzhtjcx/cjcx/getXss")
//                    .cookies(cookies).headers(defaultRequestHeader)
//                    .method(Connection.Method.POST)
//                    .ignoreContentType(true)
//                    .header("X-Requested-With", "XMLHttpRequest")
//                    .execute();
//            String json = r.body();
//            JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
//            JsonObject content = jo.get("content").getAsJsonArray().get(0).getAsJsonObject();
//            fah = JsonUtils.getStringInfo(content, "fah");
//            xjid = JsonUtils.getStringInfo(content, "xjid");
//            xh = JsonUtils.getStringInfo(content, "xh");
//            nj = JsonUtils.getStringInfo(content, "nj");
//            pylx = JsonUtils.getStringInfo(content, "pylx");
//        } catch (Exception ignored) {
//            throw EASException.getConnectErrorExpection();
//        }
//        /*请求条件完整后，获取必修课进度信息*/
//        JsonObject requestPayload = new JsonObject();
//        requestPayload.addProperty("fah", fah);
//        requestPayload.addProperty("jzxnxq", xnxq);
//        requestPayload.addProperty("nj", nj);
//        requestPayload.addProperty("pylx", pylx);
//        requestPayload.addProperty("xh", xh);
//        requestPayload.addProperty("xjid", xjid);
//        if (fah != null && xjid != null && xh != null) {
//            try {
//                r = Jsoup.connect("http://jw.hitsz.edu.cn/cjgl/cjzhtjcx/cjcx/queryBxkqk")
//                        .cookies(cookies).headers(defaultRequestHeader)
//                        .method(Connection.Method.POST)
//                        .ignoreContentType(true)
//                        .header("X-Requested-With", "XMLHttpRequest")
//                        .header("Content-Type", "application/json") //这里特殊
//                        .requestBody(requestPayload.toString()) //用payload传递参数
//                        .execute();
//                String json = r.body();
//                Log.e("json", json);
//                JsonObject content = new JsonParser().parse(json).getAsJsonObject().get("content").getAsJsonObject();
//                result.put("done_number", content.get("ywcms").getAsString());
//                result.put("done_credit", content.get("ywcxf").getAsString());
//                JsonObject required = content.get("yqmsxf").getAsJsonObject();
//                result.put("required_number", JsonUtils.getStringInfo(required, "YQMS"));
//                result.put("required_credit", JsonUtils.getStringInfo(required, "YQXF"));
//                result.put("xfj", JsonUtils.getStringInfo(required, "XFJ"));
//                result.put("xfjpm", JsonUtils.getStringInfo(required, "PM") + "/" + JsonUtils.getStringInfo(required, "ZYRS"));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//        } else {
//            throw EASException.getFormatErrorException();
//        }
//        return result;
//    }
//
//
//    /**
//     * 获取学分类别要求
//     * 参数：xnxq:截止学年学期 eg:2019-20202
//     * 返回：List<Map>
//     * name 名称
//     * required 要求
//     * done 已完成学分
//     **/
//    List<Map<String, String>> getCreditTypeRequirement() throws EASException {
//        List<Map<String, String>> result = new ArrayList<>();
//
//        Connection.Response r = null;
//        String xjid = null; //学籍id
//        String fah = null;//方案号
//        String pylx = null;//培养类型
//        /*获取上述信息*/
//        try {
//            r = Jsoup.connect("http://jw.hitsz.edu.cn/cjgl/cjzhtjcx/cjcx/getXss")
//                    .cookies(cookies).headers(defaultRequestHeader)
//                    .method(Connection.Method.POST)
//                    .ignoreContentType(true)
//                    .header("X-Requested-With", "XMLHttpRequest")
//                    .execute();
//            String json = r.body();
//            JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
//            JsonObject content = jo.get("content").getAsJsonArray().get(0).getAsJsonObject();
//            fah = JsonUtils.getStringInfo(content, "fah");
//            xjid = JsonUtils.getStringInfo(content, "xjid");
//            pylx = JsonUtils.getStringInfo(content, "pylx");
//        } catch (Exception ignored) {
//            throw EASException.getConnectErrorExpection();
//        }
//        /*请求条件完整后，获取必修课进度信息*/
//        JsonObject requestPayload = new JsonObject();
//        requestPayload.addProperty("fah", fah);
//        requestPayload.addProperty("pylx", pylx);
//        requestPayload.addProperty("current", "1");
//        requestPayload.addProperty("pageSize", "100");
//        requestPayload.addProperty("xjid", xjid);
//        if (fah != null && xjid != null) {
//            try {
//                r = Jsoup.connect("http://jw.hitsz.edu.cn/cjgl/cjzhtjcx/cjcx/queryXflbyq")
//                        .cookies(cookies).headers(defaultRequestHeader)
//                        .method(Connection.Method.POST)
//                        .ignoreContentType(true)
//                        .header("X-Requested-With", "XMLHttpRequest")
//                        .header("Content-Type", "application/json") //这里特殊
//                        .requestBody(requestPayload.toString()) //用payload传递参数
//                        .execute();
//                String json = r.body();
//                Log.e("json", json);
//                JsonObject content = new JsonParser().parse(json).getAsJsonObject().get("content").getAsJsonObject();
//                JsonArray list = content.get("list").getAsJsonArray();
//                for (JsonElement je : list) {
//                    Map<String, String> item = new HashMap<>();
//                    JsonObject jo = je.getAsJsonObject();
//                    item.put("name", JsonUtils.getStringInfo(jo, "kclbmc"));
//                    item.put("done", JsonUtils.getStringInfo(jo, "ywcxf"));
//                    item.put("required", JsonUtils.getStringInfo(jo, "yqwcxf"));
//                    item.put("togo", JsonUtils.getStringInfo(jo, "wwcxf"));
//                    result.add(item);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//        } else {
//            throw EASException.getFormatErrorException();
//        }
//        return result;
//    }
//
//
//    /**
//     * 获取教学楼列表
//     *
//     * @return
//     * @throws EASException
//     */
//    @WorkerThread
//    List<Map<String, String>> getJXLList() throws EASException {
//        List<Map<String, String>> res = new ArrayList<>();
//        try {
//            Connection.Response r = Jsoup.connect("http://jw.hitsz.edu.cn/pksd/queryjxlList")
//                    .cookies(cookies).headers(defaultRequestHeader)
//                    .method(Connection.Method.POST)
//                    .ignoreContentType(true)
//                    .header("X-Requested-With", "XMLHttpRequest")
//                    .execute();
//            JsonArray ja = new JsonParser().parse(r.body()).getAsJsonArray();
//            for (JsonElement je : ja) {
//                JsonObject jo = je.getAsJsonObject();
//                HashMap<String, String> hm = new HashMap<String, String>();
//                hm.put("name", JsonUtils.getStringInfo(jo, "MC"));
//                hm.put("code", JsonUtils.getStringInfo(jo, "DM"));
//                res.add(hm);
//            }
//        } catch (Exception e) {
//            throw EASException.getConnectErrorExpection();
//        }
//        return res;
//    }
//
//
//    /**
//     * 查询空教室信息
//     *
//     * @return
//     */
//
//    List<JsonObject> queryKJS(String xn, String xq, String jxl, String[] weeks) throws EASException {
//        List<JsonObject> res = new ArrayList<>();
//        StringBuilder sb = new StringBuilder();
//        char[] weekZeros = "000000000000000000000000000000000000000000000000".toCharArray();
//        try {
//            for (int i = 0; i < weeks.length; i++) {
//                sb.append(weeks[i]);
//                weekZeros[Integer.parseInt(weeks[i])] = '1';
//                if (i != weeks.length - 1) sb.append(",");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            Connection.Response r = Jsoup.connect("http://jw.hitsz.edu.cn/cdkb/querycdzyleftzhou")
//                    .cookies(cookies).headers(defaultRequestHeader)
//                    .method(Connection.Method.POST)
//                    .ignoreContentType(true)
//                    .header("X-Requested-With", "XMLHttpRequest")
//                    .data("qsjsz", sb.toString())
//                    .data("pxn", xn)
//                    .data("pxq", xq)
//                    .data("jxl", jxl)
//                    .data("zc", String.valueOf(weekZeros))
//                    .execute();
//            JsonArray ja = new JsonParser().parse(r.body()).getAsJsonArray();
//            for (JsonElement je : ja) {
//                JsonObject hm = new JsonObject();
//                JsonObject jo = je.getAsJsonObject();
//                hm.addProperty("code", JsonUtils.getStringInfo(jo, "DM"));
//                hm.addProperty("name", JsonUtils.getStringInfo(jo, "MC"));
//                hm.addProperty("jtjs", JsonUtils.getStringInfo(jo, "SFJTJS"));
//                hm.addProperty("kj", JsonUtils.getStringInfo(jo, "SFKJ"));
//                hm.addProperty("seats", JsonUtils.getStringInfo(jo, "ZWS"));
//                hm.add("list", new JsonArray());
//                res.add(hm);
//            }
//
//            Connection.Response r2 = Jsoup.connect("http://jw.hitsz.edu.cn/cdkb/querycdzyrightzhou")
//                    .cookies(cookies).headers(defaultRequestHeader)
//                    .method(Connection.Method.POST)
//                    .ignoreContentType(true)
//                    .header("X-Requested-With", "XMLHttpRequest")
//                    .data("qsjsz", sb.toString())
//                    .data("pxn", xn)
//                    .data("pxq", xq)
//                    .data("jxl", jxl)
//                    .data("zc", String.valueOf(weekZeros))
//                    .execute();
//            JsonArray jar = new JsonParser().parse(r2.body()).getAsJsonArray();
//            // Log.e("jar", String.valueOf(jar));
//            for (JsonElement je : jar) {
//                JsonObject jo = je.getAsJsonObject();
//                for (JsonObject j : res) {
//                    if (j.get("code").equals(jo.get("CDDM"))) {
//                        jo.remove("CDDM");
//                        j.get("list").getAsJsonArray().add(jo);
//                        break;
//                    }
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return res;
//    }
//
//    public HashMap<String, String> getCookies() {
//        return cookies;
//    }
//
//
//    public void loadCookies(HashMap hm) {
//        cookies.clear();
//        cookies.putAll(hm);
//    }
//
//}
