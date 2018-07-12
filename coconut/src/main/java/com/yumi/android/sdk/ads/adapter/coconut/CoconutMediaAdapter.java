package com.yumi.android.sdk.ads.adapter.coconut;

import android.app.Activity;

import com.afk.client.ads.ADSDK;
import com.afk.client.ads.AdEventListener;
import com.afk.client.ads.VideoStatus;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

/**
 * Created by Administrator on 2017/10/27.
 */

public class CoconutMediaAdapter extends YumiCustomerMediaAdapter{
    private String TAG = "CoconutMediaAdapter";
    private Activity activity;
    private AdEventListener listener;
    private boolean isReady = false;
    protected CoconutMediaAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
        this.activity = activity;
    }

    @Override
    protected void onPrepareMedia() {

    }

    @Override
    protected void onShowMedia() {
        if (ADSDK.getInstance().getAdStatus(activity) != VideoStatus.NO_AD) {
            ADSDK.getInstance().showAdVideo(activity);
        } else {
            ADSDK.getInstance().load(activity);
        }
    }

    @Override
    protected boolean isMediaReady() {
        return isReady;
    }

    @Override
    protected void init() {
        creatListener();
        ADSDK.getInstance().setDebug(false).setLogSwitch(false);
        ADSDK.getInstance().init(activity, getProvider().getKey1(), getProvider().getKey2(),listener);

    }

    private void creatListener() {
        listener = new AdEventListener() {
            @Override
            public void onAdExist(boolean isAdExist, long code) {
                if (isAdExist) {
                    ZplayDebug.d(TAG, "椰子视频有广告"+code, onoff);
                    isReady = isAdExist;
                    layerPrepared();
                } else {
                    ZplayDebug.d(TAG, "椰子视频没有广告"+code, onoff);
                }
            }

            @Override
            public void onVideoCached(boolean isCached) {
                if (isCached) {
                    ZplayDebug.d(TAG, "椰子已缓存广告视频", onoff);
                } else {
                    ZplayDebug.d(TAG, "椰子已缓存失败", onoff);
                }
            }

            @Override
            public void onVideoStart() {
                ZplayDebug.d(TAG, "Coconut media shown", onoff);
                layerExposure();
                layerMediaStart();
            }

            @Override
            public void onVideoCompletion(boolean isLookBack) {
                ZplayDebug.d(TAG, "chartboost media closed", onoff);
                layerMediaEnd();
                if (!isLookBack) {
                    // 可以发放奖励

                }
            }

            @Override
            public void onVideoClose(int currentPosition) {
//                layerClosed();
            }

            @Override
            public void onVideoError(String error) {
                if(error.equals(VideoStatus.NO_AD)){
                    layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
                }else{
                    layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
                }
            }

            @Override
            public void onLandingPageClose(boolean status) {
                //直接关闭为false，点击下载按钮关闭为true
                layerIncentived();
                layerClosed();

            }

            @Override
            public void onDownloadStart() {

            }

            @Override
            public void onNetRequestError(String error) {
                layerPreparedFailed(LayerErrorCode.ERROR_NETWORK_ERROR);
            }
        };

    }

    @Override
    protected void callOnActivityDestroy() {
        ADSDK.getInstance().release(activity);
    }

    @Override
    public void onActivityPause() {
        ADSDK.getInstance().onPause(activity);
    }

    @Override
    public void onActivityResume() {
        ADSDK.getInstance().onResume(activity);
    }
}
