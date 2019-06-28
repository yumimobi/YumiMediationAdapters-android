package com.yumi.android.sdk.ads.adapter.baidu;


import android.app.Activity;

import com.baidu.mobads.InterstitialAd;
import com.baidu.mobads.InterstitialAdListener;

public class BaiduExtra {

    private InterstitialAd instertitial;

    private BaiduExtra() {
    }

    static BaiduExtra getBaiduExtra() {
        return BaiduExtraHolder.INSTANCE;
    }

    public InterstitialAd getBaiduInterstitialAd(Activity activity, String key2, InterstitialAdListener instertitialListener) {

        if (instertitial == null) {
            instertitial = new InterstitialAd(activity, key2);
        }
        instertitial.setListener(instertitialListener);
        return instertitial;
    }

    private static class BaiduExtraHolder {

        private static final BaiduExtra INSTANCE = new BaiduExtra();

    }
}
