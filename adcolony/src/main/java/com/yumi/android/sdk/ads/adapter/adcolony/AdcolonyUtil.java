package com.yumi.android.sdk.ads.adapter.adcolony;

import com.yumi.android.sdk.ads.publish.AdError;

import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_NO_FILL;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
class AdcolonyUtil {
    static AdError recodeError() {
        AdError result = new AdError(ERROR_NO_FILL);
        result.setErrorMessage("Adcolony errorMsg: request not filled.");
        return result;
    }

    static String sdkVersion(){
        return "3.3.10";
    }
}
