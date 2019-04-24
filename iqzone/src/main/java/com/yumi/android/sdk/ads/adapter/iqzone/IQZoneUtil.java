package com.yumi.android.sdk.ads.adapter.iqzone;

import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

import static android.text.TextUtils.isEmpty;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/28.
 */
class IQZoneUtil {
    static AdError recodeError(LayerErrorCode layerErrorCode) {
        return recodeError(layerErrorCode, null);
    }

    static AdError recodeError(LayerErrorCode layerErrorCode, String yumiLog) {
        String extraMsg = "IQZone errorMsg: null";

        if (!isEmpty(yumiLog)) {
            extraMsg += ", " + yumiLog;
        }

        AdError result = new AdError(layerErrorCode);
        result.setErrorMessage(extraMsg);
        return result;
    }
}
