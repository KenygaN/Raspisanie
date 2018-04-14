package com.kenny.raspisanie;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class NotificationReceiver extends BroadcastReceiver {

    SharedPreferences sp;
    AlarmManager am;
    Intent intent;
    PendingIntent pi;
    Calendar c;
    PowerManager pm;
    PowerManager.WakeLock wl;
    SharedPreferences sPref, tr;
    SharedPreferences.Editor tred;
    String ringtonePath;
    int small_screen = 0;
    int NOTIFY_ID = 1;
    boolean sound = true, vibrate = true;
    int hour, min, ned, sec, tec, i, p, next, mes, day, col_par = 3;
    boolean[] pars = {true, true, true, false, false};
    int[] dn = {450,
            500, 545, 555, 600,
            610, 655, 665, 710,
            750, 795, 805, 850,
            860, 905, 915, 960,
            970, 1015, 1025, 1070};
    int[] sb = {450,
            500, 545, 550, 595,
            605, 650, 655, 700,
            710, 755, 760, 805,
            815, 860, 865, 910,
            910, 910, 910, 910};
    int[] a = {0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,};
    String[] dn_t = {"", "8.20", "9.05", "9.15", "10.00",
            "10.10", "10.55", "11.05", "11.50",
            "12.30", "13.15", "13.25", "14.10",
            "14.20", "15.05", "15.15", "16.00",
            "16.10", "16.55", "17.05", "17.50"};
    String[] sb_t = {"", "8.20", "9.05", "9.10", "9.55",
            "10.05", "10.50", "10.55", "11.40",
            "11.50", "12.35", "12.40", "13.25",
            "13.35", "14.20", "14.25", "15.10",
            "15.20", "16.05", "16.10", "16.55"};
    String[] a_t;
    String[] par = {"", "Первая пара", "Вторая пара", "Третья пара", "Четвертая пара", "Пятая пара"};

    public int tec_date() {
        c = Calendar.getInstance();
        c.setTime(new Date());
        mes = c.get(Calendar.MONTH) + 1;
        day = c.get(Calendar.DAY_OF_MONTH);
        return mes * 30 + day;
    }

    public void traffic(Context context, int s) {
        tr = context.getSharedPreferences("traffic", MODE_PRIVATE);
        tred = tr.edit();
        int t = tr.getInt("col", 0);
        tred.putInt("col", t + s);
        tred.apply();
    }

    public void log(String s) {
        Log.d("log", time() + " " + s);
        /*if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return;
        }
        try {
            BufferedWriter bw = new BufferedWriter(
                    new FileWriter(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/rasp_log.txt", true));
            bw.write(time()+" "+s+"\n");
            bw.close();
        } catch (IOException e) {
            //Log.d("log",time()+" notification error sdcard");
        }*/
    }

    public int date(Context context, int s) {
        int ret = 0, i;
        sPref = context.getSharedPreferences("groups", MODE_PRIVATE);
        String gr = sPref.getString("group", "");
        sPref = context.getSharedPreferences(gr, MODE_PRIVATE);
        switch (s) {
            case 1:
                i = 0;
                String date1 = sPref.getString("date", "");
                date1 = date1.replaceAll("[А-Яа-я() ]", "");
                if (date1.matches("[0-9.]*") && date1.length() != 0) {
                    for (String temp : date1.split("[.]", 2)) {
                        if (i == 0) {
                            if (String.valueOf(temp.charAt(0)).compareTo("0") == 0) {
                                ret += Integer.parseInt(String.valueOf(temp.charAt(1)));
                            } else {
                                ret += Integer.parseInt(temp);
                            }
                        } else {
                            if (String.valueOf(temp.charAt(0)).compareTo("0") == 0) {
                                ret += Integer.parseInt(String.valueOf(temp.charAt(1))) * 30;
                            } else {
                                ret += Integer.parseInt(temp) * 30;
                            }
                        }
                        i++;
                    }
                }
                break;
            case 2:
                i = 0;
                String date2 = sPref.getString("date_1", "");
                date2 = date2.replaceAll("[А-Яа-я() ]", "");
                if (date2.matches("[0-9.]*") && date2.length() != 0) {
                    for (String temp : date2.split("[.]", 2)) {
                        if (i == 0) {
                            if (String.valueOf(temp.charAt(0)).compareTo("0") == 0) {
                                ret += Integer.parseInt(String.valueOf(temp.charAt(1)));
                            } else {
                                ret += Integer.parseInt(temp);
                            }
                        } else {
                            if (String.valueOf(temp.charAt(0)).compareTo("0") == 0) {
                                ret += Integer.parseInt(String.valueOf(temp.charAt(1))) * 30;
                            } else {
                                ret += Integer.parseInt(temp) * 30;
                            }
                        }
                        i++;
                    }
                }
                break;
            case 3:
                i = 0;
                String date3 = sPref.getString("date_2", "");
                date3 = date3.replaceAll("[А-Яа-я() ]", "");
                if (date3.matches("[0-9.]*") && date3.length() != 0) {
                    for (String temp : date3.split("[.]", 2)) {
                        if (i == 0) {
                            if (String.valueOf(temp.charAt(0)).compareTo("0") == 0) {
                                ret += Integer.parseInt(String.valueOf(temp.charAt(1)));
                            } else {
                                ret += Integer.parseInt(temp);
                            }
                        } else {
                            if (String.valueOf(temp.charAt(0)).compareTo("0") == 0) {
                                ret += Integer.parseInt(String.valueOf(temp.charAt(1))) * 30;
                            } else {
                                ret += Integer.parseInt(temp) * 30;
                            }
                        }
                        i++;
                    }
                }
                break;
        }
        return ret;
    }

    public int date_net(String s) {
        int ret = 0, j = 0;
        String date1 = s;
        date1 = date1.replaceAll("[А-Яа-я() ]", "");
        if (date1.matches("[0-9.]*") && date1.length() != 0) {
            for (String temp : date1.split("[.]", 2)) {
                if (j == 0) {
                    if (String.valueOf(temp.charAt(0)).compareTo("0") == 0) {
                        ret += Integer.parseInt(String.valueOf(temp.charAt(1)));
                    } else {
                        ret += Integer.parseInt(temp);
                    }
                } else {
                    if (String.valueOf(temp.charAt(0)).compareTo("0") == 0) {
                        ret += Integer.parseInt(String.valueOf(temp.charAt(1))) * 30;
                    } else {
                        ret += Integer.parseInt(temp) * 30;
                    }
                }
                j++;
            }
        }
        return ret;
    }

    public void pref(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean sound_pref = sp.getBoolean("sound_pref", true);
        Boolean vibrate_pref = sp.getBoolean("vibrate_pref", true);
        Boolean small_pref = sp.getBoolean("small_pref", false);
        ringtonePath = sp.getString("sound_pref_custom", "content://setting/system/notification_sound");
        if (small_pref) {
            small_screen = 1;
        } else {
            small_screen = 0;
        }
        sound = sound_pref;
        vibrate = vibrate_pref;
    }



    public String time() {
        c = Calendar.getInstance();
        c.setTime(new Date());
        hour = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);
        if ((c.get(Calendar.DAY_OF_WEEK)) == 1)
            ned = 7;
        else
            ned = c.get(Calendar.DAY_OF_WEEK) - 1;
        sec = c.get(Calendar.SECOND);
        tec = (hour * 60) + min;
        return c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND) + " ";
    }

    public int mill(int a) {
        return a * 60 * 1000;
    }

    public void show_notify(Context context, String title, String text, int mute) { //, String bigtext){
        hide_notify(context);
        Intent notificationIntent = new Intent(context, FirstActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        int s = Notification.FLAG_AUTO_CANCEL;
        if (vibrate && mute == 0) s += Notification.DEFAULT_VIBRATE;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        if (small_screen == 1) {
            builder.setContentTitle("Расписание");
            builder.setContentText(title);
            builder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(title + "\n" + text));
        } else {
            builder.setContentTitle(title);
            builder.setContentText(text);
            builder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(text));
        }
        if (sound && mute == 0) builder.setSound(Uri.parse(ringtonePath));
        builder.setContentIntent(contentIntent);
        builder.setDefaults(s);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, notification);
    }

    public void hide_notify(Context context) {
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFY_ID);
    }

    public void set_notify(final Context context, int mute) {
        if (col_par == 1 && tec >= a[4]) {
            if (mute == 0) {
                show_notify(context, "Пары закончились", "нажмите на уведомление для загрузки нового расписания", mute);
            }
        }
        if (col_par == 2 && tec >= a[8]) {
            if (mute == 0) {
                show_notify(context, "Пары закончились", "нажмите на уведомление для загрузки нового расписания", mute);
            }
        }
        if (col_par == 3 && tec >= a[12]) {
            if (mute == 0) {
                show_notify(context, "Пары закончились", "нажмите на уведомление для загрузки нового расписания", mute);
            }
        }
        if (col_par == 4 && tec >= a[16]) {
            if (mute == 0) {
                show_notify(context, "Пары закончились", "нажмите на уведомление для загрузки нового расписания", mute);
            }
        }
        if (col_par == 5 && tec >= a[20]) {
            if (mute == 0) {
                show_notify(context, "Пары закончились", "нажмите на уведомление для загрузки нового расписания", mute);
            }
        }
        if (col_par >= 1) {
            if (tec >= a[0] && tec <= a[1] - 1 && pars[0])
                show_notify(context, "Доброе утро", "след. пара " + par[1] + ", начинается в " + a_t[1], mute);
            if (tec >= a[1] && tec <= a[2] - 1 && pars[0])
                if (col_par == 1)
                    show_notify(context, par[1], "Первый урок, заканчивается в " + a_t[2], mute);
                else
                    show_notify(context, par[1], "Первый урок, заканчивается в " + a_t[2] + " \nслед. пара " + par[2], mute);
            if (tec >= a[2] && tec <= a[3] - 1 && pars[0])
                if (col_par == 1)
                    show_notify(context, par[1], "Перемена, заканчивается в " + a_t[3], mute);
                else
                    show_notify(context, par[1], "Перемена, заканчивается в " + a_t[3] + " \nслед. пара " + par[2], mute);
            if (tec >= a[3] && tec <= a[4] - 1 && pars[0])
                if (col_par == 1)
                    show_notify(context, par[1], "Второй урок, заканчивается в " + a_t[4], mute);
                else
                    show_notify(context, par[1], "Второй урок, заканчивается в " + a_t[4] + " \nслед. пара " + par[2], mute);
        }
        if (col_par >= 2) {
            if (tec >= a[4] && tec <= a[5] - 1 && pars[1])
                show_notify(context, "Перемена, заканчивается в " + a_t[5], "след. пара " + par[2], mute);
            if (tec >= a[5] && tec <= a[6] - 1 && pars[1])
                if (col_par == 2)
                    show_notify(context, par[2], "Первый урок, заканчивается в " + a_t[6], mute);
                else
                    show_notify(context, par[2], "Первый урок, заканчивается в " + a_t[6] + " \nслед. пара " + par[3], mute);
            if (tec >= a[6] && tec <= a[7] - 1 && pars[1])
                if (col_par == 2)
                    show_notify(context, par[2], "Перемена, заканчивается в " + a_t[7], mute);
                else
                    show_notify(context, par[2], "Перемена, заканчивается в " + a_t[7] + " \nслед. пара " + par[3], mute);
            if (tec >= a[7] && tec <= a[8] - 1 && pars[1])
                if (col_par == 2)
                    show_notify(context, par[2], "Второй урок, заканчивается в " + a_t[8], mute);
                else
                    show_notify(context, par[2], "Второй урок, заканчивается в " + a_t[8] + " \nслед. пара " + par[3], mute);
        }
        if (col_par >= 3) {
            if (tec >= a[8] && tec <= a[9] - 1 && pars[2])
                show_notify(context, "Перемена, окончание " + a_t[9], "след. пара " + par[3], mute);
            if (tec >= a[9] && tec <= a[10] - 1 && pars[2])
                if (col_par == 3)
                    show_notify(context, par[3], "Первый урок, заканчивается в " + a_t[10], mute);
                else
                    show_notify(context, par[3], "Первый урок, заканчивается в " + a_t[10] + " \nслед. пара " + par[4], mute);
            if (tec >= a[10] && tec <= a[11] - 1 && pars[2])
                if (col_par == 3)
                    show_notify(context, par[3], "Перемена, заканчивается в " + a_t[11], mute);
                else
                    show_notify(context, par[3], "Перемена, заканчивается в " + a_t[11] + " \nслед. пара " + par[4], mute);
            if (tec >= a[11] && tec <= a[12] - 1 && pars[2])
                if (col_par == 3)
                    show_notify(context, par[3], "Второй урок, заканчивается в " + a_t[12], mute);
                else
                    show_notify(context, par[3], "Второй урок, заканчивается в " + a_t[12] + " \nслед. пара " + par[4], mute);
        }
        if (col_par >= 4) {
            if (tec >= a[12] && tec <= a[13] - 1 && pars[3])
                show_notify(context, "Перемена, заканчивается в " + a_t[13], "след. пара " + par[4], mute);
            if (tec >= a[13] && tec <= a[14] - 1 && pars[3])
                if (col_par == 4)
                    show_notify(context, par[4], "Первый урок, заканчивается в " + a_t[14], mute);
                else
                    show_notify(context, par[4], "Первый урок, заканчивается в " + a_t[14] + " \nслед. пара " + par[5], mute);
            if (tec >= a[14] && tec <= a[15] - 1 && pars[3])
                if (col_par == 4)
                    show_notify(context, par[4], "Перемена, заканчивается в " + a_t[15], mute);
                else
                    show_notify(context, par[4], "Перемена, заканчивается в " + a_t[15] + " \nслед. пара " + par[5], mute);
            if (tec >= a[15] && tec <= a[16] - 1 && pars[3])
                if (col_par == 4)
                    show_notify(context, par[4], "Второй урок, заканчивается в " + a_t[16], mute);
                else
                    show_notify(context, par[4], "Второй урок, заканчивается в " + a_t[16] + " \nслед. пара " + par[5], mute);
        }
        if (col_par >= 5) {
            if (tec >= a[16] && tec <= a[17] - 1 && pars[4])
                show_notify(context, "Перемена, заканчивается в " + a_t[17], "след. пара " + par[5], mute);
            if (tec >= a[17] && tec <= a[18] - 1 && pars[4])
                show_notify(context, par[5], "Первый урок, заканчивается в " + a_t[18], mute);
            if (tec >= a[18] && tec <= a[19] - 1 && pars[4])
                show_notify(context, par[5], "Перемена, заканчивается в " + a_t[19], mute);
            if (tec >= a[19] && tec <= a[20] - 1 && pars[4])
                show_notify(context, par[5], "Второй урок, заканчивается в " + a_t[20], mute);
        }
    }

    public void am_create(Context context) {
        pref(context);
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(context, NotificationReceiver.class);
        pi = PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public void onReceive(Context context, Intent intent) {
        log("notification onReceive");
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
            wl.acquire();
        }
        set_alarm(context, 0);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            wl.release();
        }
    }

    public void set_alarm(Context context, int mute) {
        log("notification set alarm");
        sPref = context.getSharedPreferences("groups", MODE_PRIVATE);
        String group = sPref.getString("group", "");
        sPref = context.getSharedPreferences(group, MODE_PRIVATE);
        if (tec_date() == date(context, 1)) {
            par[1] = sPref.getString("p1", "") + " " + sPref.getString("p1_k", "");
            if (sPref.getString("p1", "") != "" && sPref.getString("p1_k", "") != "")
                pars[0] = true;
            par[2] = sPref.getString("p2", "") + " " + sPref.getString("p2_k", "");
            if (sPref.getString("p2", "") != "" && sPref.getString("p2_k", "") != "")
                pars[1] = true;
            par[3] = sPref.getString("p3", "") + " " + sPref.getString("p3_k", "");
            if (sPref.getString("p3", "") != "" && sPref.getString("p3_k", "") != "")
                pars[2] = true;
            par[4] = sPref.getString("p4", "") + " " + sPref.getString("p4_k", "");
            if (sPref.getString("p4", "") != "" && sPref.getString("p4_k", "") != "")
                pars[3] = true;
            par[5] = sPref.getString("p5", "") + " " + sPref.getString("p5_k", "");
            if (sPref.getString("p5", "") != "" && sPref.getString("p5_k", "") != "")
                pars[4] = true;
        }
        if (tec_date() == date(context, 2)) {
            par[1] = sPref.getString("p1_1", "") + " " + sPref.getString("p1_k_1", "");
            if (sPref.getString("p1_1", "") != "" && sPref.getString("p1_k_1", "") != "")
                pars[0] = true;
            par[2] = sPref.getString("p2_1", "") + " " + sPref.getString("p2_k_1", "");
            if (sPref.getString("p2_1", "") != "" && sPref.getString("p2_k_1", "") != "")
                pars[1] = true;
            par[3] = sPref.getString("p3_1", "") + " " + sPref.getString("p3_k_1", "");
            if (sPref.getString("p3_1", "") != "" && sPref.getString("p3_k_1", "") != "")
                pars[2] = true;
            par[4] = sPref.getString("p4_1", "") + " " + sPref.getString("p4_k_1", "");
            if (sPref.getString("p4_1", "") != "" && sPref.getString("p4_k_1", "") != "")
                pars[3] = true;
            par[5] = sPref.getString("p5_1", "") + " " + sPref.getString("p5_k_1", "");
            if (sPref.getString("p5_1", "") != "" && sPref.getString("p5_k_1", "") != "")
                pars[4] = true;
        }
        if (tec_date() == date(context, 3)) {
            par[1] = sPref.getString("p1_2", "") + " " + sPref.getString("p1_k_2", "");
            if (sPref.getString("p1_2", "") != "" && sPref.getString("p1_k_2", "") != "")
                pars[0] = true;
            par[2] = sPref.getString("p2_2", "") + " " + sPref.getString("p2_k_2", "");
            if (sPref.getString("p2_2", "") != "" && sPref.getString("p2_k_2", "") != "")
                pars[1] = true;
            par[3] = sPref.getString("p3_2", "") + " " + sPref.getString("p3_k_2", "");
            if (sPref.getString("p3_2", "") != "" && sPref.getString("p3_k_2", "") != "")
                pars[2] = true;
            par[4] = sPref.getString("p4_2", "") + " " + sPref.getString("p4_k_2", "");
            if (sPref.getString("p4_2", "") != "" && sPref.getString("p4_k_2", "") != "")
                pars[3] = true;
            par[5] = sPref.getString("p5_2", "") + " " + sPref.getString("p5_k_2", "");
            if (sPref.getString("p5_2", "") != "" && sPref.getString("p5_k_2", "") != "")
                pars[4] = true;
        }
        for (int ij = 0; ij <= 4; ij++) {
            if (pars[ij]) col_par = ij;
        }
        col_par++;
        if (ned == 6) {
            a = sb;
            a_t = sb_t;
        } else if (ned != 7) {
            a = dn;
            a_t = dn_t;
        }
        am_create(context);
        set_notify(context, mute);
        switch (ned) {
            case 6:
                if (tec >= 450 && tec < a[col_par * 4]) {
                    log("6 >450 <" + a[col_par * 4]);
                    p = 0;
                    i = 0;
                    while (tec >= a[i]) {
                        p = a[i + 1];
                        i++;
                    }
                    next = p - tec;
                } else {
                    if (tec < 450) {
                        log("6 <450 " + a[col_par * 4]);
                        next = 450 - tec;
                    }
                    if (tec >= a[col_par * 4]) {
                        log("6 >=" + a[col_par * 4]);
                        next = (1440 - tec) + 1890;
                    }
                }
                break;
            case 7:
                log("7 " + a[col_par * 4]);
                next = (1440 - tec) + 450;
                break;
            default:
                if (tec >= 450 && tec < a[col_par * 4]) {
                    log("d >=450 <" + a[col_par * 4]);
                    p = 0;
                    i = 0;
                    while (tec >= a[i]) {
                        p = a[i + 1];
                        i++;
                    }
                    next = p - tec;
                } else {
                    if (tec < 450) {
                        log("d <450 " + a[col_par * 4]);
                        next = 450 - tec;
                    }
                    if (tec >= a[col_par * 4]) {
                        log("d >=" + a[col_par * 4]);
                        next = (1440 - tec) + 450;
                    }
                }
                break;
        }
        log(col_par + " notification am " + (mill(next) - (sec * 1000)) + "  "
                + (((((next * 60) - sec) / 60) >= 60) ? ((next * 60) - sec) / 60 / 60 : 0) + ":"
                + ((((next * 60) - sec) / 60) - ((((next * 60) - sec) / 60 / 60) * 60)) + ":"
                + (sec == 0 ? sec : (60 - sec)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + mill(next) - ((sec - 1) * 1000), pi);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + mill(next) - ((sec - 1) * 1000), pi);
            } else {
                am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + mill(next) - ((sec - 1) * 1000), pi);
            }
        }
    }

    public void cancel_alarm(Context context) {
        log("notification cancel alarm");
        am_create(context);
        am.cancel(pi);
    }
}