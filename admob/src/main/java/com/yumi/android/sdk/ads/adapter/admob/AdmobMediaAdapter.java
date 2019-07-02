package com.yumi.android.sdk.ads.adapter.admob;

import android.app.Activity;

import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.admob.AdMobUtil.getAdRequest;
import static com.yumi.android.sdk.ads.adapter.admob.AdMobUtil.recodeError;

/**
 * Created by Administrator on 2017/4/19.
 */

public class AdmobMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "AdmobMediaAdapter";
    private RewardedAd mAd;
    private boolean isReady;
    private boolean isRewarded = false;

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
        ZplayDebug.d(TAG, "admob media PrepareMedia", onoff);
        isReady = false;
        mAd = new RewardedAd(getContext(), getProvider().getKey1());
        mAd.loadAd(getAdRequest(getContext()), new RewardedAdLoadCallback() {
            public void onRewardedAdLoaded() {
                ZplayDebug.d(TAG, "admob media onRewardedAdLoaded", onoff);
                isReady = true;
                layerPrepared();
            }

            public void onRewardedAdFailedToLoad(int var1) {
                ZplayDebug.d(TAG, "admob media onRewardedAdFailedToLoad errorCode:" + var1, onoff);
                isReady = false;
                layerPreparedFailed(recodeError(var1));
            }
        });
    }

    @Override
    protected void onShowMedia() {
        if (mAd != null) {
            if (mAd.isLoaded()) {
                mAd.show(getActivity(), new RewardedAdCallback() {
                    public void onRewardedAdOpened() {
                        ZplayDebug.d(TAG, "admob media onRewardedAdOpened", onoff);
                        isRewarded = false;
                        isReady = false;
                        layerExposure();
                        layerStartPlaying();
                    }

                    public void onRewardedAdClosed() {
                        ZplayDebug.d(TAG, "admob media onRewardedAdClosed", onoff);
                        isReady = false;
                        layerClosed(isRewarded);
                    }

                    public void onUserEarnedReward(RewardItem reward) {
                        ZplayDebug.d(TAG, "admob media onUserEarnedReward", onoff);
                        isReady = false;
                        isRewarded = true;
                        layerIncentived();
                    }

                    public void onRewardedAdFailedToShow(int errorCode) {
                        layerExposureFailed(recodeError(errorCode));
                    }
                });
            }
        }
    }

    @Override
    protected boolean isMediaReady() {
        try {
            if (mAd != null) {
                if (mAd.isLoaded()) {
                    ZplayDebug.d(TAG, "admob media isMediaReady true", onoff);
                    return true;
                }
            }
            ZplayDebug.d(TAG, "admob media isMediaReady false", onoff);
            return false;
        } catch (Exception e) {
            ZplayDebug.e(TAG, "admob media isMediaReady error : ", e, onoff);
            return isReady;
        }
    }

    @Override
    protected void init() {
    }

    @Override
    protected void callOnActivityDestroy() {
    }
}
