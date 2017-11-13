package com.yumi.android.sdk.ads.mraid;

import android.view.Gravity;
import android.widget.FrameLayout;

import com.yumi.android.sdk.ads.self.ads.b.BannerAD;

/**
 * Created by mzk10 on 2017/11/1.
 */

public class MyBannerMraidiListener implements MRAIDViewListener {

    private boolean isLoaded;
    private FrameLayout.LayoutParams params_web;
    private int banner_width;
    private int banner_height;
    private FrameLayout bannerBox;
    private BannerAD bannerAD;

    public MyBannerMraidiListener(FrameLayout.LayoutParams params_web, int banner_width, int banner_height, FrameLayout bannerBox, BannerAD bannerAD) {
        this.params_web = params_web;
        this.banner_width = banner_width;
        this.banner_height = banner_height;
        this.bannerBox = bannerBox;
        this.bannerAD = bannerAD;
    }

    public void mraidViewLoaded(MRAIDView mraidView) {
        if (isLoaded) {
            return;
        }
        FrameLayout.LayoutParams params_box = new FrameLayout.LayoutParams(
                banner_width, banner_height);
        params_box.gravity = Gravity.CENTER;
        bannerBox.setLayoutParams(params_box);
        bannerBox.addView(mraidView, params_web);
        bannerAD.onBannerShow("展示成功");
        isLoaded = true;
    }

    public void mraidViewExpand(MRAIDView mraidView) {
    }

    public void mraidViewClose(MRAIDView mraidView) {
    }

    public boolean mraidViewResize(MRAIDView mraidView, int width, int height, int offsetX, int offsetY) {
        return false;
    }
}
