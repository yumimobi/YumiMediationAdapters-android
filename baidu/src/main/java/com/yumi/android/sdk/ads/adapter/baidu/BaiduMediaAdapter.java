package com.yumi.android.sdk.ads.adapter.baidu;

import android.app.Activity;

import com.baidu.mobad.video.XAdManager;
import com.baidu.mobads.rewardvideo.RewardVideoAd;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.baidu.BaiduUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.baidu.BaiduUtil.sdkVersion;

public class BaiduMediaAdapter extends YumiCustomerMediaAdapter {
    private static final String TAG = "BaiduMediaAdapter";
    private static boolean isReady = false;
    private RewardVideoAd rewardVideoAd;
    private RewardVideoAd.RewardVideoAdListener rewardVideoAdListener;
    private boolean isRewarded = false;


    protected BaiduMediaAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);

    }


    @Override
    protected void onPrepareMedia() {
        ZplayDebug.d(TAG, "load new media");
        if (rewardVideoAd != null && rewardVideoAdListener != null) {
            isReady = false;
            rewardVideoAd.load();
        }
    }

    @Override
    protected void onShowMedia() {
        if (rewardVideoAd != null && isReady) {
            rewardVideoAd.show();
        }
    }

    @Override
    protected boolean isMediaReady() {
        if (rewardVideoAd != null && isReady) {
            return true;
        }
        return false;
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "init key1 : " + getProvider().getKey1() +" ,key2 : " + getProvider().getKey2());
        isReady = false;
        createrListener();
        XAdManager.getInstance(getActivity()).setAppSid(getProvider().getKey1());
        rewardVideoAd = new RewardVideoAd(getActivity(), getProvider().getKey2(), rewardVideoAdListener);
    }

    private void createrListener() {
        rewardVideoAdListener = new RewardVideoAd.RewardVideoAdListener() {
            @Override
            public void onAdShow() {
                ZplayDebug.i(TAG, "onAdShow");
                isRewarded = false;
                layerExposure();
                layerStartPlaying();
            }

            @Override
            public void onVideoDownloadSuccess() {
                ZplayDebug.i(TAG, "onVideoDownloadSuccess");
                layerPrepared();
                isReady = true;
            }

            @Override
            public void onVideoDownloadFailed() {
                ZplayDebug.i(TAG, "onVideoDownloadFailed");
                layerPreparedFailed(recodeError(null));
            }

            @Override
            public void playCompletion() {
                ZplayDebug.d(TAG, "playCompletion");
                isRewarded = true;
                layerIncentived();
                isReady = false;

            }

            @Override
            public void onAdClick() {
                ZplayDebug.i(TAG, "onAdClick");
                layerClicked();
                isReady = false;
            }

            @Override
            public void onAdClose(float v) {
                ZplayDebug.i(TAG, "onAdClose");
                layerClosed(isRewarded);
                isReady = false;
            }

            @Override
            public void onAdFailed(String s) {
                ZplayDebug.i(TAG, "onAdFailed:" + s);
                layerPreparedFailed(recodeError(s));
                isReady = false;
            }
        };

    }

    @Override
    public void onActivityPause() {
        if (rewardVideoAd != null) {
            rewardVideoAd.pause();
        }
    }

    @Override
    public void onActivityResume() {
        if (rewardVideoAd != null) {
            rewardVideoAd.resume();
        }
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
