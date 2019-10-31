package com.yumi.android.sdk.ads.adapter.gdtmob;

import android.app.Activity;
import android.os.SystemClock;

import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.comm.util.AdError;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.GdtUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.GdtUtil.recodeFiledToShowError;
import static com.yumi.android.sdk.ads.adapter.GdtUtil.sdkVersion;

public class GdtmobMediaAdapter extends YumiCustomerMediaAdapter {
    private static final String TAG = "GdtmobMediaAdapter";
    private RewardVideoADListener rewardVideoADListener;
    private RewardVideoAD rewardVideoAD;
    private boolean adLoaded = false;
    private boolean isRewarded = false;

    protected GdtmobMediaAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    protected void onPrepareMedia() {
        try {
            ZplayDebug.i(TAG, "load new media");
            if (rewardVideoAD == null) {
                if (rewardVideoADListener == null) {
                    createListener();
                }
                rewardVideoAD = new RewardVideoAD(getContext(), getProvider().getKey1(), getProvider().getKey2(), rewardVideoADListener);
            }
            adLoaded = false;
            rewardVideoAD.loadAD();
        } catch (Exception e) {
            ZplayDebug.e(TAG, "onPrepareMedia error", e);
        }
    }

    @Override
    protected void onShowMedia() {
        if (adLoaded) {
            if (!rewardVideoAD.hasShown()) {
                if (checkMaterialNotExpired()) {
                    rewardVideoAD.showAD();
                } else {
                    ZplayDebug.e(TAG, "onShowMedia error : MATERIAL ETIME ");
                    layerExposureFailed(recodeFiledToShowError());
                }
            } else {
                ZplayDebug.e(TAG, "onShowMedia error : hasShown" + rewardVideoAD.hasShown());
            }
        } else {
            ZplayDebug.e(TAG, "onShowMedia error : adloaded" + adLoaded);
        }
    }

    @Override
    protected boolean isMediaReady() {
        if (rewardVideoAD != null) {
            if (adLoaded && !rewardVideoAD.hasShown()) {
                ZplayDebug.i(TAG, "isMediaReady isAdLoaded true");
                if(checkMaterialNotExpired()){
                    return true;
                }else{
                    adLoaded = false;
                    layerExposureFailed(recodeFiledToShowError());
                }
            }
            ZplayDebug.i(TAG, "isMediaReady isAdLoaded false");
        }
        ZplayDebug.i(TAG, "isMediaReady false");
        return false;
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "init");
        createListener();
    }

    private void createListener() {
        rewardVideoADListener = new RewardVideoADListener() {
            @Override
            public void onADLoad() {
                ZplayDebug.i(TAG, "onADLoad");
                adLoaded = true;
                layerPrepared();
            }

            @Override
            public void onVideoCached() {
                ZplayDebug.i(TAG, "onVideoCached");
            }

            @Override
            public void onADShow() {
                ZplayDebug.i(TAG, "onADShow");
            }

            @Override
            public void onADExpose() {
                ZplayDebug.i(TAG, "onADExpose");
                isRewarded = false;
                layerExposure();
                layerStartPlaying();
            }

            @Override
            public void onReward() {
                ZplayDebug.i(TAG, "onReward");
                isRewarded = true;
                layerIncentived();
            }

            @Override
            public void onADClick() {
                ZplayDebug.i(TAG, "onADClick");
                layerClicked();
            }

            @Override
            public void onVideoComplete() {
                ZplayDebug.i(TAG, "onVideoComplete");
            }

            @Override
            public void onADClose() {
                ZplayDebug.i(TAG, "onADClose");
                layerClosed(isRewarded);
            }

            @Override
            public void onError(AdError adError) {
                if (adError == null) {
                    ZplayDebug.d(TAG, "onError adError = null");
                    layerPreparedFailed(recodeError(null));
                    return;
                }
                ZplayDebug.d(TAG, "onError ErrorCode:" + adError.getErrorCode() + " msg:" + adError.getErrorMsg());
                layerPreparedFailed(recodeError(adError));
            }
        };
    }

    private boolean checkMaterialNotExpired() {
        long delta = 1000;
        return SystemClock.elapsedRealtime() < (rewardVideoAD.getExpireTimestamp() - delta);
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
