package com.yumi.android.sdk.ads.adapter.baidu;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.baidu.mobads.AdView;
import com.baidu.mobads.InterstitialAd;
import com.baidu.mobads.InterstitialAdListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerInterstitialAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.baidu.BaiduUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.baidu.BaiduUtil.sdkVersion;

public class BaiduInterstitialAdapter extends YumiCustomerInterstitialAdapter {
    private static final String TAG = "BaiduInstertitialAdapter";
    private InterstitialAdListener instertitialListener;
    private InterstitialAd instertitial;
    private RelativeLayout parentLayout;
    private boolean isAdPresent = false;

    protected BaiduInterstitialAdapter(Activity activity,
                                       YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }

    @Override
    public boolean onActivityBackPressed() {
        return false;
    }

    @Override
    protected void onPrepareInterstitial() {
        ZplayDebug.d(TAG, "load new interstitial");
        if (isInterstitialAspectRatio(getProvider().getExtraData("interstitialAspectRatio"))) {
            int[] interstitialAdSize = getInterstitialAdSize();
            ZplayDebug.d(TAG, "AdSize，width : " + interstitialAdSize[0] + ",height: " + interstitialAdSize[1]);
            instertitial = BaiduExtra.getBaiduExtra().getBaiduInterstitialForVideoPausePlayAd(getActivity(), getProvider().getKey2(), instertitialListener);
            instertitial.loadAdForVideoApp(interstitialAdSize[0], interstitialAdSize[1]);
        } else {
            instertitial = BaiduExtra.getBaiduExtra().getBaiduInterstitialAd(getActivity(), getProvider().getKey2(), instertitialListener);
            instertitial.loadAd();
        }

    }

    @Override
    protected void onShowInterstitialLayer(Activity activity) {
        if (!isInterstitialLayerReady()) {
            return;
        }
        if (isInterstitialAspectRatio(getProvider().getExtraData("interstitialAspectRatio"))) {
            int[] interstitialAdSize = getInterstitialAdSize();
            parentLayout = new RelativeLayout(getContext());
            LayoutParams parentParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            parentLayout.setClickable(true);
            getActivity().addContentView(parentLayout, parentParams);
            final RelativeLayout adView = new RelativeLayout(getContext());
            LayoutParams adViewParams = new RelativeLayout.LayoutParams(interstitialAdSize[0], interstitialAdSize[1]);
            adViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            parentLayout.setBackgroundColor(Color.BLACK);
            parentLayout.addView(adView, adViewParams);
            instertitial.showAdInParentForVideoApp(getActivity(), adView);
        } else {
            instertitial.showAd(activity);
        }
    }

    @Override
    protected boolean isInterstitialLayerReady() {
        if (instertitial != null && instertitial.isAdReady()) {
            return true;
        }
        return false;
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "init appSid : " + getProvider().getKey1() + " ,adPlaceID : " + getProvider().getKey2());
        createListener();
        AdView.setAppSid(getContext(), getProvider().getKey1());
    }

    private void createListener() {
        if (instertitialListener == null) {
            instertitialListener = new InterstitialAdListener() {

                @Override
                public void onAdReady() {
                    ZplayDebug.d(TAG, "onAdReady");
                    layerPrepared();
                }

                @Override
                public void onAdPresent() {
                    ZplayDebug.d(TAG, "onAdPresent");
                    isAdPresent = true;
                    layerExposure();
                }

                @Override
                public void onAdFailed(String arg0) {
                    ZplayDebug.d(TAG, "onAdFailed " + arg0);
                    layerPreparedFailed(recodeError(arg0));
                }

                @Override
                public void onAdDismissed() {
                    ZplayDebug.d(TAG, "onAdDismissed");
                    removeInterstitialView();
                    if(isAdPresent){
                        layerClosed();
                    }
                    isAdPresent = false;
                }

                @Override
                public void onAdClick(InterstitialAd arg0) {
                    ZplayDebug.d(TAG, "onAdClick");
                    layerClicked(-99f, -99f);
                }
            };
        }
    }

    private int[] getInterstitialAdSize() {
        try {
            DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;

            int adWidth = Math.min(width, height);

            return new int[]{adWidth, (int) (adWidth / Double.valueOf(getProvider().getExtraData("interstitialAspectRatio")))};
        } catch (Exception e) {
            ZplayDebug.e(TAG, "getInterstitialAdSize error: " + e);

        }
        return new int[]{0, 0};
    }

    private boolean isInterstitialAspectRatio(String aspectRatio) {
        try {
            if (TextUtils.equals("0", aspectRatio.trim())) {
                return false;
            }
            Double.valueOf(aspectRatio);
            return true;
        } catch (Exception e) {
            ZplayDebug.e(TAG, "isDoubleOrFloat error: " + e);
        }
        return false;
    }

    private void removeInterstitialView() {
        try {
            if (parentLayout != null && parentLayout.getParent() instanceof ViewGroup) {
                ((ViewGroup) parentLayout.getParent()).removeView(parentLayout);
                parentLayout = null;
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "removeInterstitialView error: " + e);

        }
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
