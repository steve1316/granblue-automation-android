package com.steve1316.granblue_automation_android;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.zoontek.rnbootsplash.RNBootSplash;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends ReactActivity {
    public static final String loggerTag = "[GAA]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the app updater to check for the latest update from GitHub.
        new AppUpdater(this)
                .setUpdateFrom(UpdateFrom.XML)
                .setUpdateXML("https://raw.githubusercontent.com/steve1316/granblue-automation-android/main/app/update.xml")
                .start();

        // Load OpenCV native library. This will throw a "E/OpenCV/StaticHelper: OpenCV error: Cannot load info library for OpenCV". It is safe to
        // ignore this error. OpenCV functionality is not impacted by this error.
        OpenCVLoader.initDebug();
    }

    /**
     * Returns the name of the main component registered from JavaScript. This is used to schedule
     * rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "Granblue Automation Android";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected ReactActivityDelegate createReactActivityDelegate() {
        return new ReactActivityDelegate(this, getMainComponentName()) {

            @Override
            protected void loadApp(String appKey) {
                RNBootSplash.init(MainActivity.this); // <- initialize the splash screen
                super.loadApp(appKey);
            }
        };
    }
}
