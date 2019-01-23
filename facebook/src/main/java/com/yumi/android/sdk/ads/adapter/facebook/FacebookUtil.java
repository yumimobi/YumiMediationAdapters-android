package com.yumi.android.sdk.ads.adapter.facebook;

import com.facebook.ads.AdError;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Created by hjl on 2018/8/7.
 */
public class FacebookUtil {

    public static LayerErrorCode recodeError(AdError adError) {
        LayerErrorCode result;
        if(adError == null){
            result = LayerErrorCode.ERROR_INTERNAL;
            result.setExtraMsg("Facebook errorMsg: null" );
            return result;
        }

        switch (adError.getErrorCode()) {
            case AdError.NETWORK_ERROR_CODE:
                result = LayerErrorCode.ERROR_NETWORK_ERROR;
                break;
            case AdError.NO_FILL_ERROR_CODE:
                result = LayerErrorCode.ERROR_NO_FILL;
                break;
            default:
                result = LayerErrorCode.ERROR_INTERNAL;
                break;
        }
        result.setExtraMsg("Facebook errorCode: " + adError.getErrorCode() + " and errorMsg: " + adError.getErrorMessage());
        return result;
    }
}
