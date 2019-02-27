package com.yumi.android.sdk.ads.adapter.admob;

import com.google.android.gms.ads.AdRequest;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
public class AdMobUtil {
    public static LayerErrorCode recodeError(int errorCode) {
        LayerErrorCode result;
        switch (errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                result = LayerErrorCode.ERROR_INTERNAL;
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                result = LayerErrorCode.ERROR_INVALID;
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                result = LayerErrorCode.ERROR_NO_FILL;
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                result = LayerErrorCode.ERROR_NETWORK_ERROR;
                break;
            case 502:
                result = LayerErrorCode.ERROR_NON_RESPONSE;
                break;
            default:
                result = LayerErrorCode.ERROR_INTERNAL;
                break;
        }
        result.setExtraMsg("AdMob errorCode: " + errorCode);
        return result;
    }
}
