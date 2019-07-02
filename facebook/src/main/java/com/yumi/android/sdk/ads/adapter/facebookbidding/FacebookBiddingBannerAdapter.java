package com.yumi.android.sdk.ads.adapter.facebookbidding;

import android.app.Activity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.BidderTokenProvider;
import com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.facebook.ads.AdError.NO_FILL;
import static com.yumi.android.sdk.ads.adapter.facebook.FacebookUtil.recodeError;
import static com.yumi.android.sdk.ads.publish.enumbean.AdSize.BANNER_SIZE_SMART;

public class FacebookBiddingBannerAdapter extends YumiCustomerBannerAdapter {

    private static final String TAG = "FacebookBiddingBannerAdapter";
    private AdView banner;
    private AdListener bannerListener;

    protected FacebookBiddingBannerAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void onActivityPause() {
    }

    @Override
    public void onActivityResume() {
    }

    @Override
    protected final void callOnActivityDestroy() {
        if (banner != null) {
            banner.destroy();
        }
    }

    @Override
    protected void onPrepareBannerLayer() {
        try {
            if (bannerSize == BANNER_SIZE_SMART) {
                ZplayDebug.d(TAG, "facebook not support smart banner", onoff);
                layerPreparedFailed(FacebookUtil.recodeError(NO_FILL, "not support smart banner."));
                return;
            }
            ZplayDebug.d(TAG, "facebookbid request new banner", onoff);
            if (getProvider().getErrCode() != 200) {
                layerPreparedFailed(recodeError(null, getProvider().getErrMessage()), getProvider().getErrMessage());
                return;
            }
            banner = new AdView(getContext(), getProvider().getKey1(), AdSize.BANNER_HEIGHT_50);
            banner.setAdListener(bannerListener);
            banner.loadAdFromBid(getProvider().getPayload());
        } catch (Exception e) {
            ZplayDebug.e(TAG, "facebook bidding banner onPrepareBanner error", e, onoff);
        }
    }


    @Override
    protected void init() {
        ZplayDebug.i(TAG, "placementID : " + getProvider().getKey1() + ",payload : " + getProvider().getPayload(), onoff);
        createBannerListener();
    }

    private void createBannerListener() {
        if (bannerListener == null) {
            bannerListener = new AdListener() {

                @Override
                public void onError(Ad arg0, AdError arg1) {
                    ZplayDebug.d(TAG, "facebookbid banner failed " + arg1.getErrorMessage(), onoff);
                    layerPreparedFailed(recodeError(arg1), arg1.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad arg0) {
                    ZplayDebug.d(TAG, "facebookbid banner prepared", onoff);
                    layerPrepared(banner, true);
                }

                @Override
                public void onAdClicked(Ad arg0) {
                    ZplayDebug.d(TAG, "facebookbid banner clicked", onoff);
                    layerClicked(-99f, -99f);
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };
        }
    }


    private AdSize calculateBannerSize() {
        if (bannerSize == com.yumi.android.sdk.ads.publish.enumbean.AdSize.BANNER_SIZE_320X50) {
            if (isMatchWindowWidth) {
                return AdSize.BANNER_HEIGHT_50;
            }
            return AdSize.BANNER_320_50;
        }
        if (bannerSize == com.yumi.android.sdk.ads.publish.enumbean.AdSize.BANNER_SIZE_728X90) {
            return AdSize.BANNER_HEIGHT_90;
        }
        return AdSize.BANNER_320_50;
    }


    public String getBidderToken() {
        return BidderTokenProvider.getBidderToken(getContext());
    }
}