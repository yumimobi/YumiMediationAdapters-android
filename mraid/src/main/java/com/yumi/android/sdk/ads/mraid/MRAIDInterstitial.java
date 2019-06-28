package com.yumi.android.sdk.ads.mraid;

import android.content.Context;
import android.util.Log;

import com.yumi.android.sdk.ads.mraid.internal.MRAIDLog;

public class MRAIDInterstitial implements MRAIDViewListener {

    private final static String TAG = "MRAIDInterstitial";

    private MRAIDInterstitialListener listener;

    private MRAIDView mraidView;
    private boolean isReady;

    public MRAIDInterstitial(
            Context context,
            String baseUrl,
            String data,
            String[] supportedNativeFeatures,
            MRAIDInterstitialListener listener,
            MRAIDNativeFeatureListener nativeFeatureListener) {

        this.listener = listener;

        mraidView = new MRAIDView(context, baseUrl, data, supportedNativeFeatures, this, nativeFeatureListener, true);
    }

    public void show() {
        if (!isReady) {
            MRAIDLog.w(TAG, "interstitial is not ready to show");
            return;
        }
        mraidView.showAsInterstitial();
    }

    // MRAIDViewListener implementation

    @Override
    public void mraidViewLoaded(MRAIDView mraidView) {
        Log.d(TAG + "-MRAIDViewListener", "mraidViewLoaded");
        isReady = true;
        if (listener != null) {
            listener.mraidInterstitialLoaded(this);
        }
    }

    @Override
    public void mraidViewExpand(MRAIDView mraidView) {
        Log.d(TAG + "-MRAIDViewListener", "mraidViewExpand");
        if (listener != null) {
            listener.mraidInterstitialShow(this);
        }
    }

    @Override
    public void mraidViewClose(MRAIDView mraidView) {
        Log.d(TAG + "-MRAIDViewListener", "mraidViewClose");
        isReady = false;
        mraidView = null;
        if (listener != null) {
            listener.mraidInterstitialHide(this);
        }
    }

    @Override
    public boolean mraidViewResize(MRAIDView mraidView, int width, int height, int offsetX, int offsetY) {
        return true;
    }

}
