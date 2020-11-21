package com.example.anatranslator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * Institution: Buckinghamshire New University
 * Academic Year: 2020/2021
 * Module: Advanced Mobile Systems
 * Author: Ana Lucia Petinga Zorro
 *
 * The MainActivity class specifies the features the user will be able to see once the
 * AnaTranslator is launched.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container,TranslateFragment.newInstance())
                    .commitNow();
        }
    }
}