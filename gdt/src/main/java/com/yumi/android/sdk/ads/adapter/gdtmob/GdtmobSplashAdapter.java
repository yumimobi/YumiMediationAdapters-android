package com.yumi.android.sdk.ads.adapter.gdtmob;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerSplashAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import static com.yumi.android.sdk.ads.adapter.GdtUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.GdtUtil.sdkVersion;

/**
 * Description:
 * <p>
 * Created by lgd on 2019-05-30.
 */
public class GdtmobSplashAdapter extends YumiCustomerSplashAdapter {
    private static final String TAG = "GdtmobSplashAdapter";
    private static final int WHAT_TIMEOUT = 0;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            layerTimeout();
        }
    };

    public GdtmobSplashAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    protected void onPrepareSplashLayer() {
        mHandler.sendEmptyMessageDelayed(WHAT_TIMEOUT, getProvider().getOutTime() * 1000);
        SplashAD splashAD = new SplashAD(getActivity(), null, getProvider().getKey1(), getProvider().getKey2(), new SplashADListener() {
            @Override
            public void onADDismissed() {
                layerClosed();
            }

            @Override
            public void onNoAD(AdError adError) {
                ZplayDebug.d(TAG, "onNoAD: " + adError.getErrorMsg());
                mHandler.removeMessages(WHAT_TIMEOUT);
                layerPreparedFailed(recodeError(adError));
            }

            @Override
            public void onADPresent() {
                ZplayDebug.d(TAG, "onADPresent");
                mHandler.removeMessages(WHAT_TIMEOUT);
                layerExposure();
            }

            @Override
            public void onADClicked() {
                ZplayDebug.d(TAG, "onADClicked");
                layerClicked(0, 0);
            }

            @Override
            public void onADTick(long l) {
                ZplayDebug.d(TAG, "onADTick: " + l);
            }

            @Override
            public void onADExposure() {
                // GDT Demo 中没有这个方法，而且测试发现，总是先触发 onADPresent 然后再触发此方法，所以忽略这个方法
            }
        }, getProvider().getOutTime() * 1000);
        splashAD.fetchAndShowIn(getDeveloperContainer());
    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
