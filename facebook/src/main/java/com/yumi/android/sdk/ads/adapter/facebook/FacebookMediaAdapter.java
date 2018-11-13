package com.yumi.android.sdk.ads.adapter.facebook;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.S2SRewardedVideoAdListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

/**
 * Created by hjl on 2017/12/4.
 */

public class FacebookMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "FacebookMediaAdapter";
    private RewardedVideoAd rewardedVideoAd;
    private S2SRewardedVideoAdListener listener;
    private static final int REQUEST_NEXT_MEDIA = 0x001;

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_NEXT_MEDIA:
                    if (rewardedVideoAd != null && listener != null) {
                        ZplayDebug.d(TAG, "Facebook media Video REQUEST_NEXT_MEDIA ", onoff);
                        layerNWRequestReport();
                        rewardedVideoAd.loadAd(false);
                    }
                    break;
                default:
                    break;
            }
        };
    };

    protected FacebookMediaAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
    }

    @Override
    protected void onPrepareMedia() {
        try {
            ZplayDebug.i(TAG, "facebook media onPrepareMedia", onoff);
            if (rewardedVideoAd == null) {
                rewardedVideoAd = new RewardedVideoAd(getActivity(), getProvider().getKey1());
                rewardedVideoAd.setAdListener(listener);
                if (listener == null) {
                    createListener();
                }
//        rewardedVideoAd.setRewardData(new RewardData("YOUR_USER_ID", "YOUR_REWARD"));  //不知道到底有什么用，文档里没有说明
            }
            rewardedVideoAd.loadAd(false);
        } catch (Exception e) {
            ZplayDebug.e(TAG, "facebook media onPrepareMedia error", e, onoff);
        }
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
                layerIncentived();
                layerMediaEnd();
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                ZplayDebug.i(TAG, "facebook media onLoggingImpression", onoff);
                layerExposure();
                layerMediaStart();
            }

            @Override
            public void onRewardedVideoClosed() {
                ZplayDebug.i(TAG, "facebook media onRewardedVideoClosed", onoff);
                layerClosed();
                requestAD(5);
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                ZplayDebug.i(TAG, "facebook media onError ErrorCode : " + adError.getErrorCode() + "  || ErrorMessage : " + adError.getErrorMessage(), onoff);
                layerPreparedFailed(FacebookAdErrorHolder.decodeError(adError));
                requestAD(getProvider().getNextRequestInterval());
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


    private void requestAD(int delaySecond) {
        try {
            if(!mHandler.hasMessages(REQUEST_NEXT_MEDIA)) {
                ZplayDebug.d(TAG, "facebook media Video requestAD delaySecond" + delaySecond, onoff);
                mHandler.sendEmptyMessageDelayed(REQUEST_NEXT_MEDIA, delaySecond * 1000);
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "facebook media requestAD error ", e, onoff);
        }
    }

    @Override
    protected void callOnActivityDestroy() {
        try {
            if (rewardedVideoAd != null) {
                rewardedVideoAd.destroy();
                rewardedVideoAd = null;
            }
            if (mHandler != null && mHandler.hasMessages(REQUEST_NEXT_MEDIA)) {
                mHandler.removeMessages(REQUEST_NEXT_MEDIA);
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
}
