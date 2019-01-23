package com.yumi.android.sdk.ads.adapter.adcolony;

import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
class AdcolonyUtil {
    static LayerErrorCode recodeError() {
        LayerErrorCode result = LayerErrorCode.ERROR_NO_FILL;
        result.setExtraMsg("Adconony errorMsg: request not filled.");
        return result;
    }
}
