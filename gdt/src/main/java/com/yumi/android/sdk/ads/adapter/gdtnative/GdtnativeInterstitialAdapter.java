package com.yumi.android.sdk.ads.adapter.gdtnative;


import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.nativead.YumiNativeExpressIntersititalAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.List;

import static com.yumi.android.sdk.ads.adapter.GdtUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.GdtUtil.sdkVersion;

public class GdtnativeInterstitialAdapter extends YumiNativeExpressIntersititalAdapter {
    private static final String TAG = "GdtnativeInterstitialAdapter";
    private static int width;
    private static int height;
    private NativeExpressAD nativeExpressAD;
    private NativeExpressADView nativeExpressADView;
    private Activity mActivity;

    protected GdtnativeInterstitialAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);
        mActivity = activity;
    }

    private static final boolean isPortrait(Context context) {
        try {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            float density = dm.density;
            width = (int) (dm.widthPixels / density);
            height = (int) (dm.heightPixels / density);
            if (dm.widthPixels <= dm.heightPixels) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    protected void onPreparedNativeInterstitial() {
        ZplayDebug.d(TAG, "init appId : " + getProvider().getKey1() + " ,pId : " + getProvider().getKey2());
        nativeExpressAD = new NativeExpressAD(getActivity(), calculateInterstitialSize(),
                getProvider().getKey1(), getProvider().getKey2(), new MyNativeExpressADListener());
        ZplayDebug.d(TAG, "load new interstitial");
        nativeExpressAD.loadAD(1);
    }

    @Override
    protected void NativeLayerPrepared(View view) {
        ZplayDebug.d(TAG, "NativeLayerPrepared");
        layerPrepared();
    }

    @Override
    protected void NativeLayerOnShow() {
        ZplayDebug.d(TAG, "NativeLayerOnShow");
    }

    @Override
    protected void calculateRequestSize() {
        ZplayDebug.d(TAG, "calculateRequestSize");
    }

    @Override
    protected void NativeLayerDismiss() {
        ZplayDebug.d(TAG, "NativeLayerDismiss");
        layerClosed();
        if (nativeExpressADView != null) {
            nativeExpressADView.destroy();
        }
    }

    @Override
    protected void init() {

    }

    @Override
    protected void onDestroy() {
        ZplayDebug.d(TAG, "callOnActivityDestroy");
        if (nativeExpressADView != null) {
            nativeExpressADView.destroy();
        }
    }

    @Override
    public void onActivityPause() {

    }

    private ADSize calculateInterstitialSize() {
        if (isPortrait(mActivity)) {
            return new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT);
        } else {
            int adWeight = width * 2 / 3;
            int adHeight = adWeight * 950 / 1230;
            if (adHeight > height) {
                adHeight = height;
            }
            return new ADSize(adWeight, adHeight);
        }
    }

    @Override
    public void onActivityResume() {
        closeOnResume();
    }

    @Override
    public boolean onActivityBackPressed() {
        return false;
    }

    private class MyNativeExpressADListener implements NativeExpressAD.NativeExpressADListener {

        @Override
        public void onNoAD(AdError adError) {
            if (adError == null) {
                ZplayDebug.d(TAG, "onNoAD adError = null");
                layerPreparedFailed(recodeError(null));
                return;
            }
            ZplayDebug.d(TAG, "onNoAD ErrorCode:" + adError.getErrorCode() + " msg:" + adError.getErrorMsg());
            layerPreparedFailed(recodeError(adError));
        }

        @Override
        public void onADLoaded(List<NativeExpressADView> list) {
            ZplayDebug.d(TAG, "onADLoaded" + list.size());
            if (list.size() > 0) {
                nativeExpressADView = list.get(0);
                nativeExpressADView.render();
            } else {
                layerPreparedFailed(recodeError(null));
            }
        }

        @Override
        public void onRenderFail(NativeExpressADView nativeExpressADView) {
            ZplayDebug.d(TAG, "onRenderFail");
            layerPreparedFailed(recodeError(null));
        }

        @Override
        public void onRenderSuccess(NativeExpressADView adView) {
            ZplayDebug.d(TAG, "onRenderSuccess" + adView.getHeight() + "," + adView.getWidth());
            loadData(adView, false);
        }

        @Override
        public void onADExposure(NativeExpressADView nativeExpressADView) {
            ZplayDebug.d(TAG, "onADExposure");
            layerExposure();
        }

        @Override
        public void onADClicked(NativeExpressADView nativeExpressADView) {
            ZplayDebug.d(TAG, "onADClicked");
            layerClicked(-99f, -99f);
        }

        @Override
        public void onADClosed(NativeExpressADView nativeExpressADView) {
            ZplayDebug.d(TAG, "onADClosed");
            closeOnResume();
        }

        @Override
        public void onADLeftApplication(NativeExpressADView nativeExpressADView) {
            ZplayDebug.d(TAG, "onADLeftApplication");
        }

        @Override
        public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {
            ZplayDebug.d(TAG, "onADOpenOverlay");
        }

        @Override
        public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {
            ZplayDebug.d(TAG, "onADCloseOverlay");
        }
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}