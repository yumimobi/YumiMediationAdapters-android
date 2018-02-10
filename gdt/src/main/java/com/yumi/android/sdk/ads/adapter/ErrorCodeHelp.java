package com.yumi.android.sdk.ads.adapter;

import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Created by hjl on 2017/10/26.
 */

public class ErrorCodeHelp {
    public static LayerErrorCode decodeErrorCode(int arg0) {
        if (arg0 == 4003) {
            return LayerErrorCode.ERROR_INVALID;
        }
        if (arg0 == 5004) {
            return LayerErrorCode.ERROR_NO_FILL;
        }
        return LayerErrorCode.ERROR_INTERNAL;
    }
}
