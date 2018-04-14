package com.kenny.raspisanie;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class PrefActivity extends PreferenceActivity {

    private AppCompatDelegate mDelegate;
    CheckBoxPreference ch2, ch3;
    NotificationReceiver nr;
    Preference pr, pr1, pr2, pr3;
    SharedPreferences sPref;
    SwitchPreference sp;
    SharedPreferences.Editor ed;
    RingtonePreference rp;
    Toast toast;
    long col,time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
        col = 0;
        nr = new NotificationReceiver();
        time = System.currentTimeMillis();
        mDelegate.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sp = (SwitchPreference) findPreference("notif_pref");
        ch2 = (CheckBoxPreference) findPreference("small_pref");
        ch3 = (CheckBoxPreference) findPreference("vibrate_pref");
        pr = findPreference("reset_group");
        rp = (RingtonePreference) findPreference("sound_pref_custom");
        pr1 = findPreference("send_message");
        pr2 = findPreference("traffic");
        pr3 = findPreference("about");
        String ringtonePath = rp.getSharedPreferences().getString("sound_pref_custom", "content://settings/system/notification_sound");
        Ringtone ringtone = RingtoneManager.getRingtone(
                PrefActivity.this, Uri.parse((String) ringtonePath));
        rp.setSummary(ringtone.getTitle(PrefActivity.this));
        rp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Ringtone ringtone = RingtoneManager.getRingtone(
                        PrefActivity.this, Uri.parse((String) newValue));
                sPref = rp.getSharedPreferences();
                ed = sPref.edit();
                ed.putString("sound_pref_custom", String.valueOf(newValue));
                ed.apply();
                rp.setSummary(ringtone.getTitle(PrefActivity.this));
                return false;
            }
        });
        sp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                if (sp.isChecked()) {
                    nr.cancel_alarm(getApplicationContext());
                    nr.set_alarm(getApplicationContext(), 1);
                } else {
                    nr.cancel_alarm(getApplicationContext());
                    nr.hide_notify(getApplicationContext());
                }
                return false;
            }
        });
        ch2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                nr.cancel_alarm(getApplicationContext());
                nr.set_alarm(getApplicationContext(), 1);
                return false;
            }
        });
        pr.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                sPref = getSharedPreferences("groups", MODE_PRIVATE);
                ed = sPref.edit();
                ed.putString("group", "");
                ed.putString("group_num", "");
                ed.apply();
                Intent intent = new Intent(PrefActivity.this, FirstActivity.class);
                startActivity(intent);
                finish();
                return false;
            }
        });
        pr1.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(PrefActivity.this, SendActivity.class);
                startActivity(intent);
                return false;
            }
        });
        pr2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                sPref = getSharedPreferences("traffic", MODE_PRIVATE);
                int t = sPref.getInt("col", 0);
                String res = t + "б";
                if (t >= 1024)
                    res = String.valueOf(t / 1024) + "кб";
                if (t >= 1048576)
                    res = String.valueOf(t / 1024 / 1024) + "мб ";
                toast = Toast.makeText(getApplicationContext(), "Загружено: " + res, Toast.LENGTH_SHORT);
                toast.show();
                col++;
                if ((System.currentTimeMillis() - time) <= 2000) {
                    if (col == 3) {
                        Intent intent = new Intent(PrefActivity.this, RedActivity.class);
                        startActivity(intent);
                    }
                } else {
                    col = 0;
                    time = System.currentTimeMillis();
                }
                return false;
            }
        });
        pr3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Dialog dialog = new Dialog(PrefActivity.this);
                dialog.setTitle("О программе");
                dialog.setContentView(R.layout.about_layout);
                dialog.show();
                return false;
            }
        });
    }

    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}