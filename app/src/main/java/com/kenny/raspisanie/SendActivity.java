package com.kenny.raspisanie;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import org.jsoup.Jsoup;
import java.io.IOException;

public class SendActivity extends AppCompatActivity {

    TextView tv1,tv2;
    CheckBox cb;
    Task1 t;
    NotificationReceiver nr;
    Toast toast,toast1,toast2;
    static String text = "",name = "anon",model = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        toast = Toast.makeText(getApplicationContext(),
                "Благодарим за ваш отзыв", Toast.LENGTH_SHORT);
        toast1 = Toast.makeText(getApplicationContext(),
                "Отсутствует подключение к интернету", Toast.LENGTH_SHORT);
        toast2 = Toast.makeText(getApplicationContext(),
                "Не все поля заполнены", Toast.LENGTH_SHORT);
        nr = new NotificationReceiver();
        nr.log("send start");
        tv1 = (TextView)findViewById(R.id.error_text);
        tv2 = (TextView)findViewById(R.id.error_name);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_send);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cb = (CheckBox)findViewById(R.id.checkBox);
    }

    public void onclick_send(View v) {
        if (cb.isChecked())
            model = Build.MODEL;
        text = String.valueOf(tv1.getText());
        name = String.valueOf(tv2.getText());
        text = text.replaceAll(" ","_");
        name = name.replaceAll(" ","_");
        model = model.replaceAll(" ","_");
        if (text.isEmpty()){
               toast2.show();
        }else{
            t = new Task1();
            t.execute();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    boolean error = false;
    class Task1 extends AsyncTask<Void, Void, Void> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... params) {
            try {
                nr.log("http://bgaek.by/rasp/bagrep.php?text="+text+"&name="+name+"&model="+model);
                Jsoup.connect("http://bgaek.by/rasp/bagrep.php?text="+text+"&name="+name+"&model="+model).get();
            } catch (IOException e) {
                nr.log("send error");
                error = true;
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (error){
                toast1.show();
            }else {
                nr.log("send ok");
                toast.show();
                finish();
            }
        }
    }
}