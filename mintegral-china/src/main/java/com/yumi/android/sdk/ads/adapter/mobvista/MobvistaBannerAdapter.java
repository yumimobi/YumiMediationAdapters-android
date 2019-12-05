package com.yumi.android.sdk.ads.adapter.mobvista;

import android.app.Activity;
import android.widget.FrameLayout;

import com.mintegral.msdk.out.BannerAdListener;
import com.mintegral.msdk.out.BannerSize;
import com.mintegral.msdk.out.MTGBannerView;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.mobvista.Util.sdkVersion;
import static com.yumi.android.sdk.ads.publish.enumbean.AdSize.BANNER_SIZE_SMART;

public class MobvistaBannerAdapter extends YumiCustomerBannerAdapter {
    private static final String TAG = "MobvistaBannerAdapter";
    private MTGBannerView mBannerView;
    private BannerAdListener mBannerAdListener;

    protected MobvistaBannerAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    protected void onPrepareBannerLayer() {
        if (bannerSize == BANNER_SIZE_SMART) {
            ZplayDebug.i(TAG, "not support smart banner");
            AdError error = new AdError(LayerErrorCode.ERROR_NO_FILL);
            error.setErrorMessage("minteral errorMsg: " + "not support smart banner");
            layerPreparedFailed(error);
            return;
        }

        if (mBannerView == null) {
            initBanner();
        }
        //mtgBannerView.setAllowShowCloseBtn(false);
        mBannerView.setRefreshTime(getProvider().getAutoRefreshInterval());
        mBannerView.setBannerAdListener(mBannerAdListener);

        mBannerView.load();
    }

    @Override
    protected void init() {
        try {
            final String appId = getProvider().getKey1();
            final String appKey = getProvider().getKey2();
            ZplayDebug.d(TAG, "init: appId: " + appId + ", appKey: " + appKey);
            Util.initSDK(getContext(), appId, appKey);
        } catch (Exception e) {
            ZplayDebug.e(TAG, "init: exception", e);
        }

    }

    private void initBanner() {
        try {
            mBannerView = new MTGBannerView(getContext());
            mBannerView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            mBannerView.init(calculateBannerSize(), getProvider().getKey3());

            mBannerAdListener = new BannerAdListener() {
                @Override
                public void onLoadFailed(String msg) {
                    ZplayDebug.e(TAG, "onLoadFailed" + msg);
                    AdError error = new AdError(LayerErrorCode.ERROR_NO_FILL);
                    error.setErrorMessage("minteral errorMsg: " + msg);
                    layerPreparedFailed(error);
                }

                @Override
                public void onLoadSuccessed() {
                    ZplayDebug.d(TAG, "onLoadSuccessed");
                    layerPrepared(mBannerView, true);
                }


                @Override
                public void onClick() {
                    ZplayDebug.d(TAG, "onClick");
                    layerClicked(-999f, -999f);
                }

                @Override
                public void onLeaveApp() {
                    ZplayDebug.d(TAG, "onLeaveApp");
                }

                @Override
                public void showFullScreen() {
                    ZplayDebug.d(TAG, "showFullScreen");
                }

                @Override
                public void closeFullScreen() {
                    ZplayDebug.d(TAG, "closeFullScreen");
                    layerClosed();
                }

                @Override
                public void onLogImpression() {
                    ZplayDebug.d(TAG, "onLogImpression");
                }
            };
        } catch (Exception e) {
            ZplayDebug.e(TAG, "init: banner error", e);
        }
    }

    private BannerSize calculateBannerSize() {
        switch (bannerSize) {
            case BANNER_SIZE_728X90:
                return new BannerSize(BannerSize.SMART_TYPE, 728, 90);
            default:
                return new BannerSize(BannerSize.STANDARD_TYPE, 320, 50);
        }
    }


    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }

    @Override
    protected final void onDestroy() {
        if (mBannerView != null) {
//            mBannerView.release();
            mBannerView = null;
            mBannerAdListener = null;
        }
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
