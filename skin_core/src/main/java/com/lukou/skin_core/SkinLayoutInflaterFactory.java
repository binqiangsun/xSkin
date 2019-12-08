package com.lukou.skin_core;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class SkinLayoutInflaterFactory implements LayoutInflater.Factory2, Observer {
    private static final String[] mClassPrefixList = {
            "android.widget.",
            "android.view.",
            "android.webkit."
    };
    //记录对应View的构造函数
    private static final Map<String, Constructor<? extends View>> mConstructorMap
            = new HashMap<>();
    private static final Class<?>[] mConstructorSignature = new Class[]{
            Context.class, AttributeSet.class};
    static final Class<?>[] sCreateViewSignature = new Class[]{View.class, String.class, Context.class, AttributeSet.class};

    // 当选择新皮肤后需要替换View与之对应的属性
    // 页面属性管理器
    private SkinAttribute skinAttribute;

    private Activity mActivity;
    private Method sCreateViewMethod;


    public SkinLayoutInflaterFactory(Activity activity) {
        this.mActivity = activity;
        skinAttribute = new SkinAttribute();
    }


    /**
     * 创建对应布局并返回
     *
     * @param parent  当前TAG 父布局
     * @param name    在布局中的TAG 如:TextView, android.support.v7.widget.Toolbar
     * @param context 上下文
     * @param attrs   对应布局TAG中的属性 如: android:text android:src
     * @return View    null则由系统创建
     */
    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view = createViewFromAppCompat(parent, name, context, attrs);
        if (null == view) {
            view = createViewFromTag(name, context, attrs);
        }
        if (null == view) {
            view = createView(name, context, attrs);
        }
        if (null != view) {
            //加载属性
            skinAttribute.load(view, attrs);
        }
        return view;
    }

    /**
     * 如果是AppCompatActivity, 则用AppCompatDelegate去创建View
     * @param parent
     * @param name
     * @param context
     * @param attrs
     * @return
     */
    private View createViewFromAppCompat(View parent, String name, Context context, AttributeSet attrs) {
        if (!(mActivity instanceof AppCompatActivity)) {
            return null;
        }
        AppCompatDelegate delegate = ((AppCompatActivity) mActivity).getDelegate();
        View view = null;
        try {
            //public View createView
            // (View parent, final String name, @NonNull Context context, @NonNull AttributeSet attrs)
            if (sCreateViewMethod == null) {
                sCreateViewMethod = delegate.getClass().getMethod("createView", sCreateViewSignature);
            }
            Object object = sCreateViewMethod.invoke(delegate, parent, name, context, attrs);
            view = (View) object;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return view;
    }

    private View createViewFromTag(String name, Context context, AttributeSet
            attrs) {
        //如果 . 则不是SDK中的view 可能是自定义view包括support库中的View
        if (-1 != name.indexOf('.')) {
            return null;
        }
        for (String s : mClassPrefixList) {
            View view = createView(s +
                    name, context, attrs);
            if (view != null) {
                return view;
            }
        }
        return null;
    }

    private View createView(String name, Context context, AttributeSet
            attrs) {
        Constructor<? extends View> constructor = findConstructor(context, name);
        try {
            return constructor.newInstance(context, attrs);
        } catch (Exception e) {
        }
        return null;
    }

    private Constructor<? extends View> findConstructor(Context context, String name) {
        Constructor<? extends View> constructor = mConstructorMap.get(name);
        if (null == constructor) {
            try {
                Class<? extends View> clazz = context.getClassLoader().loadClass
                        (name).asSubclass(View.class);
                constructor = clazz.getConstructor(mConstructorSignature);
                mConstructorMap.put(name, constructor);
            } catch (Exception e) {
            }
        }
        return constructor;
    }

    @Override
    public void update(Observable o, Object arg) {
        skinAttribute.applySkin();
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }
}
