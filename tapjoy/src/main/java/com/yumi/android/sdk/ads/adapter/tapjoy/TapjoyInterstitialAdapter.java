package com.yumi.android.sdk.ads.adapter.tapjoy;

import android.app.Activity;

import com.tapjoy.TJActionRequest;
import com.tapjoy.TJConnectListener;
import com.tapjoy.TJError;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.TJPlacementVideoListener;
import com.tapjoy.Tapjoy;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.tapjoy.TapjoyHelper.connectTapjoy;
import static com.yumi.android.sdk.ads.adapter.tapjoy.TapjoyHelper.recodeError;
import static com.yumi.android.sdk.ads.adapter.tapjoy.TapjoyHelper.sdkVersion;
import static com.yumi.android.sdk.ads.adapter.tapjoy.TapjoyHelper.updateGDPRStatus;

public class TapjoyInterstitialAdapter extends YumiCustomerInterstitialAdapter {

    private static final String TAG = "TapjoyInterstitialAdapter";
    private TJPlacement directPlayPlacement;

    protected TapjoyInterstitialAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
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

    @Override
    protected void onPrepareInterstitial() {
        final boolean isConnected = Tapjoy.isConnected();
        ZplayDebug.d(TAG, "onPrepareInterstitial: " + isConnected);
        Tapjoy.setActivity(getActivity());
        if (!isConnected) {
            connectTapjoy(getContext(), getProvider().getKey1(), new TJConnectListener() {
                @Override
                public void onConnectSuccess() {
                    ZplayDebug.d(TAG, "onConnectSuccess");
                    requestAd();
                }

                @Override
                public void onConnectFailure() {
                    ZplayDebug.d(TAG, "onConnectFailure");
                    layerPreparedFailed(recodeError(new TJError(0, "onConnectFailure")));
                }
            });
            return;
        }

        if (!isReady()) {
            requestAd();
        }
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        if (directPlayPlacement != null && directPlayPlacement.isContentReady()) {
            directPlayPlacement.showContent();
        }
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        return isReady();
    }

    private boolean isReady() {
        // 官方文档
        // isContentAvailable(): Whether or not content for this placement has been returned and is ready to be presented
        // isContentReady(): Whether or not the pre-loaded content for this placement has been cached and is ready to be presented
        // 以下写法参考示例代码
        return directPlayPlacement != null && directPlayPlacement.isContentAvailable() && directPlayPlacement.isContentReady();
    }

    @Override
    protected void init() {
    }

    private void requestAd() {
        ZplayDebug.d(TAG, "requestAd: " + getProvider().getKey2());
        updateGDPRStatus();
        directPlayPlacement = Tapjoy.getPlacement(getProvider().getKey2(), new TJPlacementListener() {
            @Override
            public void onRequestSuccess(TJPlacement tjPlacement) {
                ZplayDebug.d(TAG, "onRequestSuccess: " + tjPlacement.isContentReady() + " : " + tjPlacement.isContentAvailable());
                if (!tjPlacement.isContentAvailable()) {
                    layerPreparedFailed(recodeError(new TJError(-1, "No content available for placement " + tjPlacement.getName())));
                }
            }

            @Override
            public void onRequestFailure(TJPlacement tjPlacement, TJError tjError) {
                ZplayDebug.d(TAG, "onRequestFailure: " + tjError.code + " : " + tjError.message);
                layerPreparedFailed(recodeError(tjError));
            }

            @Override
            public void onContentReady(TJPlacement tjPlacement) {
                ZplayDebug.d(TAG, "onContentReady: ");
                layerPrepared();
            }

            @Override
            public void onContentShow(TJPlacement tjPlacement) {
                ZplayDebug.d(TAG, "onContentShow: ");
                layerExposure();
            }

            @Override
            public void onContentDismiss(TJPlacement tjPlacement) {
                ZplayDebug.d(TAG, "onContentDismiss: ");
                layerClosed();
            }

            @Override
            public void onPurchaseRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s) {
                ZplayDebug.d(TAG, "onPurchaseRequest: " + tjActionRequest);
                tjActionRequest.completed();
            }

            @Override
            public void onRewardRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s, int i) {
                ZplayDebug.d(TAG, "onRewardRequest: " + tjActionRequest);
                tjActionRequest.completed();
            }

            @Override
            public void onClick(TJPlacement tjPlacement) {
                ZplayDebug.d(TAG, "onClick: ");
                layerClicked(-99f, -99f);
            }
        });

        directPlayPlacement.setVideoListener(new TJPlacementVideoListener() {
            @Override
            public void onVideoStart(TJPlacement placement) {
                ZplayDebug.i(TAG, "onVideoStart: " + placement);
                layerStartPlaying();
            }

            @Override
            public void onVideoError(TJPlacement placement, String message) {
                ZplayDebug.i(TAG, "Video error: " + message + " for " + placement.getName());
            }

            @Override
            public void onVideoComplete(TJPlacement placement) {
                ZplayDebug.d(TAG, "onVideoComplete: ");
            }

        });

        directPlayPlacement.requestContent();
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
