package com.lukou.skin_core.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

public class SkinResources {

    private static SkinResources instance;

    private Resources mSkinResources;
    private String mSkinPkgName;
    private boolean isDefaultSkin = true;

    private Resources mAppResources;
    private String mSkinPrefix;

    private SkinResources(Context context, String skinPrefix) {
        mAppResources = context.getResources();
        mSkinPrefix = skinPrefix;
    }

    public static void init(Context context, String skinPrefix) {
        if (instance == null) {
            synchronized (SkinResources.class) {
                if (instance == null) {
                    instance = new SkinResources(context, skinPrefix);
                }
            }
        }
    }

    public static SkinResources getInstance() {
        return instance;
    }

    public void reset() {
        mSkinResources = null;
        mSkinPkgName = "";
        isDefaultSkin = true;
    }

    public void applySkin(Resources resources, String pkgName) {
        mSkinResources = resources;
        mSkinPkgName = pkgName;
        //是否使用默认皮肤
        isDefaultSkin = TextUtils.isEmpty(pkgName) || resources == null;
    }


    private int getIdentifier(int resId) {
        if (isDefaultSkin) {
            return resId;
        }
        //通过id获取当前的名称, 找到皮肤包里对应的id
        String resName = mAppResources.getResourceEntryName(resId);
        String resType = mAppResources.getResourceTypeName(resId);
        return mSkinResources.getIdentifier(resName, resType, mSkinPkgName);
    }

    public int getColor(int resId) {
        if (isDefaultSkin) {
            return mAppResources.getColor(resId);
        }
        int skinId = getIdentifier(resId);
        if (skinId == 0) {
            return mAppResources.getColor(resId);
        }
        return mSkinResources.getColor(skinId);
    }

    public ColorStateList getColorStateList(int resId) {
        if (isDefaultSkin) {
            return mAppResources.getColorStateList(resId);
        }
        int skinId = getIdentifier(resId);
        if (skinId == 0) {
            return mAppResources.getColorStateList(resId);
        }
        return mSkinResources.getColorStateList(skinId);
    }

    public Drawable getDrawable(int resId) {
        if (isDefaultSkin) {
            return mAppResources.getDrawable(resId);
        }
        int skinId = getIdentifier(resId);
        if (skinId == 0) {
            return mAppResources.getDrawable(resId);
        }
        return mSkinResources.getDrawable(skinId);
    }


    /**
     * 可能是Color 也可能是drawable
     *
     * @return
     */
    public Object getColorOrDrawable(int resId) {
        String resourceTypeName = mAppResources.getResourceTypeName(resId);

        if (resourceTypeName.equals("color")) {
            return getColor(resId);
        } else {
            // drawable
            return getDrawable(resId);
        }
    }

    public String getString(int resId) {
        try {
            if (isDefaultSkin) {
                return mAppResources.getString(resId);
            }
            int skinId = getIdentifier(resId);
            if (skinId == 0) {
                return mAppResources.getString(resId);
            }
            return mSkinResources.getString(skinId);
        } catch (Resources.NotFoundException e) {

        }
        return null;
    }

    public int getDimens(int resId) {
        try {
            if (isDefaultSkin) {
                return mAppResources.getDimensionPixelSize(resId);
            }
            int skinId = getIdentifier(resId);
            if (skinId == 0) {
                return mAppResources.getDimensionPixelSize(resId);
            }
            return mSkinResources.getDimensionPixelSize(skinId);
        } catch (Resources.NotFoundException e) {
            Log.e("skin_resources", e.getMessage());
        }
        return 0;
    }

    /**
     * 是否需要换肤的属性
     * @param resId
     * @return
     */
    public boolean needChanged(int resId) {
        String resName = mAppResources.getResourceEntryName(resId);
        return resName.startsWith(mSkinPrefix);
    }
}
