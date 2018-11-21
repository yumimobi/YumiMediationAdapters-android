package com.yumi.android.sdk.ads.adapter.baidu;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.baidu.mobads.interfaces.IXAdConstants4PDK;
import com.baidu.mobads.rewardvideo.RewardVideoAd;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

public class BaiduMediaAdapter extends YumiCustomerMediaAdapter {
    private static final String TAG = "BaiduMediaAdapter";
    private RewardVideoAd rewardVideoAd;
    private RewardVideoAd.RewardVideoAdListener rewardVideoAdListener;
    private boolean adLoaded = false;
    private static final int REQUEST_NEXT_MEDIA = 0x001;

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_NEXT_MEDIA:
                    if (rewardVideoAd != null && rewardVideoAdListener != null) {
                        ZplayDebug.d(TAG, "baidu media Video REQUEST_NEXT_MEDIA ", onoff);
                        layerNWRequestReport();
                        adLoaded = false;
                        rewardVideoAd.load();
                    }
                    break;
                default:
                    break;
            }
        }
        ;
    };

    protected BaiduMediaAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);

    }

    @Override
    protected void onPrepareMedia() {
        if (rewardVideoAd != null && rewardVideoAdListener != null) {
            adLoaded = false;
            rewardVideoAd.load();
        }
    }

    @Override
    protected void onShowMedia() {
        if (adLoaded && rewardVideoAd != null) {
            rewardVideoAd.show();
        }
    }

    @Override
    protected boolean isMediaReady() {
        return adLoaded;
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "baidu key1 : " + getProvider().getKey1(), onoff);
        ZplayDebug.i(TAG, "baidu key2 : " + getProvider().getKey2(), onoff);
        createrListener();
        rewardVideoAd = new RewardVideoAd(getActivity(), getProvider().getKey2(), rewardVideoAdListener);
    }

    private void createrListener() {
        rewardVideoAdListener = new RewardVideoAd.RewardVideoAdListener() {
            @Override
            public void onAdShow() {
                ZplayDebug.i(TAG, "baidu media onAdShow", onoff);
                layerExposure();
                layerMediaStart();
            }

            @Override
            public void onVideoDownloadSuccess() {
                ZplayDebug.i(TAG, "baidu media onVideoDownloadSuccess", onoff);
                adLoaded = true;
                layerPrepared();
            }

            @Override
            public void onVideoDownloadFailed() {
                ZplayDebug.i(TAG, "baidu media onRewardServerSuccess", onoff);
                layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
                requestAD(getProvider().getNextRequestInterval());
            }

            @Override
            public void onAdClick() {
                ZplayDebug.i(TAG, "baidu media onAdClick", onoff);
                layerClicked();
            }

            @Override
            public void onAdClose() {
                ZplayDebug.i(TAG, "baidu media onAdClose", onoff);
                layerMediaEnd();
                ZplayDebug.d(TAG, "baidu media get reward", onoff);
                layerIncentived();
                layerClosed();
                requestAD(3);
            }

            @Override
            public void onAdFailed(String s) {
                ZplayDebug.i(TAG, "baidu media onAdFailed:" + s, onoff);
                layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
                requestAD(getProvider().getNextRequestInterval());
            }
        };
    }

    private void requestAD(int delaySecond) {
        try {
            if (!mHandler.hasMessages(REQUEST_NEXT_MEDIA)) {
                ZplayDebug.d(TAG, "baidu media Video requestAD delaySecond" + delaySecond, onoff);
                mHandler.sendEmptyMessageDelayed(REQUEST_NEXT_MEDIA, delaySecond * 1000);
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "baidu media requestAD error ", e, onoff);
        }
    }

    @Override
    protected void callOnActivityDestroy() {
        try {
            if (mHandler != null && mHandler.hasMessages(REQUEST_NEXT_MEDIA)) {
                mHandler.removeMessages(REQUEST_NEXT_MEDIA);
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "baidu media callOnActivityDestroy error ", e, onoff);
        }
    }

    @Override
    public void onActivityPause() {
        if (rewardVideoAd != null) {
            rewardVideoAd.setActivityState(IXAdConstants4PDK.ActivityState.PAUSE);
        }
    }

    @Override
    public void onActivityResume() {
        if (rewardVideoAd != null) {
            rewardVideoAd.setActivityState(IXAdConstants4PDK.ActivityState.RESUME);
        }
    }
}
