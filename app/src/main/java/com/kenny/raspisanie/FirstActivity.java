    package com.kenny.raspisanie;

    import android.app.ProgressDialog;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.net.Uri;
    import android.os.AsyncTask;
    import android.preference.PreferenceManager;
    import android.provider.Settings;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.ListView;
    import android.widget.Toast;
    import org.jsoup.Jsoup;
    import org.jsoup.nodes.Document;
    import org.jsoup.nodes.Element;
    import org.jsoup.select.Elements;
    import java.io.IOException;
    import java.util.ArrayList;

    public class FirstActivity extends AppCompatActivity {

        ArrayList<String> arr = new ArrayList<>();
        ListView listview;
        ArrayAdapter<String> adapter;
        ProgressDialog pd;
        SharedPreferences sPref;
        SharedPreferences.Editor ed;
        NotificationReceiver nr;
        Toast toast;
        String gr;
        int i = 0;

        public void progress(String title) {
            pd = new ProgressDialog(this);
            pd.setTitle(title);
            pd.setMessage("Пожалуйста подождите...");
            pd.setCancelable(false);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setIndeterminate(true);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_first);
            nr = new NotificationReceiver();
            nr.log("first onCreate");
            toast = Toast.makeText(getApplicationContext(), "Отсутствует подключение к интернету", Toast.LENGTH_SHORT);
            Uri defaultstr = Uri.parse(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("sound_pref_custom", Settings.System.DEFAULT_RINGTONE_URI.toString()));
            if (defaultstr.equals(android.provider.Settings.System.DEFAULT_RINGTONE_URI)) {
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("sound_pref_custom",
                        android.provider.Settings.System.DEFAULT_RINGTONE_URI.toString()).apply();
            }
            sPref = getSharedPreferences("groups", MODE_PRIVATE);
            ed = sPref.edit();
            listview = (ListView) findViewById(R.id.listView);
            gr = sPref.getString("group", "");
            if (!gr.isEmpty()) {
                nr.log("first skip");
                sPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                switch (sPref.getString("otdel", "0")) {
                    case "0":
                        startService(new Intent(getApplicationContext(), Parse1Service.class));
                        startService(new Intent(getApplicationContext(), Parse2Service.class));
                        break;
                    case "1":
                        startService(new Intent(getApplicationContext(), Parse1Service.class));
                        break;
                    case "2":
                        startService(new Intent(getApplicationContext(), Parse2Service.class));
                        break;
                }
                sPref = getSharedPreferences("groups", MODE_PRIVATE);
                ed = sPref.edit();
                Intent in = new Intent(getApplicationContext(), StartActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
            } else {
                adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1, arr);
                listview.setAdapter(adapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                            long id) {
                        nr.log("first group select " + arr.get(position));
                        ed.putString("group", arr.get(position));
                        ed.putString("group_num", String.valueOf(position));
                        ed.apply();
                        for (int ii = 0; ii < i; ii++) {
                            ed.putString(String.valueOf(ii), arr.get(ii));
                        }
                        ed.putString("col", String.valueOf(i - 1));
                        ed.apply();
                        sPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        switch (sPref.getString("otdel", "0")) {
                            case "0":
                                startService(new Intent(getApplicationContext(), Parse1Service.class));
                                startService(new Intent(getApplicationContext(), Parse2Service.class));
                                break;
                            case "1":
                                startService(new Intent(getApplicationContext(), Parse1Service.class));
                                break;
                            case "2":
                                startService(new Intent(getApplicationContext(), Parse2Service.class));
                                break;
                        }
                        Intent in = new Intent(getApplicationContext(), StartActivity.class);
                        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(in);
                    }
                });
                progress("Загрузка списка групп");
                pd.show();
                nr.log("first parse group");
                MyTask mt = new MyTask();
                mt.execute();
            }
        }

        class MyTask extends AsyncTask<Void, Void, Void> {
            Elements text1;
            String text;

            @Override
            protected Void doInBackground(Void... params) {
                Document doc = null;
                i = 0;
                sPref = getSharedPreferences("groups", MODE_PRIVATE);
                int coll = Integer.parseInt(sPref.getString("col", "0"));
                try {
                    doc = Jsoup.connect("http://bgaek.by/rasp/group.html").get();
                    nr.traffic(getApplicationContext(), String.valueOf(doc).length());
                } catch (IOException e) {
                    for (int ii = 0; ii <= coll; ii++) {
                        arr.add(sPref.getString(String.valueOf(ii), ""));
                        i++;
                    }
                    if (i == 1) {
                        toast.show();
                        finish();
                    }
                    nr.log("first error parse");
                }
                if (doc != null) {
                    text = doc.select("div").html();
                    text1 = Jsoup.parse(text).select("p");
                    for (Element temp : text1) {
                        arr.add(temp.text());
                        i++;
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                nr.log("first parse ok");
                adapter.notifyDataSetChanged();
                pd.hide();
            }
        }

    }