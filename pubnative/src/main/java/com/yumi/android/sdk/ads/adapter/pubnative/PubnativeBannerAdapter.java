package com.yumi.android.sdk.ads.adapter.pubnative;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import net.pubnative.lite.sdk.views.PNAdView;
import net.pubnative.lite.sdk.views.PNBannerAdView;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.yumi.android.sdk.ads.adapter.pubnative.PubNativeUtil.dp2px;
import static com.yumi.android.sdk.ads.adapter.pubnative.PubNativeUtil.initPubNativeSDK;
import static com.yumi.android.sdk.ads.adapter.pubnative.PubNativeUtil.isTablet;
import static com.yumi.android.sdk.ads.adapter.pubnative.PubNativeUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.pubnative.PubNativeUtil.updateGDPRStatus;
import static com.yumi.android.sdk.ads.publish.enumbean.AdSize.BANNER_SIZE_SMART;

public class PubnativeBannerAdapter extends YumiCustomerBannerAdapter {
    private String TAG = "PubnativeBannerAdapter";
    private FrameLayout mActivityContent;
    private FrameLayout mBannerContainer;
    private PNBannerAdView mBanner;
    private PNAdView.Listener mBannerListener;

    protected PubnativeBannerAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    protected void onPrepareBannerLayer() {
        ZplayDebug.i(TAG, "pubnative request new banner key2:" + getProvider().getKey2(), onoff);
        if (bannerSize == BANNER_SIZE_SMART) {
            ZplayDebug.i(TAG, "pubnative not support smart banner", onoff);
            layerPreparedFailed(recodeError("not support smart banner"));
            return;
        }
        mBanner = new PNBannerAdView(getContext());
        int[] wh = getWH();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(wh[0], wh[1]);
        mBanner.setLayoutParams(params);

        removeTempViews();
        mActivityContent = newActivityContentView();
        mBannerContainer = newFrameLayout();
        mActivityContent.addView(mBannerContainer);
        mBannerContainer.addView(mBanner);

        mBanner.load(getProvider().getKey2(), mBannerListener);
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "pubnative banner init key1:" + getProvider().getKey1(), onoff);
        initPubNativeSDK(getProvider().getKey1(), getActivity());
        updateGDPRStatus();
        createBannerListener();
    }

    private void createBannerListener() {
        mBannerListener = new PNAdView.Listener() {
            @Override
            public void onAdLoaded() {
                ZplayDebug.i(TAG, "pubnative banner onAdLoaded", onoff);
                removeTempViews();
                layerPrepared(mBanner, true);
            }

            @Override
            public void onAdLoadFailed(Throwable throwable) {
                ZplayDebug.i(TAG, "pubnative banner onAdLoadFailed", onoff);
                layerPreparedFailed(recodeError(throwable.toString()));
            }

            @Override
            public void onAdImpression() {
                ZplayDebug.i(TAG, "pubnative banner onAdImpression", onoff);
            }

            @Override
            public void onAdClick() {
                ZplayDebug.i(TAG, "pubnative banner onAdClick", onoff);
                layerClicked(-999f, -999f);
            }
        };
    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }

    @Override
    protected void onDestroy() {
//        if (mBanner != null) {
//            mBanner.destroy();
//        }
    }

    private FrameLayout newActivityContentView() {
        FrameLayout result = new FrameLayout(getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        getActivity().addContentView(result, params);
        return result;
    }

    private FrameLayout newFrameLayout() {
        int[] wh = getWH();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(wh[0], wh[1]);
        params.gravity = Gravity.BOTTOM;
        FrameLayout result = new FrameLayout(getActivity());
        result.setLayoutParams(params);
        return result;
    }

    private void removeTempViews() {
        removeView(mActivityContent);
        removeView(mBannerContainer);
    }

    private void removeView(View view) {
        if (view != null && view.getParent() instanceof ViewGroup) {
            if (view.getParent() instanceof ViewGroup) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
        }
    }

    private int[] getWH() {
        switch (bannerSize) {
            case BANNER_SIZE_728X90:
                return new int[]{dp2px(720), dp2px(720f / 20 * 3)};
            case BANNER_SIZE_AUTO:
                if (isTablet()) {
                    return new int[]{dp2px(720), dp2px(720f / 20 * 3)};
                }
            default:
                return new int[]{dp2px(320), dp2px(50)};
        }

    }
}
