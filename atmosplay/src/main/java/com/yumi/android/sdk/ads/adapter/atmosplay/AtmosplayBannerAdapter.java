package com.yumi.android.sdk.ads.adapter.atmosplay;

import android.app.Activity;

import com.atmosplayads.AtmosplayBanner;
import com.atmosplayads.entity.BannerSize;
import com.atmosplayads.listener.BannerListener;
import com.atmosplayads.presenter.widget.AtmosBannerView;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.atmosplayads.entity.BannerSize.SMART_BANNER;
import static com.yumi.android.sdk.ads.adapter.atmosplay.AtmosplayAdsUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.atmosplay.AtmosplayAdsUtil.sdkVersion;
import static com.yumi.android.sdk.ads.adapter.atmosplay.AtmosplayAdsUtil.updateGDPRStatus;

/**
 * Created by Administrator on 2017/3/23.
 */

public class AtmosplayBannerAdapter extends YumiCustomerBannerAdapter {

    private static final String TAG = "AtmosplayBannerAdapter";
    private static final float DEFAULT_CLICK_CX = -99f;
    private static final float DEFAULT_CLICK_CY = -99f;
    private AtmosplayBanner mAtmosplayBanner;

    protected AtmosplayBannerAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }


    @Override
    protected void onPrepareBannerLayer() {
        final String appId = getProvider().getKey1();
        final String unitId = getProvider().getKey2();
        ZplayDebug.d(TAG, "onPrepareBannerLayer: " + appId + ", unitId: " + unitId);
        updateGDPRStatus();
        mAtmosplayBanner = new AtmosplayBanner(getActivity(), appId, unitId);
        mAtmosplayBanner.setBannerSize(getBannerSize());
        mAtmosplayBanner.setBannerListener(new BannerListener() {
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
        mAtmosplayBanner.loadAd();
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
        if (mAtmosplayBanner != null) {
            mAtmosplayBanner.destroy();
        }
    }
}