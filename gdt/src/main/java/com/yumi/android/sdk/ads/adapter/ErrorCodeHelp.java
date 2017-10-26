package com.yumi.android.sdk.ads.adapter;

import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Created by hjl on 2017/10/26.
 */

public class ErrorCodeHelp {
    public static LayerErrorCode decodeErrorCode(int arg0) {
        if (arg0 == 500) {
            return LayerErrorCode.ERROR_INVALID;
        }
        if (arg0 == 501) {
            return LayerErrorCode.ERROR_NO_FILL;
        }
        if (arg0 >= 400 && arg0 < 500) {
            return LayerErrorCode.ERROR_NETWORK_ERROR;
        }
        return LayerErrorCode.ERROR_INTERNAL;
    }
}
