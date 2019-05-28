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
                    .setCodeId("901121365")
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(rewardWidth, rewardHeight)
                    .setUserID("")//用户id,必传参数
                    .setMediaExtra("") //附加参数，可选
                    .setOrientation(isLandscaps()) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
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
                        .appId("5001121")
                        .useTextureView(false) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                        .appName(getActivity().getPackageName())
                        .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                        .allowShowNotify(false) //是否允许sdk展示通知栏提示
                        .allowShowPageWhenScreenLock(true) //是否在锁屏场景支持展示广告落地页
                        .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                        .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_3G) //允许直接下载的网络状态集合
                        .supportMultiProcess(false) //是否支持多进程，true支持
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

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
                ZplayDebug.d(TAG, "Bytedance media Prepared ", onoff);
                isReady = true;
                layerPrepared();
            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                mttRewardVideoAd = ad;
//                mttRewardVideoAd.setShowDownLoadBar(false);
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

            //视频播放完成回调
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
