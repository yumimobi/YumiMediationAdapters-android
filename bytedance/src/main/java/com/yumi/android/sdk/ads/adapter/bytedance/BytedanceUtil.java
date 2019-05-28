package com.yumi.android.sdk.ads.adapter.bytedance;

import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
public class BytedanceUtil {
    public static AdError recodeError(int errorCode, String errMags) {
        LayerErrorCode errCode;
        errCode = LayerErrorCode.ERROR_NO_FILL;

        AdError adError = new AdError(errCode);
        adError.setErrorMessage("Bytedance errorCode: " + errorCode +", errorMessage: " + errMags);
        return adError;
    }

    public static AdError recodeNativeAdError(int errorCode, String errMags) {
        LayerErrorCode errCode;
        errCode = LayerErrorCode.ERROR_NO_FILL;

        AdError adError = new AdError(errCode);
        adError.setErrorMessage("Bytedance errorCode: " + errorCode +", errorMessage: " + errMags);
        return adError;
    }
}
