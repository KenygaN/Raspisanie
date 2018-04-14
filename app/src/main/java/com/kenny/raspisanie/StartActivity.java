package com.kenny.raspisanie;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    static SharedPreferences sp;
    PendingIntent pi;
    SharedPreferences sPref;
    Context context;
    static String[][][] rasp = new String[5][30][20];
    NotificationReceiver nr;
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    BroadcastReceiver br;
    boolean enabled = true;
    int def = 0;
    static int col_par = 0;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                startActivity(new Intent(getApplicationContext(), PrefActivity.class));
                break;
            case R.id.reload:
                startService(new Intent(getApplicationContext(), Parse1Service.class));
                startService(new Intent(getApplicationContext(), Parse2Service.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onRefresh() {}

    public void onStop() {
        super.onStop();
        nr.log("start stop");
        enabled = false;
    }

    public void onResume() {
        super.onResume();
        nr.log("start resume");
        enabled = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        nr = new NotificationReceiver();
        sPref = getSharedPreferences("groups", MODE_PRIVATE);
        def = Integer.parseInt(sPref.getString("group_num", "0"));
        String col = sPref.getString("col", "");
        col_par = Integer.parseInt(col);
        for (int i = 0; i <= Integer.valueOf(col); i++) {
            sPref = getSharedPreferences("groups", MODE_PRIVATE);
            String group = sPref.getString(String.valueOf(i), "");
            sPref = getSharedPreferences(group, MODE_PRIVATE);
            rasp[0][i][0] = group;
            rasp[0][i][1] = sPref.getString("date", "");
            rasp[0][i][2] = sPref.getString("p1", "");
            rasp[0][i][3] = sPref.getString("p2", "");
            rasp[0][i][4] = sPref.getString("p3", "");
            rasp[0][i][5] = sPref.getString("p4", "");
            rasp[0][i][6] = sPref.getString("p5", "");
            rasp[0][i][7] = sPref.getString("p1_k", "");
            rasp[0][i][8] = sPref.getString("p2_k", "");
            rasp[0][i][9] = sPref.getString("p3_k", "");
            rasp[0][i][10] = sPref.getString("p4_k", "");
            rasp[0][i][11] = sPref.getString("p5_k", "");
            rasp[1][i][0] = group;
            rasp[1][i][1] = sPref.getString("date_1", "");
            rasp[1][i][2] = sPref.getString("p1_1", "");
            rasp[1][i][3] = sPref.getString("p2_1", "");
            rasp[1][i][4] = sPref.getString("p3_1", "");
            rasp[1][i][5] = sPref.getString("p4_1", "");
            rasp[1][i][6] = sPref.getString("p5_1", "");
            rasp[1][i][7] = sPref.getString("p1_k_1", "");
            rasp[1][i][8] = sPref.getString("p2_k_1", "");
            rasp[1][i][9] = sPref.getString("p3_k_1", "");
            rasp[1][i][10] = sPref.getString("p4_k_1", "");
            rasp[1][i][11] = sPref.getString("p5_k_1", "");
            rasp[2][i][0] = group;
            rasp[2][i][1] = sPref.getString("date_2", "");
            rasp[2][i][2] = sPref.getString("p1_2", "");
            rasp[2][i][3] = sPref.getString("p2_2", "");
            rasp[2][i][4] = sPref.getString("p3_2", "");
            rasp[2][i][5] = sPref.getString("p4_2", "");
            rasp[2][i][6] = sPref.getString("p5_2", "");
            rasp[2][i][7] = sPref.getString("p1_k_2", "");
            rasp[2][i][8] = sPref.getString("p2_k_2", "");
            rasp[2][i][9] = sPref.getString("p3_k_2", "");
            rasp[2][i][10] = sPref.getString("p4_k_2", "");
            rasp[2][i][11] = sPref.getString("p5_k_2", "");
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_start);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(def);
        context = this.getApplicationContext();
        nr.log("start onCreate");
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        if (sp.getBoolean("notif_pref", false)) {
            nr.cancel_alarm(context);
            nr.hide_notify(context);
            nr.set_alarm(context, 1);
        }
        br = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String text = intent.getStringExtra("text");

                switch (text) {
                    case "bio":
                        Toast t = Toast.makeText(getApplicationContext(), "Новое расписание бухгалтерского отделения загружено", Toast.LENGTH_SHORT);
                        t.show();
                        reload();
                        break;
                    case "bis":
                        Toast t2 = Toast.makeText(getApplicationContext(), "Новое расписание бухгалтерского отделения отсутствует на сайте", Toast.LENGTH_SHORT);
                        t2.show();
                        break;
                    case "sio":
                        Toast t3 = Toast.makeText(getApplicationContext(), "Новое расписание строительного отделения загружено", Toast.LENGTH_SHORT);
                        t3.show();
                        reload();
                        break;
                    case "sis":
                        Toast t4 = Toast.makeText(getApplicationContext(), "Новое расписание строительного отделения отсутствует на сайте", Toast.LENGTH_SHORT);
                        t4.show();
                        break;
                    case "n":
                        Toast t5 = Toast.makeText(getApplicationContext(), "Отсутсвует подключение к интернету\nзагрузка с кэша", Toast.LENGTH_SHORT);
                        t5.show();
                        break;
                }
            }
        };
        IntentFilter intFilt = new IntentFilter("com.kenny.raspisanie");
        registerReceiver(br, intFilt);
    }

    public void reload() {
        if (enabled) {
            Intent intent = getIntent();
            overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0, 0);
            intent.putExtra("load", "false");
            startActivity(intent);
        }
    }

    public static class PlaceholderFragment extends Fragment {

        TextView tvdate, tv1, tv2, tv3, tv4, tv5, tv11, tv21, tv31, tv41, tv51, tvdate_1, tv1_1, tv2_1, tv3_1, tv4_1, tv5_1, tv11_1, tv21_1, tv31_1, tv41_1, tv51_1, tvdate_2, tv1_2, tv2_2, tv3_2, tv4_2, tv5_2, tv11_2, tv21_2, tv31_2, tv41_2, tv51_2;
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_start, container, false);
            tvdate = (TextView) rootView.findViewById(R.id.textView);
            tv1 = (TextView) rootView.findViewById(R.id.textView1);
            tv2 = (TextView) rootView.findViewById(R.id.textView2);
            tv3 = (TextView) rootView.findViewById(R.id.textView3);
            tv4 = (TextView) rootView.findViewById(R.id.textView4);
            tv5 = (TextView) rootView.findViewById(R.id.textView5);
            tv11 = (TextView) rootView.findViewById(R.id.textView11);
            tv21 = (TextView) rootView.findViewById(R.id.textView21);
            tv31 = (TextView) rootView.findViewById(R.id.textView31);
            tv41 = (TextView) rootView.findViewById(R.id.textView41);
            tv51 = (TextView) rootView.findViewById(R.id.textView51);
            tvdate_1 = (TextView) rootView.findViewById(R.id.ptextView);
            tv1_1 = (TextView) rootView.findViewById(R.id.ptextView1);
            tv2_1 = (TextView) rootView.findViewById(R.id.ptextView2);
            tv3_1 = (TextView) rootView.findViewById(R.id.ptextView3);
            tv4_1 = (TextView) rootView.findViewById(R.id.ptextView4);
            tv5_1 = (TextView) rootView.findViewById(R.id.ptextView5);
            tv11_1 = (TextView) rootView.findViewById(R.id.ptextView11);
            tv21_1 = (TextView) rootView.findViewById(R.id.ptextView21);
            tv31_1 = (TextView) rootView.findViewById(R.id.ptextView31);
            tv41_1 = (TextView) rootView.findViewById(R.id.ptextView41);
            tv51_1 = (TextView) rootView.findViewById(R.id.ptextView51);
            tvdate_2 = (TextView) rootView.findViewById(R.id.pptextView);
            tv1_2 = (TextView) rootView.findViewById(R.id.pptextView1);
            tv2_2 = (TextView) rootView.findViewById(R.id.pptextView2);
            tv3_2 = (TextView) rootView.findViewById(R.id.pptextView3);
            tv4_2 = (TextView) rootView.findViewById(R.id.pptextView4);
            tv5_2 = (TextView) rootView.findViewById(R.id.pptextView5);
            tv11_2 = (TextView) rootView.findViewById(R.id.pptextView11);
            tv21_2 = (TextView) rootView.findViewById(R.id.pptextView21);
            tv31_2 = (TextView) rootView.findViewById(R.id.pptextView31);
            tv41_2 = (TextView) rootView.findViewById(R.id.pptextView41);
            tv51_2 = (TextView) rootView.findViewById(R.id.pptextView51);
            text(getArguments().getInt(ARG_SECTION_NUMBER));
            return rootView;
        }

        public void text(int a) {
            tvdate.setText(rasp[0][a][1]);
            tv1.setText(rasp[0][a][2]);
            tv2.setText(rasp[0][a][3]);
            tv3.setText(rasp[0][a][4]);
            tv4.setText(rasp[0][a][5]);
            tv5.setText(rasp[0][a][6]);
            tv11.setText(rasp[0][a][7]);
            tv21.setText(rasp[0][a][8]);
            tv31.setText(rasp[0][a][9]);
            tv41.setText(rasp[0][a][10]);
            tv51.setText(rasp[0][a][11]);
            tvdate_1.setText(rasp[1][a][1]);
            tv1_1.setText(rasp[1][a][2]);
            tv2_1.setText(rasp[1][a][3]);
            tv3_1.setText(rasp[1][a][4]);
            tv4_1.setText(rasp[1][a][5]);
            tv5_1.setText(rasp[1][a][6]);
            tv11_1.setText(rasp[1][a][7]);
            tv21_1.setText(rasp[1][a][8]);
            tv31_1.setText(rasp[1][a][9]);
            tv41_1.setText(rasp[1][a][10]);
            tv51_1.setText(rasp[1][a][11]);
            tvdate_2.setText(rasp[2][a][1]);
            tv1_2.setText(rasp[2][a][2]);
            tv2_2.setText(rasp[2][a][3]);
            tv3_2.setText(rasp[2][a][4]);
            tv4_2.setText(rasp[2][a][5]);
            tv5_2.setText(rasp[2][a][6]);
            tv11_2.setText(rasp[2][a][7]);
            tv21_2.setText(rasp[2][a][8]);
            tv31_2.setText(rasp[2][a][9]);
            tv41_2.setText(rasp[2][a][10]);
            tv51_2.setText(rasp[2][a][11]);
        }
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getItemPosition(final Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return col_par + 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return rasp[0][position][0];
        }
    }
}