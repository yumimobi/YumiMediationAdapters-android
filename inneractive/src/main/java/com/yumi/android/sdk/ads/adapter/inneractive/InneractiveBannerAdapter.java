package com.yumi.android.sdk.ads.adapter.inneractive;

import android.app.Activity;
import android.content.Context;
import android.widget.FrameLayout;

import com.fyber.inneractive.sdk.external.InneractiveAdManager;
import com.fyber.inneractive.sdk.external.InneractiveAdRequest;
import com.fyber.inneractive.sdk.external.InneractiveAdSpot;
import com.fyber.inneractive.sdk.external.InneractiveAdSpotManager;
import com.fyber.inneractive.sdk.external.InneractiveAdViewEventsListener;
import com.fyber.inneractive.sdk.external.InneractiveAdViewUnitController;
import com.fyber.inneractive.sdk.external.InneractiveErrorCode;
import com.fyber.inneractive.sdk.external.InneractiveUnitController;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.AdSize;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.inneractive.InneractiveUtil.initInneractiveSDK;
import static com.yumi.android.sdk.ads.adapter.inneractive.InneractiveUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.inneractive.InneractiveUtil.sdkVersion;

public class InneractiveBannerAdapter extends YumiCustomerBannerAdapter {

    private static final String TAG = "InneractiveBannerAdapter";
    private InneractiveAdSpot mBannerSpot;
    private InneractiveAdSpot.RequestListener requestListener;
    private FrameLayout container;

    protected InneractiveBannerAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    protected void onPrepareBannerLayer() {
        ZplayDebug.d(TAG, "load new banner wasInit = " + InneractiveAdManager.wasInitialized());
        if (!InneractiveAdManager.wasInitialized()) {
            initInneractiveSDK(getActivity(), getProvider().getKey1());
            if (!InneractiveAdManager.wasInitialized()) {
                layerPreparedFailed(recodeError(LayerErrorCode.ERROR_INTERNAL));
                return;
            }
        }
        if (mBannerSpot == null) {
            mBannerSpot = InneractiveAdSpotManager.get().createSpot();
        }
        loadAd();
    }

    private void loadAd() {
        ZplayDebug.d(TAG, "loadAd");
        if (mBannerSpot != null && bannerSize == AdSize.BANNER_SIZE_SMART) {
            ZplayDebug.d(TAG, "not support smart banner:");
            layerPreparedFailed(recodeError(LayerErrorCode.ERROR_INTERNAL, "not support smart banner."));
            return;
        }
        InneractiveAdViewUnitController controller = new InneractiveAdViewUnitController();
        mBannerSpot.addUnitController(controller);

        InneractiveAdRequest request = new InneractiveAdRequest(getProvider().getKey2());

        mBannerSpot.setRequestListener(requestListener);

        mBannerSpot.requestAd(request);
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
                if (adSpot != mBannerSpot) {
                    return;
                }

                container = new FrameLayout(getActivity());
                InneractiveAdViewUnitController controller = (InneractiveAdViewUnitController) mBannerSpot.getSelectedUnitController();
                controller.setEventsListener(new InneractiveAdViewEventsListener() {
                    @Override
                    public void onAdImpression(InneractiveAdSpot adSpot) {
                        ZplayDebug.d(TAG, "onAdImpression");
                    }

                    @Override
                    public void onAdClicked(InneractiveAdSpot adSpot) {
                        ZplayDebug.d(TAG, "onAdClicked");
                        layerClicked(-99f, -99f);
                    }

                    @Override
                    public void onAdWillCloseInternalBrowser(InneractiveAdSpot adSpot) {
                        ZplayDebug.d(TAG, "onAdWillCloseInternalBrowser");
                    }

                    @Override
                    public void onAdWillOpenExternalApp(InneractiveAdSpot adSpot) {
                        ZplayDebug.d(TAG, "onAdWillOpenExternalApp");
                    }

                    @Override
                    public void onAdEnteredErrorState(InneractiveAdSpot inneractiveAdSpot, InneractiveUnitController.AdDisplayError adDisplayError) {
                        ZplayDebug.d(TAG, "onAdEnteredErrorState");
                    }

                    @Override
                    public void onAdExpanded(InneractiveAdSpot adSpot) {
                        ZplayDebug.d(TAG, "onAdExpanded");
                    }

                    @Override
                    public void onAdResized(InneractiveAdSpot adSpot) {
                        ZplayDebug.d(TAG, "onAdResized");
                    }

                    @Override
                    public void onAdCollapsed(InneractiveAdSpot adSpot) {
                        ZplayDebug.d(TAG, "onAdCollapsed");
                    }
                });

                controller.bindView(container);
                ZplayDebug.d(TAG, "onInneractiveSuccessfulAdRequest");
                layerPrepared(container, true);
            }

            @Override
            public void onInneractiveFailedAdRequest(InneractiveAdSpot adSpot, InneractiveErrorCode errorCode) {
                ZplayDebug.d(TAG, "onInneractiveFailedAdRequest: " + errorCode);
                layerPreparedFailed(recodeError(errorCode));
            }
        };
    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }

    private final int dip2px(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return ((int) (dp * scale + 0.5f));
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
