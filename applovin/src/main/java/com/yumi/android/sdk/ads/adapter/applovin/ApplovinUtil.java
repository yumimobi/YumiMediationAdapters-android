package com.yumi.android.sdk.ads.adapter.applovin;

import com.applovin.sdk.AppLovinErrorCodes;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
class ApplovinUtil {
    static LayerErrorCode recodeError(int applovinErrorCode) {
        LayerErrorCode error;
        if (applovinErrorCode == AppLovinErrorCodes.NO_FILL) {
            error = LayerErrorCode.ERROR_NO_FILL;
        } else {
            error = LayerErrorCode.ERROR_INTERNAL;
        }
        error.setExtraMsg("Applovin errorCode: " + applovinErrorCode);
        return error;
    }
}
