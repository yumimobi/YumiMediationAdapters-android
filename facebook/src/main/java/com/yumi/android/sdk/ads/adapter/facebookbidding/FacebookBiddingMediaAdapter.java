package com.yumi.android.sdk.ads.adapter.facebookbidding;

import android.app.Activity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.BidderTokenProvider;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.S2SRewardedVideoAdListener;
import com.yumi.android.sdk.ads.adapter.facebook.FacebookAdErrorHolder;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

/**
 * Created by yfb on 2018/9/6.
 */

public class FacebookBiddingMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "FacebookBiddingMediaAdapter";
    private RewardedVideoAd rewardedVideoAd;
    private S2SRewardedVideoAdListener listener;


    protected FacebookBiddingMediaAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
    }

    @Override
    protected void onPrepareMedia() {
        try {
            ZplayDebug.i(TAG, "facebookbid media onPrepareMedia", onoff);
            if (rewardedVideoAd == null) {
                rewardedVideoAd = new RewardedVideoAd(getActivity(), getProvider().getKey1());
                rewardedVideoAd.setAdListener(listener);
                if (listener == null) {
                    createListener();
                }
//        rewardedVideoAd.setRewardData(new RewardData("YOUR_USER_ID", "YOUR_REWARD"));  //不知道到底有什么用，文档里没有说明
            }
            if (getProvider().getErrCode() != 200) {
                layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL, getProvider().getErrMessage());
                return;
            }
            rewardedVideoAd.loadAdFromBid(getProvider().getPayload(), false);
        } catch (Exception e) {
            ZplayDebug.e(TAG, "facebookbid media onPrepareMedia error", e, onoff);
        }
    }

    @Override
    protected void onShowMedia() {
        try {
            boolean isShow = rewardedVideoAd.show();
            ZplayDebug.i(TAG, "facebookbid media onShowMedia " + isShow, onoff);
        } catch (Exception e) {
            ZplayDebug.e(TAG, "facebookbid media onShowMedia error ", e, onoff);
        }
    }

    @Override
    protected boolean isMediaReady() {
        if (rewardedVideoAd != null) {
            if (rewardedVideoAd.isAdLoaded()) {
                ZplayDebug.i(TAG, "facebookbid media isMediaReady isAdLoaded true", onoff);
                return true;
            }
            ZplayDebug.i(TAG, "facebookbid media isMediaReady isAdLoaded false", onoff);
        }
        ZplayDebug.i(TAG, "facebookbid media isMediaReady false", onoff);
        return false;
    }


    @Override
    protected void init() {
        ZplayDebug.d(TAG, "facebookbid media init", onoff);
        createListener();
    }

    private void createListener() {
        listener = new S2SRewardedVideoAdListener() {
            @Override
            public void onRewardServerFailed() {
                ZplayDebug.i(TAG, "facebookbid media onRewardServerFailed", onoff);
            }

            @Override
            public void onRewardServerSuccess() {
                ZplayDebug.i(TAG, "facebookbid media onRewardServerSuccess", onoff);
            }

            @Override
            public void onRewardedVideoCompleted() {
                ZplayDebug.i(TAG, "facebookbid media onRewardedVideoCompleted", onoff);
                layerIncentived();
                layerMediaEnd();
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                ZplayDebug.i(TAG, "facebookbid media onLoggingImpression", onoff);
                layerExposure();
                layerMediaStart();
            }

            @Override
            public void onRewardedVideoClosed() {
                ZplayDebug.i(TAG, "facebookbid media onRewardedVideoClosed", onoff);
                layerClosed();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                ZplayDebug.i(TAG, "facebookbid media onError ErrorCode : " + adError.getErrorCode() + "  || ErrorMessage : " + adError.getErrorMessage(), onoff);
                layerPreparedFailed(FacebookAdErrorHolder.decodeError(adError), adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                ZplayDebug.i(TAG, "facebookbid media onAdLoaded PlacementId:" + ad.getPlacementId(), onoff);
                layerPrepared();
            }

            @Override
            public void onAdClicked(Ad ad) {
                ZplayDebug.i(TAG, "facebookbid media onAdClicked", onoff);
                layerClicked();
            }
        };
    }

    @Override
    protected void callOnActivityDestroy() {
        try {
            if (rewardedVideoAd != null) {
                rewardedVideoAd.destroy();
                rewardedVideoAd = null;
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "facebookbid media callOnActivityDestroy error ", e, onoff);
        }
    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }

    public String getBidderToken() {
        return BidderTokenProvider.getBidderToken(getContext());
    }

}
