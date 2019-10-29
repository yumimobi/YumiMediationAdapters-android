package com.yumi.android.sdk.ads.adapter.inmobi;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiBanner;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.AdSize;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.Map;

import static com.yumi.android.sdk.ads.adapter.inmobi.InmobUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.inmobi.InmobUtil.sdkVersion;
import static com.yumi.android.sdk.ads.publish.enumbean.AdSize.BANNER_SIZE_SMART;
import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_NO_FILL;
import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_OVER_RETRY_LIMIT;

public class InmobiBannerAdapter extends YumiCustomerBannerAdapter {

    private static final String TAG = "InmobiBannerAdapter";
    private InMobiBanner banner;
    private InMobiBanner.BannerAdListener bannerListener;
    private FrameLayout container;
    private int bannerHeight;
    private int bannerWidth;

    protected InmobiBannerAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void onActivityPause() {
    }

    @Override
    public void onActivityResume() {
    }

    @Override
    protected final void onDestroy() {
        InmobiExtraHolder.onDestroy();
    }

    @Override
    protected void onPrepareBannerLayer() {
        if (bannerSize == BANNER_SIZE_SMART) {
            ZplayDebug.d(TAG, "not support smart banner");
            layerPreparedFailed(recodeError(ERROR_NO_FILL, "not support smart banner."));
            return;
        }
        calculateBannerSize();
        ZplayDebug.d(TAG, "load new banner");
        String key2 = getProvider().getKey2();
        long placementID = 0L;
        if (key2 != null && key2.length() > 0) {
            try {
                placementID = Long.valueOf(key2);
            } catch (NumberFormatException e) {
                ZplayDebug.e(TAG, "", e);
                layerPreparedFailed(recodeError(ERROR_OVER_RETRY_LIMIT, "inmobi key2 error"));
                return;
            }
        } else {
            layerPreparedFailed(recodeError(ERROR_OVER_RETRY_LIMIT, "inmobi key2 error"));
            return;
        }
        container = new FrameLayout(getActivity());

        banner = new InMobiBanner(getActivity(), placementID);
        banner.setListener(bannerListener);
        if (getProvider().getAutoRefreshInterval() > 0) {
            banner.setEnableAutoRefresh(true);
            banner.setRefreshInterval(getProvider().getAutoRefreshInterval());
        } else {
            banner.setEnableAutoRefresh(false);
        }
        container.addView(banner, new FrameLayout.LayoutParams(bannerWidth, bannerHeight, Gravity.CENTER));
        banner.load();
    }

    private void calculateBannerSize() {
        if (isMatchWindowWidth && calculateLayerSize != null) {
            if (calculateLayerSize[0] > 0 && calculateLayerSize[1] > 0) {
                bannerWidth = calculateLayerSize[0];
                bannerHeight = calculateLayerSize[1];
            } else {
                if (bannerSize == AdSize.BANNER_SIZE_728X90) {
                    bannerWidth = 728;
                    bannerHeight = 90;
                } else {
                    bannerWidth = 320;
                    bannerHeight = 50;
                }
                bannerWidth = dip2px(getContext(), bannerWidth);
                bannerHeight = dip2px(getContext(), bannerHeight);
            }
        } else {
            if (bannerSize == AdSize.BANNER_SIZE_728X90) {
                bannerWidth = 728;
                bannerHeight = 90;
            } else {
                bannerWidth = 320;
                bannerHeight = 50;
            }
            bannerWidth = dip2px(getContext(), bannerWidth);
            bannerHeight = dip2px(getContext(), bannerHeight);
        }
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "init accounID : " + getProvider().getKey1() + " ,placementID : " + getProvider().getKey2());
        InmobiExtraHolder.initInmobiSDK(getActivity(), getProvider().getKey1());
        bannerListener = new InMobiBanner.BannerAdListener() {

            @Override
            public void onUserLeftApplication(InMobiBanner arg0) {
                ZplayDebug.d(TAG, "onUserLeftApplication");
                layerClicked(-99f, -99f);
            }

            @Override
            public void onAdRewardActionCompleted(InMobiBanner arg0,
                                                  Map<Object, Object> arg1) {
            }

            @Override
            public void onAdLoadSucceeded(InMobiBanner arg0) {
                ZplayDebug.d(TAG, "onAdLoadSucceeded");
                layerPrepared(container, true);
            }

            @Override
            public void onAdLoadFailed(InMobiBanner arg0, InMobiAdRequestStatus arg1) {
                ZplayDebug.d(TAG, "onAdLoadFailed" + arg1.getStatusCode());
                layerPreparedFailed(recodeError(arg1));
            }

            @Override
            public void onAdInteraction(InMobiBanner arg0, Map<Object, Object> arg1) {
            }

            @Override
            public void onAdDisplayed(InMobiBanner arg0) {
            }

            @Override
            public void onAdDismissed(InMobiBanner arg0) {
            }
        };

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
