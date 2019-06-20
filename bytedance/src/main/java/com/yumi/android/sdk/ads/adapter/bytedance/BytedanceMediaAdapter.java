package com.yumi.android.sdk.ads.adapter.bytedance;

import android.app.Activity;
import android.content.res.Configuration;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;
import com.yumi.android.sdk.ads.utils.device.WindowSizeUtils;

import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.getAppName;
import static com.yumi.android.sdk.ads.adapter.bytedance.BytedanceUtil.recodeError;


public class BytedanceMediaAdapter extends YumiCustomerMediaAdapter {

    private static final String TAG = "BytedanceMediaAdapter";
    private TTAdNative mTTAdNative;
    private TTRewardVideoAd mttRewardVideoAd;
    private TTAdNative.RewardVideoAdListener loadListener;
    private int rewardHeight = 1920;
    private int rewardWidth = 1080;
    private boolean isReady = false;
    private boolean isRewarded = false;

    protected BytedanceMediaAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    protected void onPrepareMedia() {
        ZplayDebug.d(TAG, "Bytedance requese new media", onoff);
        if (mTTAdNative != null && loadListener != null) {
            calculateMediaSize();
            isReady = false;
            AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId(getProvider().getKey2())
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(rewardWidth, rewardHeight)
                    .setUserID("")
                    .setMediaExtra("")
                    .setOrientation(isLandscaps())
                    .build();
            //step5:请求广告
            mTTAdNative.loadRewardVideoAd(adSlot, loadListener);

        }
    }

    @Override
    protected void onShowMedia() {
        if (mttRewardVideoAd != null && isReady) {
            //step6:在获取到广告后展示
            mttRewardVideoAd.showRewardVideoAd(getActivity());
            mttRewardVideoAd = null;
        }
    }

    @Override
    protected boolean isMediaReady() {
        return isReady;
    }

    @Override
    protected void init() {
        ZplayDebug.d(TAG, "bytedance media init key1: " + getProvider().getKey1() + "key2: " + getProvider().getKey2(), onoff);

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
        mTTAdNative = TTAdSdk.getAdManager().createAdNative(getActivity());//baseContext建议为activity
        createrListener();
    }

    protected void createrListener() {
        loadListener = new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                ZplayDebug.d(TAG, "Bytedance media Prepared Failed , message ：" + message, onoff);
                layerPreparedFailed(recodeError(code, message));
                isReady = false;
            }

            @Override
            public void onRewardVideoCached() {
                ZplayDebug.d(TAG, "Bytedance media Prepared ", onoff);
                isReady = true;
                layerPrepared();
            }

            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                mttRewardVideoAd = ad;
                setRewardAdInteractionListener(mttRewardVideoAd);
            }
        };

    }


    private void setRewardAdInteractionListener(TTRewardVideoAd ttRewardVideoAd) {
        ttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

            @Override
            public void onAdShow() {
                ZplayDebug.d(TAG, "Bytedance media show ", onoff);
                isReady = false;
                isRewarded = false;
                layerExposure();
                layerStartPlaying();
            }

            @Override
            public void onAdVideoBarClick() {
                ZplayDebug.d(TAG, "Bytedance media click ", onoff);
                isReady = false;
                layerClicked();
            }

            @Override
            public void onAdClose() {
                ZplayDebug.d(TAG, "Bytedance media close ", onoff);
                layerClosed(isRewarded);
            }

            @Override
            public void onVideoComplete() {
                isReady = false;
            }

            @Override
            public void onVideoError() {
                isReady = false;
                isRewarded = false;
            }

            //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
            @Override
            public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
                ZplayDebug.d(TAG, "Bytedance media Reward rewardVerify:" + rewardVerify, onoff);
                isReady = false;
                isRewarded = true;
                layerIncentived();
            }
        });
    }

    @Override
    protected void callOnActivityDestroy() {

    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityResume() {

    }

    /**
     * 获取横竖屏的标示，约定1：竖屏 0：横屏
     *
     * @return
     */
    private int isLandscaps() {
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return TTAdConstant.HORIZONTAL;
        } else {
            return TTAdConstant.VERTICAL;
        }
    }

    private void calculateMediaSize() {
        try {
            int[] realSize = WindowSizeUtils.getRealSize(getActivity());
            rewardHeight = realSize[1];
            rewardWidth = realSize[0];
        } catch (Exception e) {
            if (isLandscaps() == 2) {
                rewardHeight = 1080;
                rewardWidth = 1920;
            } else {
                rewardHeight = 1920;
                rewardWidth = 1080;
            }
        }
    }
}
