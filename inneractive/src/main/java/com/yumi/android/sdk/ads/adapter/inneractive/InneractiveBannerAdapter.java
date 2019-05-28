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
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.inneractive.InneractiveUtil.recodeError;

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
        ZplayDebug.d(TAG, "inneractive request new banner", onoff);
        InneractiveAdViewUnitController controller = new InneractiveAdViewUnitController();
        mBannerSpot.addUnitController(controller);

        InneractiveAdRequest request = new InneractiveAdRequest(getProvider().getKey2());

        mBannerSpot.setRequestListener(requestListener);

        mBannerSpot.requestAd(request);
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "inneractive banner init key1: " + getProvider().getKey1() + "key2: " + getProvider().getKey2(), onoff);

        InneractiveAdManager.initialize(getActivity(), getProvider().getKey1());
        createListener();
        // initialize rectangle spot
        if (mBannerSpot != null) {
            mBannerSpot.destroy();
        }

        mBannerSpot = InneractiveAdSpotManager.get().createSpot();
    }

    private void createListener(){
        requestListener = new InneractiveAdSpot.RequestListener() {
            @Override
            public void onInneractiveSuccessfulAdRequest(InneractiveAdSpot adSpot) {
                if (adSpot != mBannerSpot) {
                    return;
                }

                container = new FrameLayout(getActivity());
                InneractiveAdViewUnitController controller = (InneractiveAdViewUnitController)mBannerSpot.getSelectedUnitController();
                controller.setEventsListener(new InneractiveAdViewEventsListener() {
                    @Override
                    public void onAdImpression(InneractiveAdSpot adSpot) {
                        ZplayDebug.d(TAG, "inneractive banner onAdImpression", onoff);
                    }

                    @Override
                    public void onAdClicked(InneractiveAdSpot adSpot) {
                        ZplayDebug.d(TAG, "inneractive banner onAdClicked", onoff);
                        layerClicked(-99f,-99f);
                    }

                    @Override
                    public void onAdWillCloseInternalBrowser(InneractiveAdSpot adSpot) {
                        ZplayDebug.d(TAG, "inneractive banner onAdWillCloseInternalBrowser", onoff);
                    }

                    @Override
                    public void onAdWillOpenExternalApp(InneractiveAdSpot adSpot) {
                        ZplayDebug.d(TAG, "inneractive banner onAdWillOpenExternalApp", onoff);
                    }

                    @Override
                    public void onAdEnteredErrorState(InneractiveAdSpot inneractiveAdSpot, InneractiveUnitController.AdDisplayError adDisplayError) {
                        ZplayDebug.d(TAG, "inneractive banner onAdEnteredErrorState", onoff);
                    }

                    @Override
                    public void onAdExpanded(InneractiveAdSpot adSpot) {
                        ZplayDebug.d(TAG, "inneractive banner onAdExpanded", onoff);
                    }

                    @Override
                    public void onAdResized(InneractiveAdSpot adSpot) {
                        ZplayDebug.d(TAG, "inneractive banner onAdResized", onoff);
                    }

                    @Override
                    public void onAdCollapsed(InneractiveAdSpot adSpot) {
                        ZplayDebug.d(TAG, "inneractive banner onAdCollapsed", onoff);
                    }
                });

                controller.bindView(container);
                ZplayDebug.d(TAG, "inneractive banner load successed", onoff);
                layerPrepared(container, true);
            }

            @Override
            public void onInneractiveFailedAdRequest(InneractiveAdSpot adSpot, InneractiveErrorCode errorCode) {
                ZplayDebug.d(TAG, "inneractive banner load Failed: " + errorCode.toString(), onoff);
                layerPreparedFailed(recodeError(errorCode));
            }
        };
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

    private final int dip2px(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return ((int) (dp * scale + 0.5f));
    }
}
