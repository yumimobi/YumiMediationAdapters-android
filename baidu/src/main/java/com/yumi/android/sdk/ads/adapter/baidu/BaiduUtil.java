package com.yumi.android.sdk.ads.adapter.baidu;

import com.baidu.mobad.feeds.NativeErrorCode;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
class BaiduUtil {

    static AdError recodeError(String baiduErrorMes) {
        AdError error = new AdError(LayerErrorCode.ERROR_INTERNAL);
        error.setErrorMessage("Baidu errorMsg: " + baiduErrorMes);
        return error;
    }

    static AdError recodeNativeError(NativeErrorCode nativeErrorCode, String errMsg) {
        LayerErrorCode errCode;
        if (nativeErrorCode == NativeErrorCode.LOAD_AD_FAILED) {
            errCode = LayerErrorCode.ERROR_NO_FILL;
        } else if (nativeErrorCode == NativeErrorCode.CONFIG_ERROR) {
            errCode = LayerErrorCode.ERROR_INVALID;
        } else {
            errCode = LayerErrorCode.ERROR_INTERNAL;
        }

        AdError result = new AdError(errCode);
        result.setErrorMessage("Baidu errorMsg: " + errMsg);
        return result;
    }
}
