package com.yumi.android.sdk.ads.adapter.mobvista;

import android.app.Activity;

import com.mintegral.msdk.out.MTGRewardVideoHandler;
import com.mintegral.msdk.out.RewardVideoListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.mobvista.Util.sdkVersion;
import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_FAILED_TO_SHOW;

/**
 * Created by hjl on 2017/11/28.
 */
public class MobvistaMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "MobvistaMediaAdapter";
    private MTGRewardVideoHandler mMvRewardVideoHandler;

    protected MobvistaMediaAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
    }

    @Override
    protected void onPrepareMedia() {
        try {
            ZplayDebug.d(TAG, "load new media");
            if (mMvRewardVideoHandler != null) {
                mMvRewardVideoHandler.load();
            } else {
                initHandler();
                mMvRewardVideoHandler.load();
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "onPrepareMedia: exception.", e);
        }
    }

    @Override
    protected void onShowMedia() {
        try {
            ZplayDebug.d(TAG, "onShowMedia: ");
            if (mMvRewardVideoHandler != null) {
                if (mMvRewardVideoHandler.isReady()) {
                    ZplayDebug.d(TAG, "onShowMedia: rewardId: " + getProvider().getKey4());
                    mMvRewardVideoHandler.show(getProvider().getKey4());
                }
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "onShowMedia: exception.", e);
        }
    }

    @Override
    protected boolean isMediaReady() {
        ZplayDebug.d(TAG, "isMediaReady: ");
        if (mMvRewardVideoHandler != null) {
            boolean isReady = mMvRewardVideoHandler.isReady();
            ZplayDebug.d(TAG, "isMediaReady: " + isReady);
            return isReady;
        }
        return false;
    }

    @Override
    protected void init() {
        try {
            final String appId = getProvider().getKey1();
            final String appKey = getProvider().getKey2();
            ZplayDebug.d(TAG, "init: appId: " + appId + ", appKey: " + appKey);
            Util.initSDK(getContext(), appId, appKey);
            initHandler();
        } catch (Exception e) {
            ZplayDebug.e(TAG, "init: exception.", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMvRewardVideoHandler = null;
    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }

    private void initHandler() {
        try {
            ZplayDebug.d(TAG, "initHandler: ");
            mMvRewardVideoHandler = new MTGRewardVideoHandler(getActivity(), getProvider().getKey4(), getProvider().getKey3()); //UnitId
            mMvRewardVideoHandler.setRewardVideoListener(new RewardVideoListener() {

                @Override
                public void onVideoLoadSuccess(String unitId, String s1) {
                    ZplayDebug.d(TAG, "onVideoLoadSuccess: " + unitId);
                    layerPrepared();
                }

                @Override
                public void onLoadSuccess(String s, String s1) {
                    ZplayDebug.d(TAG, "onLoadSuccess: ");
                }

                @Override
                public void onVideoLoadFail(String errorMsg) {
                    ZplayDebug.d(TAG, "onVideoLoadFail: " + errorMsg);
                    AdError error = new AdError(LayerErrorCode.ERROR_NO_FILL);
                    error.setErrorMessage("minteral errorMsg: " + errorMsg);
                    layerPreparedFailed(error);
                }

                @Override
                public void onShowFail(String errorMsg) {
                    ZplayDebug.d(TAG, "onShowFail: " + errorMsg);
                    AdError adError = new AdError(ERROR_FAILED_TO_SHOW);
                    adError.setErrorMessage("Mobvista errorMsg: " + errorMsg);
                    layerExposureFailed(adError);
                }

                @Override
                public void onVideoAdClicked(String s, String s1) {
                    ZplayDebug.d(TAG, "onVideoAdClicked: " + s);
                    layerClicked();
                }

                @Override
                public void onAdShow() {
                    ZplayDebug.d(TAG, "onAdShow: ");
                    layerExposure();
                    layerStartPlaying();
                }

                @Override
                public void onAdClose(boolean isCompleteView, String rewardName, float rewardCount) {
                    ZplayDebug.d(TAG, "onAdClose: " + isCompleteView + ", " + rewardName + ", " + rewardCount);
                    //三个参数为：是否播放完，奖励名，奖励积分
                    if (isCompleteView) {
                        layerIncentived();
                    }
                    layerClosed(isCompleteView);
                }

                @Override
                public void onVideoComplete(String s, String s1) {

                }

                @Override
                public void onEndcardShow(String s, String s1) {

                }

            });
        } catch (Exception e) {
            ZplayDebug.e(TAG, "initHandler: exception.", e);
        }
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}