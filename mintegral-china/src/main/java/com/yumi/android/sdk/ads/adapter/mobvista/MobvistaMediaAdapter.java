package com.yumi.android.sdk.ads.adapter.mobvista;

import android.app.Activity;

import com.mintegral.msdk.MIntegralConstans;
import com.mintegral.msdk.MIntegralSDK;
import com.mintegral.msdk.out.MIntegralSDKFactory;
import com.mintegral.msdk.out.MTGRewardVideoHandler;
import com.mintegral.msdk.out.RewardVideoListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.YumiSettings;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.YumiGDPRStatus;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.Map;

import static com.yumi.android.sdk.ads.adapter.mobvista.Util.sdkVersion;
import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_FAILED_TO_SHOW;

/**
 * Created by hjl on 2017/11/28.
 */
public class MobvistaMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "MobvistaMediaAdapter-China";
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
            MIntegralSDK sdk = MIntegralSDKFactory.getMIntegralSDK();
            Map<String, String> map = sdk.getMTGConfigurationMap(appId, appKey); //appId, appKey
            if (YumiSettings.getGDPRStatus() != YumiGDPRStatus.UNKNOWN) {
                int isConsent = YumiSettings.getGDPRStatus() == YumiGDPRStatus.PERSONALIZED ? MIntegralConstans.IS_SWITCH_ON : MIntegralConstans.IS_SWITCH_OFF;
                sdk.setUserPrivateInfoType(getActivity(), MIntegralConstans.AUTHORITY_ALL_INFO, isConsent);
            }
            sdk.init(map, getContext());
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
            mMvRewardVideoHandler = new MTGRewardVideoHandler(getActivity(), getProvider().getKey3()); //UnitId
            mMvRewardVideoHandler.setRewardVideoListener(new RewardVideoListener() {

                @Override
                public void onVideoLoadSuccess(String unitId) {
                    ZplayDebug.d(TAG, "onVideoLoadSuccess: " + unitId);
                    layerPrepared();
                }

                @Override
                public void onLoadSuccess(String unitId) {
                    ZplayDebug.d(TAG, "onLoadSuccess: ");
                }

                @Override
                public void onVideoLoadFail(String errorMsg) {
                    ZplayDebug.d(TAG, "onVideoLoadFail: " + errorMsg);
                    AdError error = new AdError(LayerErrorCode.ERROR_NO_FILL);
                    error.setErrorMessage("minteral-China errorMsg: " + errorMsg);
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
                public void onVideoAdClicked(String unitId) {
                    ZplayDebug.d(TAG, "onVideoAdClicked: " + unitId);
                    layerClicked();
                }

                @Override
                public void onVideoComplete(String s) {

                }

                @Override
                public void onEndcardShow(String s) {

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