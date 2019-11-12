package com.yumi.android.sdk.ads.adapter.playableads;

import android.app.Activity;

import com.playableads.AtmosplayAdsBanner;
import com.playableads.BannerListener;
import com.playableads.entity.BannerSize;
import com.playableads.presenter.widget.AtmosBannerView;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.playableads.entity.BannerSize.SMART_BANNER;
import static com.yumi.android.sdk.ads.adapter.playableads.PlayableAdsUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.playableads.PlayableAdsUtil.sdkVersion;
import static com.yumi.android.sdk.ads.adapter.playableads.PlayableAdsUtil.updateGDPRStatus;

/**
 * Created by Administrator on 2017/3/23.
 */

public class PlayableadsBannerAdapter extends YumiCustomerBannerAdapter {

    private static final String TAG = "AdmobBannerAdapter";
    private static final float DEFAULT_CLICK_CX = -99f;
    private static final float DEFAULT_CLICK_CY = -99f;
    private AtmosplayAdsBanner mBanner;

    protected PlayableadsBannerAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }


    @Override
    protected void onPrepareBannerLayer() {
        final String appId = getProvider().getKey1();
        final String unitId = getProvider().getKey2();
        ZplayDebug.d(TAG, "onPrepareBannerLayer: " + appId + ", unitId: " + unitId);
        updateGDPRStatus();
        mBanner = new AtmosplayAdsBanner(getActivity(), appId, unitId);
        mBanner.setBannerSize(getBannerSize());
        mBanner.setBannerListener(new BannerListener() {
            @Override
            public void onBannerPrepared(AtmosBannerView view) {
                ZplayDebug.d(TAG, "onBannerPrepared: " + view);
                layerPrepared(view, true);
            }

            @Override
            public void onBannerPreparedFailed(int code, String error) {
                ZplayDebug.d(TAG, "onBannerPreparedFailed: " + code + ", errorMsg: " + error);
                layerPreparedFailed(recodeError(code, error));
            }

            @Override
            public void onBannerClicked() {
                ZplayDebug.d(TAG, "onBannerClicked: ");
                layerClicked(DEFAULT_CLICK_CX, DEFAULT_CLICK_CY);
            }
        });
        mBanner.loadAd();
    }

    @Override
    protected void init() {
    }

    private BannerSize getBannerSize() {
        switch (bannerSize) {
            case BANNER_SIZE_SMART:
                return SMART_BANNER;
            case BANNER_SIZE_728X90:
                return BannerSize.BANNER_728x90;
            default:
                return BannerSize.BANNER_320x50;
        }
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }

    @Override
    public void onActivityPause() {
    }

    @Override
    public void onActivityResume() {
    }

    @Override
    protected final void onDestroy() {
        if (mBanner != null) {
            mBanner.destroy();
        }
    }
}