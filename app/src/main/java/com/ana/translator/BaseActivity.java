package com.ana.translator;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class BaseActivity extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context mContex;

    @Override
    public void onCreate() {
        super.onCreate();
        mContex=this;
    }

    public static Context getmContex()
    {
        return mContex;
    }
}
