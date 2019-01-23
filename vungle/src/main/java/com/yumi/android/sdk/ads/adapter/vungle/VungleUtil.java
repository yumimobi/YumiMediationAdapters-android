package com.yumi.android.sdk.ads.adapter.vungle;

import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_NO_FILL;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
class VungleUtil {
    static LayerErrorCode recodeError(Throwable extra){
        LayerErrorCode result = ERROR_NO_FILL;
        result.setExtraMsg("Vungle error: " + extra);
        return result;
    }
}
