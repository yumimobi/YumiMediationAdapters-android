package com.yumi.android.sdk.ads.adapter.facebook;

import com.facebook.ads.AdError;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Created by hjl on 2018/8/7.
 */
public class FacebookAdErrorHolder {

    public static LayerErrorCode decodeError(AdError adError) {
        if(adError == null){
            return LayerErrorCode.ERROR_INTERNAL;
        }

        LayerErrorCode error;
        switch (adError.getErrorCode()) {
            case AdError.NETWORK_ERROR_CODE:
                error = LayerErrorCode.ERROR_NETWORK_ERROR;
                break;
            case AdError.NO_FILL_ERROR_CODE:
                error = LayerErrorCode.ERROR_NO_FILL;
                break;
            default:
                error = LayerErrorCode.ERROR_INTERNAL;
                break;
        }
        error.setExtraMsg("Facebook errorCode: " + adError.getErrorCode() + " and errorMsg: " + adError.getErrorMessage());
        return error;
    }
}
