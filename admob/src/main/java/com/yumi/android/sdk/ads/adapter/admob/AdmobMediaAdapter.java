package com.yumi.android.sdk.ads.adapter.admob;

import android.app.Activity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

/**
 * Created by Administrator on 2017/4/19.
 */

public class AdmobMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "AdmobMediaAdapter";
    private RewardedVideoAd mAd;
    private RewardedVideoAdListener mediaListener;
    private boolean isPreparedFailed=false;

    //王雪提供
//    private static final String AD_UNIT_ID = "ca-app-pub-1755510051935997/3006338664";
//    private static final String APP_ID = "ca-app-pub-1755510051935997~6407649864";
    //admob提供
//    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/3377882518";
//    private static final String APP_ID = "ca-app-pub-3940256099942544~4992070916";

    protected AdmobMediaAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }

    @Override
    protected void onPrepareMedia() {
        loadRewardedVideoAd();
    }

    @Override
    protected void onShowMedia() {
        if (mAd.isLoaded()) {
            mAd.show();
        }
    }

    @Override
    protected boolean isMediaReady() {
        if (mAd.isLoaded()) {
           return true;
        }
        if(isPreparedFailed) {
            loadRewardedVideoAd();
        }
        return false;
    }

    @Override
    protected void init() {
        createMediaListener();
        mAd = MobileAds.getRewardedVideoAdInstance(getActivity());
        mAd.setRewardedVideoAdListener(mediaListener);
    }

    @Override
    protected void callOnActivityDestroy() {

    }

    private void createMediaListener() {
        mediaListener = new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                ZplayDebug.d(TAG, "admob media onRewardedVideoAdLoaded", onoff);
                layerPrepared();
            }

            @Override
            public void onRewardedVideoAdOpened() {
                ZplayDebug.d(TAG, "admob media onRewardedVideoAdOpened  layerClicked" , onoff);
                layerClicked();
            }

            @Override
            public void onRewardedVideoStarted() {
                ZplayDebug.d(TAG, "admob media onRewardedVideoStarted", onoff);
                layerMediaStart();
            }

            @Override
            public void onRewardedVideoAdClosed() {
                ZplayDebug.d(TAG, "admob media onRewardedVideoAdClosed", onoff);
                layerMediaEnd();
                layerClosed();
                loadRewardedVideoAd();
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                ZplayDebug.d(TAG, "admob media onRewarded", onoff);
                layerIncentived();
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                ZplayDebug.d(TAG, "admob media onRewardedVideoAdLeftApplication", onoff);
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int errorCode) {
                ZplayDebug.d(TAG, "admob media onRewardedVideoAdFailedToLoad errorCode:" + errorCode, onoff);
                if ( AdRequest.ERROR_CODE_NO_FILL == errorCode) {
                    layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
                }else {
                    layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
                }
                isPreparedFailed=true;
            }
        };
    }
    private void loadRewardedVideoAd() {
        ZplayDebug.d(TAG, "admob media loadRewardedVideoAd", onoff);
        if (mAd != null) {
            if (!mAd.isLoaded()) {
                ZplayDebug.d(TAG, "admob media loadRewardedVideoAd loadAd", onoff);
                isPreparedFailed=false;
                mAd.loadAd(getProvider().getKey1(), new AdRequest.Builder().build());
            }
        }
    }
}
