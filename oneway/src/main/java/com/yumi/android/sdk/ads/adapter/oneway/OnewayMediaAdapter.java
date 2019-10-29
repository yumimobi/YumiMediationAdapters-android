package com.yumi.android.sdk.ads.adapter.oneway;

import android.app.Activity;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import mobi.oneway.export.Ad.OWRewardedAd;
import mobi.oneway.export.Ad.OnewaySdk;
import mobi.oneway.export.AdListener.OWRewardedAdListener;
import mobi.oneway.export.enums.OnewayAdCloseType;
import mobi.oneway.export.enums.OnewaySdkError;

/**
 * Created by Administrator on 2017/10/27.
 */

public class OnewayMediaAdapter extends YumiCustomerMediaAdapter {
    private String TAG = "OnewayMediaAdapter";
    private OWRewardedAdListener listener;
    private boolean isRewarded = false;
    private static boolean isOnewayMediaInit = false;

    protected OnewayMediaAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
    }

    @Override
    protected void onPrepareMedia() {
        ZplayDebug.d(TAG, "load new media");
        if (!isOnewayMediaInit) {
            OWRewardedAd.init(getActivity(), listener);
            isOnewayMediaInit = true;
        }
        OWRewardedAd.setListener(listener);
    }

    @Override
    protected void onShowMedia() {
        OWRewardedAd.show(getActivity(), getProvider().getKey1());
    }

    @Override
    protected boolean isMediaReady() {
        return OWRewardedAd.isReady();
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "init: publishId: " + getProvider().getKey1());
        createListener();
        OnewaySdk.init(getContext());
        OnewaySdk.configure(getActivity(), getProvider().getKey1());
    }

    private void createListener() {
        listener = new OWRewardedAdListener() {

            @Override
            public void onAdReady() {
                ZplayDebug.d(TAG, "onAdReady: ");
                layerPrepared();
            }

            @Override
            public void onAdShow(String s) {
                ZplayDebug.d(TAG, "onAdShow: " + s);
                isRewarded = false;
                layerExposure();
                layerStartPlaying();
            }

            @Override
            public void onAdClick(String s) {
                ZplayDebug.d(TAG, "onAdClick: " + s);
                layerClicked();
            }

            @Override
            public void onAdClose(String s, OnewayAdCloseType onewayAdCloseType) {
                ZplayDebug.d(TAG, "onAdClose: " + s + ", close type: " + onewayAdCloseType);
                layerClosed(isRewarded);
            }

            @Override
            public void onAdFinish(String s, OnewayAdCloseType onewayAdCloseType, String s1) {
                ZplayDebug.d(TAG, "onAdFinish: " + s + ", close type: " + onewayAdCloseType + ", " + s1);
                if (onewayAdCloseType == OnewayAdCloseType.COMPLETED) {
                    isRewarded = true;
                    layerIncentived();
                }
            }

            @Override
            public void onSdkError(OnewaySdkError onewaySdkError, String s) {
                ZplayDebug.d(TAG, "onSdkError: " + onewaySdkError + ", " + s);
                layerPreparedFailed(decodeError(onewaySdkError, s));
            }
        };

    }

    private AdError decodeError(OnewaySdkError onewaySdkError, String msg) {
        if (onewaySdkError == null) {
            AdError result = new AdError(LayerErrorCode.ERROR_INTERNAL);
            result.setErrorMessage("Oneway errorMsg: " + msg);
            return result;
        }

        AdError result;
        if (onewaySdkError.equals(OnewaySdkError.CAMPAIGN_NO_FILL)) {
            result = new AdError(LayerErrorCode.ERROR_NO_FILL);
        } else if (onewaySdkError.equals(OnewaySdkError.INITIALIZE_FAILED)) {
            result = new AdError(LayerErrorCode.ERROR_NETWORK_ERROR);
        } else {
            result = new AdError(LayerErrorCode.ERROR_INTERNAL);
        }
        result.setErrorMessage("Oneway errorName: " + onewaySdkError + " errorMsg: " + msg);
        return result;
    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }

    @Override
    public String getProviderVersion() {
        return "2.3.4";
    }
}
