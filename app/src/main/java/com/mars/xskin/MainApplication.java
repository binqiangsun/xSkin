package com.mars.xskin;

import android.app.Application;

import com.lukou.skin_core.SkinManager;

import java.util.ArrayList;
import java.util.List;

public class MainApplication extends Application {

    private static List<String> skinActivityNames = new ArrayList<>();
    static {
        skinActivityNames.add("MainActivity");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager.init(this, skinActivityNames, "skin_");
    }

}
