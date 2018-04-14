package com.kenny.raspisanie;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RedActivity extends AppCompatActivity {

    EditText et1,et2,et3;
    SharedPreferences sPref;
    SharedPreferences.Editor ed;
    String group;
    Toast t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_red);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        et1 = (EditText)findViewById(R.id.editText1);
        et2 = (EditText)findViewById(R.id.editText2);
        et3 = (EditText)findViewById(R.id.editText3);
        sPref = getSharedPreferences("groups", MODE_PRIVATE);
        group = sPref.getString("group","");
        sPref = getSharedPreferences(group, MODE_PRIVATE);
        et1.setText(sPref.getString("date",""));
        et2.setText(sPref.getString("date_1",""));
        et3.setText(sPref.getString("date_2",""));
        t = Toast.makeText(getApplicationContext(),"Изменения сохранены", Toast.LENGTH_SHORT);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public void save(View w){
        sPref = getSharedPreferences("groups", MODE_PRIVATE);
        int col = Integer.parseInt(sPref.getString("col","0"));
        for (int i=0;i<=col;i++){
            sPref = getSharedPreferences("groups", MODE_PRIVATE);
            sPref = getSharedPreferences(sPref.getString(String.valueOf(i),""), MODE_PRIVATE);
            ed = sPref.edit();
            ed.putString("date",String.valueOf(et1.getText()));
            ed.putString("date_1",String.valueOf(et2.getText()));
            ed.putString("date_2",String.valueOf(et3.getText()));
            ed.apply();
        }
        t.show();
        finish();
    }
}