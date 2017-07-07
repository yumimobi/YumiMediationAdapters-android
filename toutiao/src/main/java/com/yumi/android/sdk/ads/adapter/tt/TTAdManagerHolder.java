package com.yumi.android.sdk.ads.adapter.tt;

import android.content.Context;

import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdManagerFactory;

/**
 * 可以用一个单例来保存TTAdManager实例
 */
public  class TTAdManagerHolder {

    private static boolean sInit;

    public static TTAdManager getInstance(Context context,String AppId) {
        TTAdManager ttAdManager = TTAdManagerFactory.getInstance(context);
        if (!sInit) {
            synchronized (TTAdManagerHolder.class) {
                if (!sInit) {
                    doInit(ttAdManager,AppId);
                    sInit = true;
                }
            }
        }
        return ttAdManager;
    }

    private static void doInit(TTAdManager ttAdManager, String AppId) {
        ttAdManager.setAppId(AppId).setName("yumi").setTitleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK);
    }
}
