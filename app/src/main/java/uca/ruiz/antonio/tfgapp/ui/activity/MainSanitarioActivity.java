package uca.ruiz.antonio.tfgapp.ui.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import uca.ruiz.antonio.tfgapp.R;
import uca.ruiz.antonio.tfgapp.utils.Pref;

public class MainSanitarioActivity extends AppCompatActivity  {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);
        tv.setText("Hola sanitario");


    }





}