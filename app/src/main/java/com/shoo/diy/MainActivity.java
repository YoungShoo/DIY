package com.shoo.diy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shoo.volley.RequestQueue;
import com.shoo.volley.Volley;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TVolley.test(this);
    }
}
