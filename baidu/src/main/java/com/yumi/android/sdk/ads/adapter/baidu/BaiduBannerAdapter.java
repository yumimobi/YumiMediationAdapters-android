package com.yumi.android.sdk.ads.adapter.baidu;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.baidu.mobad.feeds.NativeErrorCode;
import com.baidu.mobads.AdSize;
import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerBannerAdapter;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import org.json.JSONObject;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.yumi.android.sdk.ads.adapter.baidu.BaiduUtil.dp2px;
import static com.yumi.android.sdk.ads.adapter.baidu.BaiduUtil.isTablet;
import static com.yumi.android.sdk.ads.adapter.baidu.BaiduUtil.recodeError;
import static com.yumi.android.sdk.ads.adapter.baidu.BaiduUtil.recodeNativeError;
import static com.yumi.android.sdk.ads.adapter.baidu.BaiduUtil.sdkVersion;
import static com.yumi.android.sdk.ads.publish.enumbean.AdSize.BANNER_SIZE_SMART;

public class BaiduBannerAdapter extends YumiCustomerBannerAdapter {

    private static final String TAG = "BaiduBannerAdapter";
    private AdViewListener bannerListener;
    private boolean isLoad = true;
    private FrameLayout mActivityContent;
    private FrameLayout mBannerContainer;

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
    protected void onDestroy() {
        super.onDestroy();
        removeTempViews();
    }

    @Override
    protected void onPrepareBannerLayer() {
        if (bannerSize == BANNER_SIZE_SMART) {
            ZplayDebug.i(TAG, "not support smart banner");
            layerPreparedFailed(recodeNativeError(NativeErrorCode.LOAD_AD_FAILED, "not support smart banner"));
            return;
        }
        ZplayDebug.d(TAG, "load new banner");
        isLoad = true;
        AdView banner = new AdView(getActivity(), AdSize.Banner, getProvider().getKey2());
        banner.setListener(bannerListener);
        removeTempViews();
        mActivityContent = newActivityContentView();
        mBannerContainer = newFrameLayout();
        mActivityContent.addView(mBannerContainer);
        mBannerContainer.addView(banner);
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "init appSid : " + getProvider().getKey1() + ",adPlaceId : " + getProvider().getKey2());
        createBannerListener();
        AdView.setAppSid(getActivity(), getProvider().getKey1());
    }

    private void createBannerListener() {
        bannerListener = new AdViewListener() {

            @Override
            public void onAdSwitch() {

            }

            @Override
            public void onAdShow(JSONObject arg0) {
                ZplayDebug.d(TAG, "onAdShow");
                if (isLoad) {
                    isLoad = false;
                }
            }

            @Override
            public void onAdReady(AdView arg0) {
                ZplayDebug.d(TAG, "onAdReady");
                removeTempViews();
                layerPrepared(arg0, true);
            }

            @Override
            public void onAdFailed(String arg0) {
                ZplayDebug.d(TAG, "onAdFailed " + arg0);
                removeTempViews();
                layerPreparedFailed(recodeError(arg0));
            }

            @Override
            public void onAdClick(JSONObject arg0) {
                ZplayDebug.d(TAG, "onAdClick");
                layerClicked(-99f, -99f);
            }

            @Override
            public void onAdClose(JSONObject arg0) {
                ZplayDebug.d(TAG, "onAdClose");
                layerClosed();
            }
        };
    }

    private FrameLayout newActivityContentView() {
        FrameLayout result = new FrameLayout(getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        getActivity().addContentView(result, params);
        return result;
    }

    private FrameLayout newFrameLayout() {
        int[] wh = getWH();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(wh[0], wh[1]);
        params.gravity = Gravity.BOTTOM;
        FrameLayout result = new FrameLayout(getActivity());
        result.setLayoutParams(params);
        return result;
    }

    private void removeTempViews() {
        removeView(mActivityContent);
        removeView(mBannerContainer);
    }

    private void removeView(View view) {
        if (view != null && view.getParent() instanceof ViewGroup) {
            if (view.getParent() instanceof ViewGroup) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
        }
    }

    private int[] getWH() {
        // 百度 Banner
        switch (bannerSize) {
            case BANNER_SIZE_728X90:
                return new int[]{dp2px(720), dp2px(720f / 20 * 3)};
            case BANNER_SIZE_AUTO:
                if (isTablet()) {
                    return new int[]{dp2px(720), dp2px(720f / 20 * 3)};
                }
            default:
                return new int[]{dp2px(320), dp2px(50)};
        }

    }

    @Override
    public String getProviderVersion() {
        return sdkVersion();
    }
}
