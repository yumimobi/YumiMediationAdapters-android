package com.yumi.android.sdk.ads.adapter.baidu;

import com.baidu.mobad.feeds.NativeErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
class BaiduUtil {

    static LayerErrorCode recodeError(String baiduErrorMes){
        LayerErrorCode error = LayerErrorCode.ERROR_INTERNAL;
        error.setExtraMsg("Baidu errorMsg: " + baiduErrorMes);
        return  error;
    }

    static LayerErrorCode recodeNativeError(NativeErrorCode nativeErrorCode, String errMsg){
        LayerErrorCode result;
        if (nativeErrorCode == NativeErrorCode.LOAD_AD_FAILED) {
            result = LayerErrorCode.ERROR_NO_FILL;
        } else if (nativeErrorCode == NativeErrorCode.CONFIG_ERROR) {
            result = LayerErrorCode.ERROR_INVALID;
        } else {
            result = LayerErrorCode.ERROR_INTERNAL;
        }
        result.setExtraMsg(errMsg);
        return  result;
    }
}
