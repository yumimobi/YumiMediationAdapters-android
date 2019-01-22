package com.yumi.android.sdk.ads.adapter;

import com.qq.e.comm.util.AdError;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Created by hjl on 2017/10/26.
 */

public class ErrorCodeHelp {
    public static LayerErrorCode decodeErrorCode(AdError gdtError) {
        if (gdtError == null) {
            return LayerErrorCode.ERROR_INTERNAL;
        }

        LayerErrorCode error;
        if (gdtError.getErrorCode() == 4003) {
            error = LayerErrorCode.ERROR_INVALID;
        } else if (gdtError.getErrorCode() == 5004) {
            error = LayerErrorCode.ERROR_NO_FILL;
        } else {
            error = LayerErrorCode.ERROR_INTERNAL;
        }
        error.setExtraMsg("GAT errorMsg: " + gdtError.getErrorMsg());
        return error;
    }
}
