package com.yumi.android.sdk.ads.adapter.playableads;

import android.app.Activity;

import com.playableads.PlayLoadingListener;
import com.playableads.PlayPreloadingListener;
import com.playableads.PlayableAds;
import com.playableads.SimplePlayLoadingListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

/**
 * Created by syj on 2017/10/12.
 */
public class PlayableadsMediaAdapter extends YumiCustomerMediaAdapter {
    private PlayPreloadingListener listener;
    private PlayableAds playable;
    private Activity activity;
    private YumiProviderBean provoder;
    private String TAG = "PlayableadsMediaAdapter";

    protected PlayableadsMediaAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
        this.activity = activity;
        this.provoder = yumiProviderBean;
    }

    @Override
    protected void onPrepareMedia() {

    }

    @Override
    protected void onShowMedia() {
        PlayableAds.getInstance().presentPlayableAD(activity, new SimplePlayLoadingListener() {
            @Override
            public void playableAdsIncentive() {
                // 广告展示完成，回到原页面，此时可以给用户奖励了。
                layerIncentived();
            }

            @Override
            public void onAdsError(int errorCode, String message) {
                // 广告展示失败，根据错误码和错误信息定位问题
                ZplayDebug.d(TAG, "Playable media Video Show Error: "+message, onoff);
            }

            @Override
            public void onVideoFinished() {
                super.onVideoFinished();
                ZplayDebug.d(TAG, "Playable media Video Finish: ", onoff);
                layerMediaEnd();
                layerClosed();
            }

            @Override
            public void onVideoStart() {
                super.onVideoStart();
                ZplayDebug.d(TAG, "Playable media Video Start: ", onoff);
                layerExposure();
                layerMediaStart();
            }
        });

    }

    @Override
    protected boolean isMediaReady() {
        if(playable.canPresentAd()){
            return true;
        }else{
            return false;
        }
    }

    @Override
    protected void init() {
        playable = PlayableAds.init(getActivity(), provoder.getKey1(), provoder.getKey2());
        playable.requestPlayableAds(new PlayPreloadingListener() {
            @Override
            public void onLoadFinished() {
                ZplayDebug.d(TAG, "Playable media Ready ", onoff);
                layerPrepared();
            }

            @Override
            public void onLoadFailed(int erroCode, String s) {
                if(erroCode ==204){
                    layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);

                }else if(erroCode ==400){
                    layerPreparedFailed(LayerErrorCode.ERROR_INTERNAL);
                }

            }
        });
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
