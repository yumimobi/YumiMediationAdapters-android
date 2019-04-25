package com.yumi.android.sdk.ads.adapter.admob;

import android.app.Activity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.admob.AdMobUtil.recodeError;

/**
 * Created by Administrator on 2017/4/19.
 */

public class AdmobMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "AdmobMediaAdapter";
    private RewardedVideoAd mAd;
    private RewardedVideoAdListener mediaListener;
    private boolean isReady;
    private boolean isCompletePlaying = false;

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
        if (mAd != null && !mAd.isLoaded()) {
            ZplayDebug.d(TAG, "admob media PrepareMedia", onoff);
            isReady = false;
            mAd.loadAd(getProvider().getKey1(), new AdRequest.Builder().build());
        }
    }

    @Override
    protected void onShowMedia() {
        if(mAd!=null) {
            if (mAd.isLoaded()) {
                mAd.show();
            }
        }
    }

    @Override
    protected boolean isMediaReady() {
        try{
        if(mAd!=null) {
            if (mAd.isLoaded()) {
                ZplayDebug.d(TAG, "admob media isMediaReady true", onoff);
                return true;
            }
        }
        ZplayDebug.d(TAG, "admob media isMediaReady false", onoff);
        return false;
        }catch (Exception e){
            ZplayDebug.e(TAG, "admob media isMediaReady error : ", e, onoff);
            return isReady;
        }
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
                isReady = true;
                layerPrepared();
            }

            @Override
            public void onRewardedVideoAdOpened() {
                ZplayDebug.d(TAG, "admob media onRewardedVideoAdOpened  layerClicked", onoff);
                isCompletePlaying = false;
                isReady = false;
                layerExposure();
            }

            @Override
            public void onRewardedVideoStarted() {
                ZplayDebug.d(TAG, "admob media onRewardedVideoStarted", onoff);
                isReady = false;
                layerStartPlaying();
            }

            @Override
            public void onRewardedVideoAdClosed() {
                ZplayDebug.d(TAG, "admob media onRewardedVideoAdClosed", onoff);
                isReady = false;
                layerClosed(isCompletePlaying);
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                ZplayDebug.d(TAG, "admob media onRewarded", onoff);
                isReady = false;
                layerIncentived();
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                ZplayDebug.d(TAG, "admob media onRewardedVideoAdLeftApplication", onoff);
                layerClicked();
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int errorCode) {
                ZplayDebug.d(TAG, "admob media onRewardedVideoAdFailedToLoad errorCode:" + errorCode, onoff);
                isReady = false;
                layerPreparedFailed(recodeError(errorCode));
            }

            @Override
            public void onRewardedVideoCompleted() {
                ZplayDebug.d(TAG, "admob media onRewardedVideoCompleted", onoff);
                isCompletePlaying = true;
            }
        };
    }
}
