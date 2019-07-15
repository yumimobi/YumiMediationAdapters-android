package com.yumi.android.sdk.ads.adapter.inneractive;


import android.app.Activity;

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
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.inneractive.InneractiveUtil.initInneractiveSDK;
import static com.yumi.android.sdk.ads.adapter.inneractive.InneractiveUtil.recodeError;


public class InneractiveMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "InneractiveMediaAdapter";
    private InneractiveAdSpot mVideoSpot;
    private InneractiveAdSpot.RequestListener requestListener;
    private InneractiveFullscreenAdEventsListener fullscreenAdEventsListener;
    private VideoContentListener videoContentListener;
    private boolean isRewarded = false;

    protected InneractiveMediaAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }


    @Override
    protected void onPrepareMedia() {
        ZplayDebug.d(TAG, "inneractive request new media", onoff);
        if (mVideoSpot != null && requestListener != null) {
            // Now create a full screen unit controller
            InneractiveFullscreenUnitController fullscreenUnitController = new InneractiveFullscreenUnitController();
            // Configure the request
            InneractiveAdRequest request = new InneractiveAdRequest(getProvider().getKey2());

            // Add the unit controller to the spot
            mVideoSpot.addUnitController(fullscreenUnitController);

            mVideoSpot.setRequestListener(requestListener);

            mVideoSpot.requestAd(request);
        }
    }

    @Override
    protected void onShowMedia() {
        ZplayDebug.d(TAG, "inneractive media show", onoff);
        if (mVideoSpot != null && mVideoSpot.isReady()) {
            InneractiveFullscreenUnitController fullscreenUnitController = (InneractiveFullscreenUnitController) mVideoSpot.getSelectedUnitController();
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
    protected boolean isMediaReady() {
        return mVideoSpot != null && mVideoSpot.isReady();
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "inneractive media init key1: " + getProvider().getKey1() + "key2: " + getProvider().getKey2(), onoff);
        initInneractiveSDK(getActivity(), getProvider().getKey1());

        createListener();

        // Create interstitial spot
        if (mVideoSpot != null) {
            mVideoSpot.destroy();
            mVideoSpot = null;
        }
        // First create a spot
        mVideoSpot = InneractiveAdSpotManager.get().createSpot();
    }

    private void createListener() {
        requestListener = new InneractiveAdSpot.RequestListener() {
            @Override
            public void onInneractiveSuccessfulAdRequest(InneractiveAdSpot adSpot) {
                ZplayDebug.d(TAG, "inneractive media onInneractiveSuccessfulAdRequest", onoff);
                layerPrepared();
                isRewarded = false;
            }

            @Override
            public void onInneractiveFailedAdRequest(InneractiveAdSpot adSpot, InneractiveErrorCode errorCode) {
                ZplayDebug.d(TAG, "inneractive media onInneractiveFailedAdRequest：" + errorCode.toString(), onoff);
                layerPreparedFailed(recodeError(errorCode));
                isRewarded = false;
            }
        };


        fullscreenAdEventsListener = new InneractiveFullscreenAdEventsListener() {
            @Override
            public void onAdDismissed(InneractiveAdSpot adSpot) {
                ZplayDebug.d(TAG, "inneractive media onAdDismissed", onoff);
                layerClosed(isRewarded);
            }

            @Override
            public void onAdImpression(InneractiveAdSpot adSpot) {
                ZplayDebug.d(TAG, "inneractive media onAdImpression", onoff);
                layerExposure();
                layerStartPlaying();
                isRewarded = false;
            }

            @Override
            public void onAdClicked(InneractiveAdSpot adSpot) {
                ZplayDebug.d(TAG, "inneractive media onAdClicked", onoff);
                layerClicked();
            }

            @Override
            public void onAdWillOpenExternalApp(InneractiveAdSpot adSpot) {
                ZplayDebug.d(TAG, "inneractive media onAdWillOpenExternalApp", onoff);
            }

            @Override
            public void onAdEnteredErrorState(InneractiveAdSpot inneractiveAdSpot, InneractiveUnitController.AdDisplayError adDisplayError) {
                ZplayDebug.d(TAG, "inneractive media onAdEnteredErrorState :" + adDisplayError.getMessage(), onoff);
            }

            @Override
            public void onAdWillCloseInternalBrowser(InneractiveAdSpot adSpot) {
                ZplayDebug.d(TAG, "inneractive media onAdWillCloseInternalBrowser", onoff);
            }
        };

        videoContentListener = new VideoContentListener() {
            @Override
            public void onProgress(int totalDurationInMsec, int positionInMsec) {
                ZplayDebug.d(TAG, "inneractive media onProgress", onoff);
            }

            @Override
            public void onCompleted() {
                ZplayDebug.d(TAG, "inneractive media onCompleted", onoff);
                isRewarded = true;
                layerIncentived();
            }

            @Override
            public void onPlayerError() {
                ZplayDebug.d(TAG, "inneractive media onPlayerError", onoff);
                isRewarded = false;
            }
        };
    }

    @Override
    protected void onDestroy() {
        if (mVideoSpot != null) {
            mVideoSpot.destroy();
            mVideoSpot = null;
        }
    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }
}
