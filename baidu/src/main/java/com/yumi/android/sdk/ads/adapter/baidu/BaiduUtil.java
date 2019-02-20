package com.yumi.android.sdk.ads.adapter.baidu;

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

    static LayerErrorCode recodeNativeError(LayerErrorCode error,String baiduErrorMes){
        error.setExtraMsg("Baidu errorMsg: " + baiduErrorMes);
        return  error;
    }
}
