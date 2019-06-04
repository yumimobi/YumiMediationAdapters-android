package com.yumi.android.sdk.ads.adapter.baidu;

import android.app.Activity;
import android.util.Log;

import com.baidu.mobads.AdView;
import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashLpCloseListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerSplashAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

/**
 * Description:
 * <p>
 * Created by lgd on 2019-05-30.
 */
public class BaiduSplashAdapter extends YumiCustomerSplashAdapter {
    private static final String TAG = "BaiduSplashAdapter";

    public BaiduSplashAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    protected void onPrepareSplashLayer() {
        Log.d(TAG, "onPrepareSplashLayer: ");
        SplashLpCloseListener listener = new SplashLpCloseListener() {
            @Override
            public void onLpClosed() {
                // 测试发现，广告关闭后不会触发这个方法
            }

            @Override
            public void onAdDismissed() {
                layerClosed();
            }

            @Override
            public void onAdFailed(String arg0) {
                ZplayDebug.e(TAG, "Baidu Splash ad failed: " + arg0);
                layerPreparedFailed(new AdError(LayerErrorCode.ERROR_NO_FILL, "baidu: " + arg0));
            }

            @Override
            public void onAdPresent() {
                ZplayDebug.i(TAG, "onAdPresent");
                layerExposure();
            }

            @Override
            public void onAdClick() {
                // 测试发现，点击广告不会触发这个方法
                layerClicked(-99f, -99f);
            }
        };
        AdView.setAppSid(getActivity(),getProvider().getKey1());
        // canClick参数表示是否接受点击类型的⼴广告，强烈建议设置为 true，否则影响广告填充
        new SplashAd(getActivity(), getDeveloperCntainer(), listener, getProvider().getKey2(), true);
    }
}
