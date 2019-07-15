package com.yumi.android.sdk.ads.adapter.baidu;

import android.app.Activity;

import com.baidu.mobad.feeds.NativeErrorCode;
import com.baidu.mobads.AdSize;
import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import org.json.JSONObject;

import static com.yumi.android.sdk.ads.adapter.baidu.BaiduUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.baidu.BaiduUtil.recodeNativeError;
import static com.yumi.android.sdk.ads.publish.enumbean.AdSize.BANNER_SIZE_SMART;

public class BaiduBannerAdapter extends YumiCustomerBannerAdapter {

    private static final String TAG = "BaiduBannerAdapter";
    private AdView banner;
    private AdViewListener bannerListener;
    private boolean isLoad = true;

    protected BaiduBannerAdapter(Activity activity, YumiProviderBean provider) {
        super(activity, provider);
    }

    @Override
    public void onActivityPause() {
    }

    @Override
    public void onActivityResume() {
    }

    @Override
    protected void onPrepareBannerLayer() {
        if (bannerSize == BANNER_SIZE_SMART) {
            ZplayDebug.i(TAG, "baidu not support smart banner", onoff);
            layerPreparedFailed(recodeNativeError(NativeErrorCode.LOAD_AD_FAILED, "not support smart banner"));
            return;
        }
        ZplayDebug.d(TAG, "baidu request new banner", onoff);
        isLoad = true;
        banner = new AdView(getActivity(), AdSize.Banner, getProvider().getKey2());
        banner.setListener(bannerListener);
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "appSid : " + getProvider().getKey1(), onoff);
        ZplayDebug.i(TAG, "adPlaceId : " + getProvider().getKey2(), onoff);
        createBannerListener();
        AdView.setAppSid(getContext(), getProvider().getKey1());
    }

    private void createBannerListener() {
        bannerListener = new AdViewListener() {

            @Override
            public void onAdSwitch() {

            }

            @Override
            public void onAdShow(JSONObject arg0) {
                ZplayDebug.d(TAG, "baidu banner shown", onoff);
                if (isLoad) {
                    isLoad = false;
                }
            }

            @Override
            public void onAdReady(AdView arg0) {
                ZplayDebug.d(TAG, "baidu banner prepared", onoff);
                layerPrepared(arg0, true);
            }

            @Override
            public void onAdFailed(String arg0) {
                ZplayDebug.d(TAG, "baidu banner failed " + arg0, onoff);
                layerPreparedFailed(recodeError(arg0));
            }

            @Override
            public void onAdClick(JSONObject arg0) {
                ZplayDebug.d(TAG, "baidu banner click", onoff);
                layerClicked(-99f, -99f);
            }

            @Override
            public void onAdClose(JSONObject arg0) {
                // TODO Auto-generated method stub
                layerClosed();
            }
        };
    }

}
