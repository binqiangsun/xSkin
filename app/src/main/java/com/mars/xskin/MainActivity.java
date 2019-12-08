package com.mars.xskin;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.lukou.skin_core.SkinManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final View homeTab = findViewById(R.id.tab_home_lay);
        final View loadSkinBtn = findViewById(R.id.load_skin_btn);
        final View cleanSkinBtn = findViewById(R.id.clean_skin_btn);
        homeTab.setOnClickListener(view -> homeTab.setSelected(!homeTab.isSelected()));
        loadSkinBtn.setOnClickListener(view -> addSkinPath());
        cleanSkinBtn.setOnClickListener(view -> cleanSkinPath());
    }

    private void addSkinPath() {
        File skinFile = loadSkinFile(this);
        SkinManager.getInstance().loadSkin(skinFile.getPath());
    }

    private void cleanSkinPath() {
        SkinManager.getInstance().loadSkin("");
    }

    private File loadSkinFile(Context context) {
        final String defaultSkinFileName = "skin_default.apk";
        File skinRootDir = new File(context.getFilesDir(), "skin");
        if (!skinRootDir.exists()) {
            skinRootDir.mkdirs();
        }
        File defaultSkin = new File(skinRootDir, defaultSkinFileName);
        try {
            InputStream is = context.getAssets().open("skin_christmas.apk");
            FileOutputStream fos = new FileOutputStream(defaultSkin);
            byte[] buffer = new byte[1024 * 40];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultSkin;
    }
}
