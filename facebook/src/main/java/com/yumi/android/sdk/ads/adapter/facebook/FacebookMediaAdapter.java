package com.yumi.android.sdk.ads.adapter.facebook;

import android.app.Activity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.S2SRewardedVideoAdListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil.initSDK;
import static com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil.sdkVersion;

/**
 * Created by hjl on 2017/12/4.
 */

public class FacebookMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "FacebookMediaAdapter";
    private RewardedVideoAd rewardedVideoAd;
    private S2SRewardedVideoAdListener listener;
    private boolean isRewarded = false;

    protected FacebookMediaAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
    }

    @Override
    protected void onPrepareMedia() {
        try {
            ZplayDebug.i(TAG, "facebook media onPrepareMedia", onoff);
            if (!AudienceNetworkAds.isInitialized(getContext())) {
                initSDK(getContext(), new AudienceNetworkAds.InitListener() {
                    @Override
                    public void onInitialized(AudienceNetworkAds.InitResult initResult) {
                        ZplayDebug.i(TAG, "facebook media init isSuccess = " + initResult.isSuccess(), onoff);
                        if (initResult.isSuccess()) {
                            loadAd();
                        }else{
                            layerPreparedFailed(recodeError(AdError.INTERNAL_ERROR,"facebook init errorMsg: " + initResult.getMessage()));
                        }
                    }
                });
                return;
            }

            loadAd();
        } catch (Exception e) {
            ZplayDebug.e(TAG, "facebook media onPrepareMedia error", e, onoff);
        }
    }

    private void loadAd() {
        if (rewardedVideoAd == null) {
            rewardedVideoAd = new RewardedVideoAd(getActivity(), getProvider().getKey1());
            rewardedVideoAd.setAdListener(listener);
            if (listener == null) {
                createListener();
            }
//        rewardedVideoAd.setRewardData(new RewardData("YOUR_USER_ID", "YOUR_REWARD"));  //不知道到底有什么用，文档里没有说明
        }
        rewardedVideoAd.loadAd(false);
    }

    @Override
    protected void onShowMedia() {
        try {
            boolean isShow = rewardedVideoAd.show();
            ZplayDebug.i(TAG, "facebook media onShowMedia " + isShow, onoff);
        } catch (Exception e) {
            ZplayDebug.e(TAG, "facebook media onShowMedia error ", e, onoff);
        }
    }

    @Override
    protected boolean isMediaReady() {
        if (rewardedVideoAd != null) {
            if (rewardedVideoAd.isAdLoaded()) {
                ZplayDebug.i(TAG, "facebook media isMediaReady isAdLoaded true", onoff);
                return true;
            }
            ZplayDebug.i(TAG, "facebook media isMediaReady isAdLoaded false", onoff);
        }
        ZplayDebug.i(TAG, "facebook media isMediaReady false", onoff);
        return false;
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "facebook media init", onoff);
        createListener();
    }

    private void createListener() {
        listener = new S2SRewardedVideoAdListener() {
            @Override
            public void onRewardServerFailed() {
                ZplayDebug.i(TAG, "facebook media onRewardServerFailed", onoff);
            }

            @Override
            public void onRewardServerSuccess() {
                ZplayDebug.i(TAG, "facebook media onRewardServerSuccess", onoff);
            }

            @Override
            public void onRewardedVideoCompleted() {
                ZplayDebug.i(TAG, "facebook media onRewardedVideoCompleted", onoff);
                isRewarded = true;
                layerIncentived();
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                ZplayDebug.i(TAG, "facebook media onLoggingImpression", onoff);
                isRewarded = false;
                layerExposure();
                layerStartPlaying();
            }

            @Override
            public void onRewardedVideoClosed() {
                ZplayDebug.i(TAG, "facebook media onRewardedVideoClosed", onoff);
                layerClosed(isRewarded);
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                ZplayDebug.i(TAG, "facebook media onError ErrorCode : " + adError.getErrorCode() + "  || ErrorMessage : " + adError.getErrorMessage(), onoff);
                layerPreparedFailed(FacebookUtil.recodeError(adError));
            }

            @Override
            public void onAdLoaded(Ad ad) {
                ZplayDebug.i(TAG, "facebook media onAdLoaded PlacementId:" + ad.getPlacementId(), onoff);
                layerPrepared();
            }

            @Override
            public void onAdClicked(Ad ad) {
                ZplayDebug.i(TAG, "facebook media onAdClicked", onoff);
                layerClicked();
            }
        };
    }


    @Override
    protected void onDestroy() {
        try {
            if (rewardedVideoAd != null) {
                rewardedVideoAd.destroy();
                rewardedVideoAd = null;
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "facebook media callOnActivityDestroy error ", e, onoff);
        }
    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
