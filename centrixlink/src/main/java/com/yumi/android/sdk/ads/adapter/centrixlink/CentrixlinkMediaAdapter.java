package com.yumi.android.sdk.ads.adapter.centrixlink;

import android.app.Activity;

import com.centrixlink.SDK.Centrixlink;
import com.centrixlink.SDK.CentrixlinkVideoADListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.Map;

/**
 * Created by hjl on 2017/11/6.
 */

public class CentrixlinkMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "CentrixlinkMediaAdapter";
    //init SDK
    private final Centrixlink centrixlink =  Centrixlink.sharedInstance();
    private CentrixlinkVideoADListener eventListener;

    /**
     设置是否跟随应用方向,默认值为true;
     */
     //  centrixlink.setEnableFollowAppOrientation(enable);

    protected CentrixlinkMediaAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    protected void onPrepareMedia() {
        ZplayDebug.d(TAG, "Centrixlink request new media", onoff);
        if (centrixlink!=null && centrixlink.hasPreloadAD()) {
            ZplayDebug.d(TAG, "Centrixlink media prapared", onoff);
            layerPrepared();
        }
    }

    @Override
    protected void onShowMedia() {
        ZplayDebug.d(TAG, "Centrixlink media onShowMedia", onoff);
        if (centrixlink!=null) {
            centrixlink.playAD(getActivity());
        }
    }

    @Override
    protected boolean isMediaReady() {
        if (centrixlink!=null) {
            if(centrixlink.hasPreloadAD())
            {
                ZplayDebug.d(TAG, "Centrixlink media prapared", onoff);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "Centrixlink media init", onoff);
        createListener();
        //SDK启动
        centrixlink.startWithAppID(getActivity(),getProvider().getKey1(),getProvider().getKey2());
    }

    private void createListener() {
        eventListener =  new CentrixlinkVideoADListener() {
            @Override
            public void centrixLinkVideoADWillShow(Map map){
                //视频广告即将播放
                ZplayDebug.v(TAG, "Centrixlink media centrixLinkVideoADWillShow", onoff);
            }

            @Override
            public void centrixLinkVideoADDidShow(Map map) {
                //视频广告播放开始
                ZplayDebug.v(TAG, "Centrixlink media centrixLinkVideoADDidShow", onoff);
                layerExposure();
                layerMediaStart();
            }

            @Override
            public void centrixLinkHasPreloadAD(boolean isPreloadFinished) {
                //本地是否有预加载的广告 false表示没有，true表示有
                ZplayDebug.v(TAG, "Centrixlink media centrixLinkHasPreloadAD isPreloadFinished:"+isPreloadFinished, onoff);
                if(isPreloadFinished) {
                    layerPrepared();
                }
            }

            @Override
            public void centrixLinkVideoADShowFail(Map map) {
                //视频广告播放失败
                //key:"error", value:AD_PlayError
                /* AD_PlayError
                    100 	广告的播放间隔时间不满足条件
                    101	本地没有可播放广告
                    105	当前正在播放其它广告
                    106   处于静默状态
                    107   本地广告资源不可用
                    108   当前用户播放超限
                */
                try {
                    String errorCode = map.get("error").toString();
                    ZplayDebug.v(TAG, "Centrixlink media centrixLinkVideoADShowFail error:" + errorCode, onoff);
                    if ("101".equals(errorCode)) {
                        layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
                    } else {
                        layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
                    }
                }catch (Exception e){
                    ZplayDebug.v(TAG, "Centrixlink media centrixLinkVideoADShowFail try error:" + e, onoff);
                }
            }

            @Override
            public void centrixLinkVideoADAction(Map map) {
                //视频广告触发了点击事件
                ZplayDebug.d(TAG, "Centrixlink media clicked", onoff);
                layerClicked();
            }

            @Override
            public void centrixLinkVideoADClose(Map map) {
                //视频广告关闭
                //key:"ADID", value:String
                //key:"playFinished", value:boolean;true表示播放完成
                //key:"isClick", value:boolean;true表示触发
                try {
                    boolean playFinished =(boolean) map.get("playFinished");
                    ZplayDebug.v(TAG, "Centrixlink media centrixLinkVideoADClose playFinished:"+playFinished, onoff);
                    layerIncentived();
                    if(playFinished){
                        layerMediaEnd();
                    }
                    layerClosed();
                }catch (Exception e){}
            }

        };
        centrixlink.addEventListeners(eventListener);
    }

    @Override
    protected void callOnActivityDestroy() {
        final Centrixlink centrixlink =   Centrixlink.sharedInstance();
        //重置SDK，防止内存泄漏
        centrixlink.removeEventListeners(eventListener);
        centrixlink.setDebugCallBack(null);
    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }
}
