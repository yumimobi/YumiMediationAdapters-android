package com.yumi.android.sdk.ads.adapter.bytedance;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewTreeObserver;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerSplashAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;

import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.getAppName;
import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.recodeError;

/**
 * Description:
 * <p>
 * Created by lgd on 2019-05-31.
 */
public class BytedanceSplashAdapter extends YumiCustomerSplashAdapter {
    private static final String TAG = "BytedanceSplashAdapter";
    private static final int WHAT_TIMEOUT = 0;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            layerTimeout();
        }
    };

    private TTAdNative mTTAdNative;
    private ViewTreeObserver.OnWindowFocusChangeListener mLayoutListener;
    private boolean isHitCloseCallback;

    public BytedanceSplashAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mLayoutListener = new ViewTreeObserver.OnWindowFocusChangeListener() {
                @Override
                public void onWindowFocusChanged(boolean hasFocus) {
                    if (!hasFocus) {
                        hitCloseCallback();
                    }
                }
            };
        }
    }

    @Override
    protected void init() {
        TTAdSdk.init(getActivity(),
                new TTAdConfig.Builder()
                        .appId(getProvider().getKey1())
                        .useTextureView(false)
                        .appName(getAppName(getActivity().getPackageManager(), getActivity().getPackageName()))
                        .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                        .allowShowNotify(false)
                        .allowShowPageWhenScreenLock(false)
                        .debug(false)
                        .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_3G)
                        .supportMultiProcess(false)
                        .build());
    }

    @Override
    protected void onPrepareSplashLayer() {
        //step2:创建TTAdNative对象
        mTTAdNative = TTAdSdk.getAdManager().createAdNative(getActivity());
        //加载开屏广告
        loadSplashAd();
    }

    private void loadSplashAd() {
        mHandler.sendEmptyMessageDelayed(WHAT_TIMEOUT, getProvider().getOutTime() * 1000);

        int[] realSize = WindowSizeUtils.getRealSize();
        //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(getProvider().getKey2())
                .setImageAcceptedSize(realSize[0], realSize[1])
                .setSupportDeepLink(true)
                .build();
        //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
        mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
            @Override
            public void onError(int code, String message) {
                ZplayDebug.d(TAG, "onError: " + message);
                mHandler.removeMessages(WHAT_TIMEOUT);
                layerPreparedFailed(recodeError(code, message));
                removeSplashViews();
            }

            @Override
            public void onTimeout() {
                ZplayDebug.d(TAG, "onTimeout: ");
                mHandler.removeMessages(WHAT_TIMEOUT);
                layerPreparedFailed(new AdError(LayerErrorCode.ERROR_NON_RESPONSE, "Bytedance: timeout"));
                removeSplashViews();
            }

            @Override
            public void onSplashAdLoad(final TTSplashAd ad) {
                ZplayDebug.d(TAG, "开屏广告请求成功");
                if (ad == null) {
                    layerExposureFailed(new AdError(LayerErrorCode.ERROR_NO_FILL, "Bytedance: TTSplashAd is null."));
                    return;
                }
                // 获取SplashView
                View view = ad.getSplashView();
                getDeveloperContainer().removeAllViews();
                // 把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕宽
                getDeveloperContainer().addView(view);
                isHitCloseCallback = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    getDeveloperContainer().getViewTreeObserver().addOnWindowFocusChangeListener(mLayoutListener);
                }

                // 展示广告后立即点击广告，sdk 不会点击 onAdShow 会导致没有上报（合并上报）
                layerExposure();
                mHandler.removeMessages(WHAT_TIMEOUT);

                //设置SplashView的交互监听器
                ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        layerClicked(0, 0);
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        ZplayDebug.d(TAG, "onAdShow: " + type);
                    }

                    @Override
                    public void onAdSkip() {
                        hitCloseCallback();
                    }

                    @Override
                    public void onAdTimeOver() {
                        hitCloseCallback();
                    }
                });
            }
        }, getProvider().getOutTime() * 1000);
    }

    private void hitCloseCallback() {
        if (!isHitCloseCallback) {
            mHandler.removeMessages(WHAT_TIMEOUT);
            isHitCloseCallback = true;
            layerClosed();
            removeSplashViews();
        }
    }

    private void removeSplashViews() {
        if (getDeveloperContainer() != null) {
            getDeveloperContainer().removeAllViews();
            if (mLayoutListener != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                getDeveloperContainer().getViewTreeObserver().removeOnWindowFocusChangeListener(mLayoutListener);
            }
        }
    }
}
