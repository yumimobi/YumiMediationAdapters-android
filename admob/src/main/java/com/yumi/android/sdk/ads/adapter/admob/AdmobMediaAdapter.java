package com.yumi.android.sdk.ads.adapter.admob;

import android.app.Activity;

import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.admob.AdMobUtil.getAdRequest;
import static com.yumi.android.sdk.ads.adapter.admob.AdMobUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.admob.AdMobUtil.sdkVersion;

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
        ZplayDebug.d(TAG, "load new media");
        isReady = false;
        mAd = new RewardedAd(getContext(), getProvider().getKey1());
        mAd.loadAd(getAdRequest(getContext()), new RewardedAdLoadCallback() {
            public void onRewardedAdLoaded() {
                ZplayDebug.d(TAG, "onRewardedAdLoaded");
                isReady = true;
                layerPrepared();
            }

            public void onRewardedAdFailedToLoad(int errorCode) {
                ZplayDebug.d(TAG, "onRewardedAdFailedToLoad errorCode: " + errorCode);
                isReady = false;
                layerPreparedFailed(recodeError(errorCode));
            }
        });
    }

    @Override
    protected void onShowMedia() {
        if (mAd != null) {
            if (isMediaReady()) {
                mAd.show(getActivity(), new RewardedAdCallback() {
                    public void onRewardedAdOpened() {
                        ZplayDebug.d(TAG, "onRewardedAdOpened");
                        isRewarded = false;
                        isReady = false;
                        layerExposure();
                        layerStartPlaying();
                    }

                    public void onRewardedAdClosed() {
                        ZplayDebug.d(TAG, "onRewardedAdClosed");
                        isReady = false;
                        layerClosed(isRewarded);
                    }

                    public void onUserEarnedReward(RewardItem reward) {
                        ZplayDebug.d(TAG, "onUserEarnedReward");
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
                    ZplayDebug.d(TAG, "isMediaReady true");
                    return true;
                }
            }
            ZplayDebug.d(TAG, "isMediaReady false");
            return false;
        } catch (Exception e) {
            ZplayDebug.e(TAG, "isMediaReady error : ", e);
            return isReady;
        }
    }

    @Override
    protected void init() {
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
