package com.yumi.android.sdk.ads.adapter.facebook;

import com.facebook.ads.AdError;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Created by hjl on 2018/8/7.
 */
public class FacebookAdErrorHolder {

    public static LayerErrorCode decodeError(AdError arg1) {
        if (arg1.getErrorCode()==1000) {
            return LayerErrorCode.ERROR_NETWORK_ERROR;
        }
        if (arg1.getErrorCode()==1001) {
            return LayerErrorCode.ERROR_NO_FILL;
        }
        return LayerErrorCode.ERROR_INTERNAL;
    }
}
