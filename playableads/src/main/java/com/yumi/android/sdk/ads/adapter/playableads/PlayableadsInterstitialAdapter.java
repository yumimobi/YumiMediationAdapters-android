package com.yumi.android.sdk.ads.adapter.playableads;

import android.app.Activity;

import com.playableads.PlayPreloadingListener;
import com.playableads.PlayableAds;
import com.playableads.SimplePlayLoadingListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

/**
 * Created by syj on 2017/10/12.
 */
public class PlayableadsInterstitialAdapter extends YumiCustomerInterstitialAdapter {
    private PlayPreloadingListener listener;
    private PlayableAds playable;
    private Activity activity;
    private YumiProviderBean provoder;
    private String TAG = "PlayableadsMediaAdapter";

    private static final int REQUEST_NEXT_MEDIA = 0x001;

    protected PlayableadsInterstitialAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
        this.activity = activity;
        this.provoder = yumiProviderBean;
    }

    @Override
    protected void onPrepareInterstitial() {
        ZplayDebug.d(TAG, "Playable Interstitial onPrepareMedia: ", onoff);
        if (playable != null && listener != null) {
            ZplayDebug.d(TAG, "Playable Interstitial REQUEST_NEXT_MEDIA ", onoff);
            playable.requestPlayableAds(provoder.getKey2(),listener);
        }
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        PlayableAds.getInstance().presentPlayableAD(provoder.getKey2(), new SimplePlayLoadingListener() {
            @Override
            public void playableAdsIncentive() {
                // 广告展示完成，回到原页面，此时可以给用户奖励了。
                ZplayDebug.d(TAG, "Playable media Video playableAdsIncentive: ", onoff);
//                layerIncentived();
            }

            @Override
            public void onAdsError(int errorCode, String message) {
                // 广告展示失败，根据错误码和错误信息定位问题
                ZplayDebug.d(TAG, "Playable media Video Show Error: "+message, onoff);
            }

            @Override
            public void onVideoFinished() {
                super.onVideoFinished();
                ZplayDebug.d(TAG, "Playable Interstitial Finish: ", onoff);
                layerMediaEnd();
            }

            @Override
            public void onVideoStart() {
                super.onVideoStart();
                ZplayDebug.d(TAG, "Playable Interstitial Start: ", onoff);
                layerExposure();
            }

            @Override
            public void onLandingPageInstallBtnClicked() {
                layerClicked(-99f,-99f);
                super.onLandingPageInstallBtnClicked();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                ZplayDebug.d(TAG, "Playable Interstitial AdClosed: ", onoff);
                layerClosed();
            }
        });
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        if(playable.canPresentAd(provoder.getKey2())){
            ZplayDebug.d(TAG, "Playable Interstitial isMediaReady true", onoff);
            return true;
        }else{
            return false;
        }
    }




    @Override
    protected void init() {
        try {
            playable = PlayableAds.init(getActivity(), provoder.getKey1());
            PlayableAds.getInstance().setAutoLoadAd(false);
            listener = new PlayPreloadingListener() {
                @Override
                public void onLoadFinished() {
                    ZplayDebug.d(TAG, "Playable media Ready ", onoff);
                    layerPrepared();
                }
                @Override
                public void onLoadFailed(int erroCode, String s) {
                    ZplayDebug.d(TAG, "Playable media onLoadFailed erroCode：" + erroCode + "   s:" + s, onoff);
                    if (erroCode == 204) {
                        layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);

                    } else if (erroCode == 400) {
                        layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
                    }
                }
            };
        }catch (Exception e)
        {
            ZplayDebug.e(TAG, "Playable Interstitial init error ",e, onoff);
        }
    }

    @Override
    protected void callOnActivityDestroy() {
        try {
            if (playable != null) {
                ZplayDebug.d(TAG, "Playable Interstitial onDestroy ", onoff);
                playable.onDestroy();
            }
        }catch (Exception e)
        {
            ZplayDebug.e(TAG, "Playable Interstitial callOnActivityDestroy error : ",e, onoff);
        }
    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }

    @Override
    public boolean onActivityBackPressed() {
        return false;
    }
}