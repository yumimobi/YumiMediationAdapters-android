package com.yumi.android.sdk.ads.mraid;

import com.yumi.android.sdk.ads.self.ads.i.IntersititialAD;

/**
 * Created by mzk10 on 2017/11/1.
 */

public class MyIntersititialMraidiListener implements MRAIDViewListener {

    private IntersititialAD intersititialAD;

    public MyIntersititialMraidiListener(IntersititialAD intersititialAD)
    {
        this.intersititialAD = intersititialAD;
    }
    @Override
    public void mraidViewLoaded(MRAIDView mraidView) {
        intersititialAD.setStatusPrepared();
        intersititialAD.pushListenerPrepare();
    }

    @Override
    public void mraidViewExpand(MRAIDView mraidView) {

    }

    @Override
    public void mraidViewClose(MRAIDView mraidView) {

    }

    @Override
    public boolean mraidViewResize(MRAIDView mraidView, int width, int height, int offsetX, int offsetY) {
        return false;
    }
}
