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

public class GdtmobMediaAdapter extends YumiCustomerMediaAdapter {
    private static final String TAG = "GdtmobMediaAdapter";
    private RewardVideoADListener rewardVideoADListener;
    private RewardVideoAD rewardVideoAD;
    private boolean adLoaded = false;

    protected GdtmobMediaAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    protected void onPrepareMedia() {
        try {
            ZplayDebug.i(TAG, "gdt media onPrepareMedia", onoff);
            if (rewardVideoAD == null) {
                if (rewardVideoADListener == null) {
                    createListener();
                }
                rewardVideoAD = new RewardVideoAD(getContext(), getProvider().getKey1(), getProvider().getKey2(), rewardVideoADListener);
            }
            adLoaded = false;
            rewardVideoAD.loadAD();
        } catch (Exception e) {
            ZplayDebug.e(TAG, "gdt media onPrepareMedia error", e, onoff);
        }
    }

    @Override
    protected void onShowMedia() {
        if (adLoaded) {
            if (!rewardVideoAD.hasShown()) {
                long delta = 1000;
                if (SystemClock.elapsedRealtime() < (rewardVideoAD.getExpireTimestamp() - delta)) {
                    rewardVideoAD.showAD();
                } else {
                    ZplayDebug.e(TAG, "gdt media onShowMedia error : MATERIAL ETIME ", onoff);
                }
            } else {
                ZplayDebug.e(TAG, "gdt media onShowMedia error : hasShown" + rewardVideoAD.hasShown(), onoff);
            }
        } else {
            ZplayDebug.e(TAG, "gdt media onShowMedia error : adloaded" + adLoaded, onoff);
        }
    }

    @Override
    protected boolean isMediaReady() {
        if (rewardVideoAD != null) {
            if (adLoaded && !rewardVideoAD.hasShown()) {
                ZplayDebug.i(TAG, "gdt media isMediaReady isAdLoaded true", onoff);
                return true;
            }
            ZplayDebug.i(TAG, "gdt media isMediaReady isAdLoaded false", onoff);
        }
        ZplayDebug.i(TAG, "gdt media isMediaReady false", onoff);
        return false;
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "gdt media init", onoff);
        createListener();
    }

    private void createListener() {
        rewardVideoADListener = new RewardVideoADListener() {
            @Override
            public void onADLoad() {
                ZplayDebug.i(TAG, "gdt media onADLoad", onoff);
                adLoaded = true;
                layerPrepared();
            }

            @Override
            public void onVideoCached() {
                ZplayDebug.i(TAG, "gdt media onVideoCached", onoff);
            }

            @Override
            public void onADShow() {
                ZplayDebug.i(TAG, "gdt media onADShow", onoff);
            }

            @Override
            public void onADExpose() {
                ZplayDebug.i(TAG, "gdt media onADExpose", onoff);
                layerExposure();
            }

            @Override
            public void onReward() {
                ZplayDebug.i(TAG, "gdt media onReward", onoff);
                layerIncentived();
            }

            @Override
            public void onADClick() {
                ZplayDebug.i(TAG, "gdt media onADClick", onoff);
                layerClicked();
            }

            @Override
            public void onVideoComplete() {
                ZplayDebug.i(TAG, "gdt media onVideoComplete", onoff);
            }

            @Override
            public void onADClose() {
                ZplayDebug.i(TAG, "gdt media onADClose", onoff);
                layerClosed();
            }

            @Override
            public void onError(AdError adError) {
                if (adError == null) {
                    ZplayDebug.d(TAG, "gdt media failed adError = null", onoff);
                    layerPreparedFailed(recodeError(null));
                    return;
                }
                ZplayDebug.d(TAG, "gdt media failed ErrorCode:" + adError.getErrorCode() + " msg:" + adError.getErrorMsg(), onoff);
                layerPreparedFailed(recodeError(adError));
            }
        };
    }

    @Override
    protected void callOnActivityDestroy() {
    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }
}
