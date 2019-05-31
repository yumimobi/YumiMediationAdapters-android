package com.yumi.android.sdk.ads.adapter.gdtmob;

import android.app.Activity;
import android.util.Log;

import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerSplashAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Description:
 * <p>
 * Created by lgd on 2019-05-30.
 */
public class GdtmobSplashAdapter extends YumiCustomerSplashAdapter {
    private static final String TAG = "GdtmobSplashAdapter";

    public GdtmobSplashAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    protected void onPrepareSplashLayer() {
        new SplashAD(getActivity(), getDeveloperCntainer(), null, getProvider().getKey1(), getProvider().getKey2(), new SplashADListener() {
            @Override
            public void onADDismissed() {
                layerClosed();
            }

            @Override
            public void onNoAD(AdError adError) {
                Log.d(TAG, "onNoAD: " + adError.getErrorMsg());
                layerExposureFailed(new com.yumi.android.sdk.ads.publish.AdError(LayerErrorCode.ERROR_NO_FILL, "GDT: " + adError.getErrorMsg()));
            }

            @Override
            public void onADPresent() {
                layerExposure();
            }

            @Override
            public void onADClicked() {
                layerClicked(0, 0);
            }

            @Override
            public void onADTick(long l) {
                Log.d(TAG, "onADTick: " + l);
            }

            @Override
            public void onADExposure() {
                layerExposure();
            }
        }, getProvider().getOutTime() * 1000);
    }
}
