package com.kenny.raspisanie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class LoadReceiver extends BroadcastReceiver {

    NotificationReceiver nr;
    SharedPreferences sp;

    @Override
    public void onReceive(Context context, Intent intent) {
        nr = new NotificationReceiver();
        nr.log("notification onReceive autoload");
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        if (sp.getBoolean("notif_pref", false)) {
            nr.cancel_alarm(context);
            nr.set_alarm(context, 1);
        }
    }
}