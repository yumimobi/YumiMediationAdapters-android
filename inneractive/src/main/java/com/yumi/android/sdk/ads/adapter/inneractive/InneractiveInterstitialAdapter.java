package com.yumi.android.sdk.ads.adapter.inneractive;

import android.app.Activity;

import com.fyber.inneractive.sdk.external.InneractiveAdManager;
import com.fyber.inneractive.sdk.external.InneractiveAdRequest;
import com.fyber.inneractive.sdk.external.InneractiveAdSpot;
import com.fyber.inneractive.sdk.external.InneractiveAdSpotManager;
import com.fyber.inneractive.sdk.external.InneractiveErrorCode;
import com.fyber.inneractive.sdk.external.InneractiveFullscreenAdEventsListener;
import com.fyber.inneractive.sdk.external.InneractiveFullscreenUnitController;
import com.fyber.inneractive.sdk.external.InneractiveFullscreenVideoContentController;
import com.fyber.inneractive.sdk.external.InneractiveUnitController;
import com.fyber.inneractive.sdk.external.VideoContentListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.inneractive.InneractiveUtil.initInneractiveSDK;
import static com.yumi.android.sdk.ads.adapter.inneractive.InneractiveUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.inneractive.InneractiveUtil.sdkVersion;

public class InneractiveInterstitialAdapter extends YumiCustomerInterstitialAdapter {

    private static final String TAG = "InneractiveInterstitialAdapter";
    private InneractiveAdSpot mInterstitialSpot;
    private InneractiveAdSpot.RequestListener requestListener;
    private InneractiveFullscreenAdEventsListener fullscreenAdEventsListener;
    private VideoContentListener videoContentListener;

    protected InneractiveInterstitialAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }


    @Override
    protected void onPrepareInterstitial() {
        ZplayDebug.d(TAG, "load new interstitial wasInit = " + InneractiveAdManager.wasInitialized());
        if (!InneractiveAdManager.wasInitialized()) {
            initInneractiveSDK(getActivity(), getProvider().getKey1());
            if (!InneractiveAdManager.wasInitialized()) {
                layerPreparedFailed(recodeError(LayerErrorCode.ERROR_INTERNAL));
                return;
            }
        }

        if (mInterstitialSpot == null) {
            // First create a spot
            mInterstitialSpot = InneractiveAdSpotManager.get().createSpot();
        }

        loadAd();
    }

    private void loadAd() {
        ZplayDebug.d(TAG, "loadAd");
        if (mInterstitialSpot != null && requestListener != null) {
            // Now create a full screen unit controller
            InneractiveFullscreenUnitController fullscreenUnitController = new InneractiveFullscreenUnitController();
            // Configure the request
            InneractiveAdRequest request = new InneractiveAdRequest(getProvider().getKey2());

            // Add the unit controller to the spot
            mInterstitialSpot.addUnitController(fullscreenUnitController);

            mInterstitialSpot.setRequestListener(requestListener);

            mInterstitialSpot.requestAd(request);
        }
    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        if (mInterstitialSpot != null && mInterstitialSpot.isReady()) {
            InneractiveFullscreenUnitController fullscreenUnitController = (InneractiveFullscreenUnitController) mInterstitialSpot.getSelectedUnitController();
            fullscreenUnitController.setEventsListener(fullscreenAdEventsListener);

            // Add video content controller, for controlling video ads
            InneractiveFullscreenVideoContentController videoContentController = new InneractiveFullscreenVideoContentController();
            videoContentController.setEventsListener(videoContentListener);

            // If you would like to change the full screen video behaviour, you can create and configure a video content controller, and add it to the unit controller
            // Override the default behaviour - show the controls within the video frame
            videoContentController.setOverlayOutside(false);

            // Now add the content controller to the unit controller
            fullscreenUnitController.addContentController(videoContentController);

            fullscreenUnitController.show(getActivity());
        }
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        return mInterstitialSpot != null && mInterstitialSpot.isReady();
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "init key1: " + getProvider().getKey1() + "key2: " + getProvider().getKey2());

        createListener();
    }

    private void createListener() {
        requestListener = new InneractiveAdSpot.RequestListener() {
            @Override
            public void onInneractiveSuccessfulAdRequest(InneractiveAdSpot adSpot) {
                ZplayDebug.d(TAG, "onInneractiveSuccessfulAdRequest");
                layerPrepared();
            }

            @Override
            public void onInneractiveFailedAdRequest(InneractiveAdSpot adSpot, InneractiveErrorCode errorCode) {
                ZplayDebug.d(TAG, "onInneractiveFailedAdRequest: " + errorCode);
                layerPreparedFailed(recodeError(errorCode));
            }
        };


        fullscreenAdEventsListener = new InneractiveFullscreenAdEventsListener() {
            @Override
            public void onAdDismissed(InneractiveAdSpot adSpot) {
                ZplayDebug.d(TAG, "onAdDismissed");
                layerClosed();
            }

            @Override
            public void onAdImpression(InneractiveAdSpot adSpot) {
                ZplayDebug.d(TAG, "onAdImpression");
                layerExposure();
                layerStartPlaying();
            }

            @Override
            public void onAdClicked(InneractiveAdSpot adSpot) {
                ZplayDebug.d(TAG, "onAdClicked");
                layerClicked(-99f, -99f);
            }

            @Override
            public void onAdWillOpenExternalApp(InneractiveAdSpot adSpot) {
                ZplayDebug.d(TAG, "onAdWillOpenExternalApp");
            }

            @Override
            public void onAdEnteredErrorState(InneractiveAdSpot inneractiveAdSpot, InneractiveUnitController.AdDisplayError adDisplayError) {
                ZplayDebug.d(TAG, "onAdEnteredErrorState :" + adDisplayError.getMessage());
            }

            @Override
            public void onAdWillCloseInternalBrowser(InneractiveAdSpot adSpot) {
                ZplayDebug.d(TAG, "onAdWillCloseInternalBrowser");
            }
        };

        videoContentListener = new VideoContentListener() {
            @Override
            public void onProgress(int totalDurationInMsec, int positionInMsec) {
                ZplayDebug.d(TAG, "onProgress");
            }

            @Override
            public void onCompleted() {
                ZplayDebug.d(TAG, "onCompleted");
            }

            @Override
            public void onPlayerError() {
                ZplayDebug.d(TAG, "onPlayerError");
            }
        };
    }

    @Override
    protected void onDestroy() {
        if (mInterstitialSpot != null) {
            mInterstitialSpot.destroy();
            mInterstitialSpot = null;
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

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
