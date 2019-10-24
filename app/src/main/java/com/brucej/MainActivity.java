package com.brucej;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * 水滴布局(从左到右排布子View,尺寸不足自动换行)
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
