package com.kenny.raspisanie;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

public class Parse2Service extends Service {

    Task t;
    NotificationReceiver nr;

    public void onCreate() {
        nr = new NotificationReceiver();
        nr.log("service onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        nr.log("service onStartCommand");
        t = new Task();
        t.execute();
        return super.onStartCommand(intent, flags, startId);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    class Task extends AsyncTask<Void, Void, Void> {
        String[][][] arr = new String[100][100][100];
        String[][][] arr_1 = new String[100][100][100];
        String[][][] arr_2 = new String[100][100][100];
        int i, j, k,i_1,j_1,k_1,i_2,j_2,k_2;
        Elements text1, text2, text3,text1_1,text2_1,text3_1,text1_2,text2_2,text3_2;
        String text,date,text_1,date_1,text_2,date_2;
        String url1,url2,url3,d1,d2,d3;
        int date1,date2,date3;
        String res;

        public Document load(String url) {
            try {
                StringBuilder sb = new StringBuilder();
                URL pageURL = new URL(url);
                String inputLine;
                URLConnection uc = pageURL.openConnection();
                BufferedReader buff = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                while ((inputLine = buff.readLine()) != null) {
                    sb.append(inputLine);
                }
                buff.close();
                return Jsoup.parse(sb.toString());
            } catch (IOException e) {
                nr.log("parse error load "+url);
                return null;
            }
        }
        SharedPreferences prefs;
        protected Void doInBackground(Void... params) {
            Document doc_title;
            Document doc;
            Document doc_1;
            Document doc_2;
            SharedPreferences sPref;
            SharedPreferences.Editor ed;
            prefs = getSharedPreferences("firstrun",MODE_PRIVATE);
            String brand = Build.BRAND;
            String model = Build.MODEL;
            String version = String.valueOf(Build.VERSION.SDK_INT);
            brand = brand.replaceAll(" ","_");
            model = model.replaceAll(" ","_");
            nr.log("http://bgaek.by/rasp/stats.php?brand="+brand+"&model="+model+"&version="+version+"&otdel=str");
            load("http://bgaek.by/rasp/stats.php?brand="+brand+"&model="+model+"&version="+version+"&otdel=str");
            doc_title = load("http://bgaek.by/category/%D1%80%D0%B0%D1%81%D0%BF%D0%B8%D1%81%D0%B0%D0%BD%D0%B8%D0%B5/stroi-otdel/");
            nr.traffic(getApplicationContext(), String.valueOf(doc_title).length());
            if (doc_title != null) {
                i = 0;
                text = doc_title.select("main.content").html();
                text2 = Jsoup.parse(text).select("a");
                for (Element temp : text2) {
                    if (i == 0) {
                        url1 = temp.attr("abs:href");
                        nr.log("s0 "+url1);
                        date1 = nr.date_net(temp.text());
                    }
                    if (i == 4) {
                        url2 = temp.attr("abs:href");
                        nr.log("s4 "+url2);
                        date2 = nr.date_net(temp.text());
                    }
                    if (i == 8) {
                        url3 = temp.attr("abs:href");
                        nr.log("b8 "+url3);
                        date3 = nr.date_net(temp.text());
                    }
                    i++;
                }
                nr.log("service title parse ok");
                nr.log(date3 + " - " + nr.date(getApplicationContext(), 3));
                nr.log("first run "+String.valueOf(prefs.getBoolean("isFirstRun", true)));
                if (date3 != nr.date(getApplicationContext(), 3)||(prefs.getBoolean("isFirstRun", true))) {
                    //111111111
                    doc = load(url1);
                    nr.traffic(getApplicationContext(), String.valueOf(doc).length());
                    if (doc != null) {
                        text = doc.select("article").html();
                        text1 = Jsoup.parse(text).select("table");
                        date = Jsoup.parse(text).getElementsByClass("entry-title").text();
                        i = 0;
                        for (Element temp : text1) {
                            text2 = Jsoup.parse(String.valueOf(temp)).select("tr");
                            j = 0;
                            for (Element temp2 : text2) {
                                text3 = Jsoup.parse("<table>" + String.valueOf(temp2) + "</table>").select("td");
                                k = 0;
                                for (Element temp3 : text3) {
                                    arr[i][j][k] = temp3.text();
                                    //nr.log(i+":"+j+":"+k+"  "+temp3.text());
                                    k++;
                                }
                                j++;
                            }
                            i++;
                        }
                        nr.log("parse 1");
                        for (int ii = 0; ii < i; ii++) {
                            if (arr[ii][0][2] != null) {
                                sPref = getSharedPreferences(arr[ii][0][1], MODE_PRIVATE);
                                ed = sPref.edit();
                                ed.putString("date", date);
                                d1 = date;
                                ed.putString("p1", arr[ii][1][1]);
                                ed.putString("p1_k", arr[ii][1][2]);
                                ed.putString("p2", arr[ii][2][1]);
                                ed.putString("p2_k", arr[ii][2][2]);
                                ed.putString("p3", arr[ii][3][1]);
                                ed.putString("p3_k", arr[ii][3][2]);
                                ed.putString("p4", arr[ii][4][1]);
                                ed.putString("p4_k", arr[ii][4][2]);
                                ed.putString("p5", arr[ii][5][1]);
                                ed.putString("p5_k", arr[ii][5][2]);
                                ed.apply();
                                sPref = getSharedPreferences(arr[ii][0][2], MODE_PRIVATE);
                                ed = sPref.edit();
                                ed.putString("date", date);
                                d1 = date;
                                ed.putString("p1", arr[ii][1][3]);
                                ed.putString("p1_k", arr[ii][1][4]);
                                ed.putString("p2", arr[ii][2][3]);
                                ed.putString("p2_k", arr[ii][2][4]);
                                ed.putString("p3", arr[ii][3][3]);
                                ed.putString("p3_k", arr[ii][3][4]);
                                ed.putString("p4", arr[ii][4][3]);
                                ed.putString("p4_k", arr[ii][4][4]);
                                ed.putString("p5", arr[ii][5][3]);
                                ed.putString("p5_k", arr[ii][5][4]);
                                ed.apply();
                            } else {
                                sPref = getSharedPreferences(arr[ii][0][1], MODE_PRIVATE);
                                ed = sPref.edit();
                                ed.putString("date", date);
                                d1 = date;
                                ed.putString("p1", arr[ii][1][1]);
                                ed.putString("p1_k", arr[ii][1][2]);
                                ed.putString("p2", arr[ii][2][1]);
                                ed.putString("p2_k", arr[ii][2][2]);
                                ed.putString("p3", arr[ii][3][1]);
                                ed.putString("p3_k", arr[ii][3][2]);
                                ed.putString("p4", arr[ii][4][1]);
                                ed.putString("p4_k", arr[ii][4][2]);
                                ed.putString("p5", arr[ii][5][1]);
                                ed.putString("p5_k", arr[ii][5][2]);
                                ed.apply();
                            }
                        }
                        nr.log("put 1");
                    }
                    //2222222222222
                    doc_1 = load(url2);
                    nr.traffic(getApplicationContext(), String.valueOf(doc_1).length());
                    if (doc_1 != null) {
                        text_1 = doc_1.select("article").html();
                        text1_1 = Jsoup.parse(text_1).select("table");
                        date_1 = Jsoup.parse(text_1).getElementsByClass("entry-title").text();
                        i_1 = 0;
                        for (Element temp : text1_1) {
                            text2_1 = Jsoup.parse(String.valueOf(temp)).select("tr");
                            j_1 = 0;
                            for (Element temp2 : text2_1) {
                                text3_1 = Jsoup.parse("<table>" + String.valueOf(temp2) + "</table>").select("td");
                                k_1 = 0;
                                for (Element temp3 : text3_1) {
                                    arr_1[i_1][j_1][k_1] = temp3.text();
                                    //nr.log(i+":"+j+":"+k+"  "+temp3.text());
                                    k_1++;
                                }
                                j_1++;
                            }
                            i_1++;
                        }
                        nr.log("parse 2");
                        for (int ii = 0; ii < i_1; ii++) {
                            if (arr_1[ii][0][2] != null) {
                                sPref = getSharedPreferences(arr_1[ii][0][1], MODE_PRIVATE);
                                ed = sPref.edit();
                                ed.putString("date_1", date_1);
                                d2 = date_1;
                                ed.putString("p1_1", arr_1[ii][1][1]);
                                ed.putString("p1_k_1", arr_1[ii][1][2]);
                                ed.putString("p2_1", arr_1[ii][2][1]);
                                ed.putString("p2_k_1", arr_1[ii][2][2]);
                                ed.putString("p3_1", arr_1[ii][3][1]);
                                ed.putString("p3_k_1", arr_1[ii][3][2]);
                                ed.putString("p4_1", arr_1[ii][4][1]);
                                ed.putString("p4_k_1", arr_1[ii][4][2]);
                                ed.putString("p5_1", arr_1[ii][5][1]);
                                ed.putString("p5_k_1", arr_1[ii][5][2]);
                                ed.apply();
                                sPref = getSharedPreferences(arr_1[ii][0][2], MODE_PRIVATE);
                                ed = sPref.edit();
                                ed.putString("date_1", date_1);
                                d2 = date_1;
                                ed.putString("p1_1", arr_1[ii][1][3]);
                                ed.putString("p1_k_1", arr_1[ii][1][4]);
                                ed.putString("p2_1", arr_1[ii][2][3]);
                                ed.putString("p2_k_1", arr_1[ii][2][4]);
                                ed.putString("p3_1", arr_1[ii][3][3]);
                                ed.putString("p3_k_1", arr_1[ii][3][4]);
                                ed.putString("p4_1", arr_1[ii][4][3]);
                                ed.putString("p4_k_1", arr_1[ii][4][4]);
                                ed.putString("p5_1", arr_1[ii][5][3]);
                                ed.putString("p5_k_1", arr_1[ii][5][4]);
                                ed.apply();
                            } else {
                                sPref = getSharedPreferences(arr_1[ii][0][1], MODE_PRIVATE);
                                ed = sPref.edit();
                                ed.putString("date_1", date_1);
                                d2 = date_1;
                                ed.putString("p1_1", arr_1[ii][1][1]);
                                ed.putString("p1_k_1", arr_1[ii][1][2]);
                                ed.putString("p2_1", arr_1[ii][2][1]);
                                ed.putString("p2_k_1", arr_1[ii][2][2]);
                                ed.putString("p3_1", arr_1[ii][3][1]);
                                ed.putString("p3_k_1", arr_1[ii][3][2]);
                                ed.putString("p4_1", arr_1[ii][4][1]);
                                ed.putString("p4_k_1", arr_1[ii][4][2]);
                                ed.putString("p5_1", arr_1[ii][5][1]);
                                ed.putString("p5_k_1", arr_1[ii][5][2]);
                                ed.apply();
                            }
                        }
                        nr.log("put 2");
                    }
                    //333333333333333
                    doc_2 = load(url3);
                    nr.traffic(getApplicationContext(), String.valueOf(doc_2).length());
                    if (doc_2 != null) {
                        text_2 = doc_2.select("article").html();
                        text1_2 = Jsoup.parse(text_2).select("table");
                        date_2 = Jsoup.parse(text_2).getElementsByClass("entry-title").text();
                        i_2 = 0;
                        for (Element temp : text1_2) {
                            text2_2 = Jsoup.parse(String.valueOf(temp)).select("tr");
                            j_2 = 0;
                            for (Element temp2 : text2_2) {
                                text3_2 = Jsoup.parse("<table>" + String.valueOf(temp2) + "</table>").select("td");
                                k_2 = 0;
                                for (Element temp3 : text3_2) {
                                    arr_2[i_2][j_2][k_2] = temp3.text();
                                    //nr.log(i+":"+j+":"+k+"  "+temp3.text());
                                    k_2++;
                                }
                                j_2++;
                            }
                            i_2++;
                        }
                        nr.log("parse 3");
                        for (int ii = 0; ii < i_2; ii++) {
                            if (arr_2[ii][0][2] != null) {
                                sPref = getSharedPreferences(arr_2[ii][0][1], MODE_PRIVATE);
                                ed = sPref.edit();
                                ed.putString("date_2", date_2);
                                d3 = date_2;
                                ed.putString("p1_2", arr_2[ii][1][1]);
                                ed.putString("p1_k_2", arr_2[ii][1][2]);
                                ed.putString("p2_2", arr_2[ii][2][1]);
                                ed.putString("p2_k_2", arr_2[ii][2][2]);
                                ed.putString("p3_2", arr_2[ii][3][1]);
                                ed.putString("p3_k_2", arr_2[ii][3][2]);
                                ed.putString("p4_2", arr_2[ii][4][1]);
                                ed.putString("p4_k_2", arr_2[ii][4][2]);
                                ed.putString("p5_2", arr_2[ii][5][1]);
                                ed.putString("p5_k_2", arr_2[ii][5][2]);
                                ed.apply();
                                sPref = getSharedPreferences(arr_2[ii][0][2], MODE_PRIVATE);
                                ed = sPref.edit();
                                ed.putString("date_2", date_2);
                                d3 = date_2;
                                ed.putString("p1_2", arr_2[ii][1][3]);
                                ed.putString("p1_k_2", arr_2[ii][1][4]);
                                ed.putString("p2_2", arr_2[ii][2][3]);
                                ed.putString("p2_k_2", arr_2[ii][2][4]);
                                ed.putString("p3_2", arr_2[ii][3][3]);
                                ed.putString("p3_k_2", arr_2[ii][3][4]);
                                ed.putString("p4_2", arr_2[ii][4][3]);
                                ed.putString("p4_k_2", arr_2[ii][4][4]);
                                ed.putString("p5_2", arr_2[ii][5][3]);
                                ed.putString("p5_k_2", arr_2[ii][5][4]);
                                ed.apply();
                            } else {
                                sPref = getSharedPreferences(arr_2[ii][0][1], MODE_PRIVATE);
                                ed = sPref.edit();
                                ed.putString("date_2", date_2);
                                d3 = date_2;
                                ed.putString("p1_2", arr_2[ii][1][1]);
                                ed.putString("p1_k_2", arr_2[ii][1][2]);
                                ed.putString("p2_2", arr_2[ii][2][1]);
                                ed.putString("p2_k_2", arr_2[ii][2][2]);
                                ed.putString("p3_2", arr_2[ii][3][1]);
                                ed.putString("p3_k_2", arr_2[ii][3][2]);
                                ed.putString("p4_2", arr_2[ii][4][1]);
                                ed.putString("p4_k_2", arr_2[ii][4][2]);
                                ed.putString("p5_2", arr_2[ii][5][1]);
                                ed.putString("p5_k_2", arr_2[ii][5][2]);
                                ed.apply();
                            }
                            nr.log("put 3");
                        }
                    }
                    nr.log("service parse ok");
                    //Новое расписание загружено
                    res = "sio";
                } else {
                    //Новое расписание отсутствует на сайте
                    res = "sis";
                }
            } else {
                //Отсутсвует подключение к интернету загрузка с кэша
                res = "n";
            }
            prefs.edit().putBoolean("isFirstRun", false).apply();
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Intent intent = new Intent("com.kenny.raspisanie");
            nr.log("parse send start "+res);
            intent.putExtra("text", res);
            sendBroadcast(intent);
        }
    }
}