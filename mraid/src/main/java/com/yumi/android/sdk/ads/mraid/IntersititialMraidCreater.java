package com.yumi.android.sdk.ads.mraid;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yumi.android.sdk.ads.self.ads.i.IntersititialAD;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by mzk10 on 2017/11/1.
 */

public class IntersititialMraidCreater {

    private static final String TAG = "IntersititialMraidCreater";
    private static final boolean onoff = true;
    private final static String[] supportedNativeFeatures = {
            //MRAIDNativeFeature.CALENDAR,
            //MRAIDNativeFeature.INLINE_VIDEO,
            "sms",//MRAIDNativeFeature.SMS,
            //MRAIDNativeFeature.STORE_PICTURE,
            "tel"//MRAIDNativeFeature.TEL,
    };

    private Activity activity;
    private IntersititialAD intersititialAD;
    private String adm;
    private FrameLayout intersititialDialogHouse;

    private Object obj_MRAIDView;

    public IntersititialMraidCreater(Activity activity, IntersititialAD intersititialAD, String adm, FrameLayout intersititialDialogHouse) {
        this.activity = activity;
        this.intersititialAD = intersititialAD;
        this.adm = adm;
        this.intersititialDialogHouse = intersititialDialogHouse;
    }

    public boolean createIntersititialMraid() {
        try {
            String classname_MyIntersititialMraidiListener = "com.yumi.android.sdk.ads.mraid.MyIntersititialMraidiListener";
            Class<?> class_MyIntersititialMraidiListener = Class.forName(classname_MyIntersititialMraidiListener);
            Constructor<?> constructor_MyIntersititialMraidiListener = class_MyIntersititialMraidiListener.getConstructor(IntersititialAD.class);
            Object obj_MyIntersititialMraidiListener = constructor_MyIntersititialMraidiListener.newInstance(intersititialAD);

            String classname_MyMRAIDNativeFeatureListener = "com.yumi.android.sdk.ads.mraid.MyMRAIDNativeFeatureListener";
            Class<?> class_MyMRAIDNativeFeatureListener = Class.forName(classname_MyMRAIDNativeFeatureListener);
            Constructor<?> constructor_MyMRAIDNativeFeatureListener = class_MyMRAIDNativeFeatureListener.getConstructor(Activity.class);
            Object obj_MyMRAIDNativeFeatureListener = constructor_MyMRAIDNativeFeatureListener.newInstance(activity);

            String classname_MRAIDView = "com.yumi.android.sdk.ads.mraid.MRAIDView";
            Class<?> class_MRAIDView = Class.forName(classname_MRAIDView);
            Constructor<?> constructor_MRAIDView = class_MRAIDView.getConstructor(
                    Context.class,
                    String.class,
                    String.class,
                    String[].class,
                    Class.forName("com.yumi.android.sdk.ads.mraid.MRAIDViewListener"),
                    Class.forName("com.yumi.android.sdk.ads.mraid.MRAIDNativeFeatureListener")
                    );

            obj_MRAIDView = constructor_MRAIDView.newInstance(
                    activity,
                    null,
                    adm,
                    supportedNativeFeatures,
                    obj_MyIntersititialMraidiListener,
                    obj_MyMRAIDNativeFeatureListener
            );

            FrameLayout.LayoutParams web_param = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            intersititialDialogHouse.addView(((ViewGroup)obj_MRAIDView), web_param);
            return true;
        } catch (Exception e) {
            ZplayDebug.e_s(TAG, e.getMessage(), e, onoff);
            return false;
        }
    }

    public void destory() {
        if (obj_MRAIDView!=null)
        {
            try {
                String classname_MRAIDView = "com.yumi.android.sdk.ads.mraid.MRAIDView";
                Class<?> class_MRAIDView = Class.forName(classname_MRAIDView);
                Method method_Destroy = class_MRAIDView.getMethod("destroy");
                method_Destroy.invoke(obj_MRAIDView);
            } catch (Exception e) {
                ZplayDebug.e_s(TAG, e.getMessage(), e, onoff);
            }
        }
    }

}