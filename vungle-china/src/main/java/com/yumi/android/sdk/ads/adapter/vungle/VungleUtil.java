package com.yumi.android.sdk.ads.adapter.vungle;

import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_NO_FILL;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
class VungleUtil {
    static AdError recodeError(Throwable extra){
        AdError result = new AdError(ERROR_NO_FILL);
        result.setErrorMessage("Vungle-China error: " + extra);
        return result;
    }
}
