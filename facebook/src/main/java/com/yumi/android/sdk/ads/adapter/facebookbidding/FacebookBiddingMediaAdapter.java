package com.yumi.android.sdk.ads.adapter.facebookbidding;

import android.app.Activity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.BidderTokenProvider;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.S2SRewardedVideoAdListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil.initSDK;
import static com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil.sdkVersion;

/**
 * Created by yfb on 2018/9/6.
 */

public class FacebookBiddingMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "FacebookBiddingMediaAdapter";
    private RewardedVideoAd rewardedVideoAd;
    private S2SRewardedVideoAdListener listener;
    private boolean isRewarded = false;


    protected FacebookBiddingMediaAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
    }

    @Override
    protected void onPrepareMedia() {
        try {
            ZplayDebug.i(TAG, "load new media");
            if (rewardedVideoAd == null) {
                rewardedVideoAd = new RewardedVideoAd(getActivity(), getProvider().getKey1());
                rewardedVideoAd.setAdListener(listener);
                if (listener == null) {
                    createListener();
                }
//        rewardedVideoAd.setRewardData(new RewardData("YOUR_USER_ID", "YOUR_REWARD"));  //不知道到底有什么用，文档里没有说明
            }
            if (getProvider().getErrCode() != 200) {
                layerPreparedFailed(recodeError(null, getProvider().getErrMessage()), getProvider().getErrMessage());
                return;
            }
            rewardedVideoAd.loadAdFromBid(getProvider().getPayload(), false);
        } catch (Exception e) {
            ZplayDebug.e(TAG, "onPrepareMedia error", e);
        }
    }

    @Override
    protected void onShowMedia() {
        try {
            boolean isShow = rewardedVideoAd.show();
            ZplayDebug.i(TAG, "onShowMedia " + isShow);
        } catch (Exception e) {
            ZplayDebug.e(TAG, "onShowMedia error ", e);
        }
    }

    @Override
    protected boolean isMediaReady() {
        if (rewardedVideoAd != null) {
            if (rewardedVideoAd.isAdLoaded()) {
                ZplayDebug.i(TAG, "isMediaReady isAdLoaded true");
                return true;
            }
            ZplayDebug.i(TAG, "isMediaReady isAdLoaded false");
        }
        ZplayDebug.i(TAG, "isMediaReady false");
        return false;
    }


    @Override
    protected void init() {
        ZplayDebug.d(TAG, "init payload：" + getProvider().getPayload());
        initSDK(getContext());
        createListener();
    }

    private void createListener() {
        listener = new S2SRewardedVideoAdListener() {
            @Override
            public void onRewardServerFailed() {
                ZplayDebug.i(TAG, "onRewardServerFailed");
            }

            @Override
            public void onRewardServerSuccess() {
                ZplayDebug.i(TAG, "onRewardServerSuccess");
            }

            @Override
            public void onRewardedVideoCompleted() {
                ZplayDebug.i(TAG, "onRewardedVideoCompleted");
                isRewarded = true;
                layerIncentived();
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                ZplayDebug.i(TAG, "onLoggingImpression");
                isRewarded = false;
                layerExposure();
                layerStartPlaying();
            }

            @Override
            public void onRewardedVideoClosed() {
                ZplayDebug.i(TAG, "onRewardedVideoClosed");
                layerClosed(isRewarded);
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                ZplayDebug.i(TAG, "onError ErrorCode : " + adError.getErrorCode() + "  || ErrorMessage : " + adError.getErrorMessage());
                layerPreparedFailed(recodeError(adError), adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                ZplayDebug.i(TAG, "onAdLoaded PlacementId:" + ad.getPlacementId());
                layerPrepared();
            }

            @Override
            public void onAdClicked(Ad ad) {
                ZplayDebug.i(TAG, "onAdClicked");
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
            ZplayDebug.e(TAG, "callOnActivityDestroy error ", e);
        }
    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }

    public String getBidderToken() {
        return BidderTokenProvider.getBidderToken(getActivity());
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
