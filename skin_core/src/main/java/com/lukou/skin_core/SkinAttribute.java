package com.lukou.skin_core;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import com.lukou.skin_core.utils.SkinResources;

import java.util.ArrayList;
import java.util.List;

public class SkinAttribute {

    private static final List<String> mAttributes = new ArrayList<>();

    static {
        mAttributes.add("layout_width");
        mAttributes.add("layout_height");
        mAttributes.add("background");
        mAttributes.add("src");
        mAttributes.add("textColor");
        mAttributes.add("textSize");
        mAttributes.add("layout_marginLeft");
        mAttributes.add("layout_marginRight");
        mAttributes.add("layout_marginTop");
        mAttributes.add("layout_marginBottom");
        mAttributes.add("text");
    }

    //记录换肤需要操作的View与属性信息
    private List<SkinView> mSkinViews = new ArrayList<>();

    public void load(View view, AttributeSet attrs) {
        List<SkinPair> mSkinPars = new ArrayList<>();
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            //获得属性名  mAttributes
            String attributeName = attrs.getAttributeName(i);
            if (mAttributes.contains(attributeName)) {
                String attributeValue = attrs.getAttributeValue(i);
                int resId = 0;
                //目前只支持@形式的资源换肤
                if (attributeValue.startsWith("@")) {
                    // 正常以 @ 开头, 才可以换肤
                    resId = Integer.parseInt(attributeValue.substring(1));
                }
                if (resId > 0 && SkinResources.getInstance().needChanged(resId)) {
                    SkinPair skinPair = new SkinPair(attributeName, resId);
                    mSkinPars.add(skinPair);
                }
            }
        }

        // 打开的时候-换肤
        if (!mSkinPars.isEmpty()) {
            SkinView skinView = new SkinView(view, mSkinPars);
            skinView.applySkin();
            mSkinViews.add(skinView);
        }
    }

    void applySkin() {
        for (SkinView mSkinView : mSkinViews) {
            try {
                mSkinView.applySkin();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class SkinView {
        View view;
        List<SkinPair> skinPairs;

        SkinView(View view, List<SkinPair> skinPairs) {
            this.view = view;
            this.skinPairs = skinPairs;
        }

        void applySkin() {
            int width = 0;
            int height = 0;
            int marginTop = 0;
            int marginBottom = 0;
            int marginLeft = 0;
            int marginRight = 0;
            for (SkinPair skinPair : skinPairs) {
                switch (skinPair.attributeName) {
                    case "layout_width":
                        width = SkinResources.getInstance().getDimens(skinPair.resId);
                        break;
                    case "layout_height":
                        height = SkinResources.getInstance().getDimens(skinPair.resId);
                        break;
                    case "layout_marginLeft":
                        marginLeft = SkinResources.getInstance().getDimens(skinPair.resId);
                        break;
                    case "layout_marginRight":
                        marginRight = SkinResources.getInstance().getDimens(skinPair.resId);
                        break;
                    case "layout_marginTop":
                        marginTop = SkinResources.getInstance().getDimens(skinPair.resId);
                        break;
                    case "layout_marginBottom":
                        marginBottom = SkinResources.getInstance().getDimens(skinPair.resId);
                        break;
                    case "background":
                        Object background = SkinResources.getInstance().getColorOrDrawable(skinPair
                                .resId);
                        if (background instanceof Integer) {
                            view.setBackgroundColor((int) background);
                        } else {
                            ViewCompat.setBackground(view, (Drawable) background);
                        }
                        break;
                    case "src":
                        Object src = SkinResources.getInstance().getColorOrDrawable(skinPair
                                .resId);
                        if (src instanceof Integer) {
                            ((ImageView) view).setImageDrawable(new ColorDrawable((Integer)
                                    src));
                        } else {
                            ((ImageView) view).setImageDrawable((Drawable) src);
                        }
                        break;
                    case "textColor":
                        ((TextView) view).setTextColor(SkinResources.getInstance().getColorStateList
                                (skinPair.resId));
                        break;
                    case "textSize":
                        ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                SkinResources.getInstance().getDimens(skinPair.resId));
                        break;
                    case "text":
                        ((TextView) view).setText(SkinResources.getInstance().getString(skinPair.resId));
                        break;
                    default:
                        break;
                }
            }
            if (width > 0 || height > 0) {
                final int finalWidth = width;
                final int finalHeight = height;
                view.post(() -> {
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    if (finalWidth > 0) {
                        layoutParams.width = finalWidth;
                    }
                    if (finalHeight > 0) {
                        layoutParams.height = finalHeight;
                    }
                    view.setLayoutParams(layoutParams);
                });
            }
            if (marginTop > 0 ||  marginBottom > 0 || marginLeft > 0 || marginRight > 0) {
                int finalMarginLeft = marginLeft;
                int finalMarginTop = marginTop;
                int finalMarginRight = marginRight;
                int finalMarginBottom = marginBottom;
                view.post(() -> {
                    setMargins(view, finalMarginLeft, finalMarginTop, finalMarginRight, finalMarginBottom);
                });
            }
        }

        void setMargins(View v, int l, int t, int r, int b) {
            ViewGroup.MarginLayoutParams p;
            if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            } else {
                p = new ViewGroup.MarginLayoutParams(v.getLayoutParams());
            }
            p.setMargins(l > 0 ? l : p.leftMargin, t > 0 ? t : p.topMargin, r > 0 ? r : p.rightMargin, b > 0 ? b : p.bottomMargin);
            v.setLayoutParams(p);
        }
    }

    static class SkinPair {
        String attributeName;
        int resId;

        SkinPair(String attributeName, int resId) {
            this.attributeName = attributeName;
            this.resId = resId;
        }
    }
}
