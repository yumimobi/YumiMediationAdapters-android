package com.yumi.android.sdk.ads.adapter.iqzone;

import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

import static android.text.TextUtils.isEmpty;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/28.
 */
class IQZoneUtil {
    static LayerErrorCode recodeError(LayerErrorCode layerErrorCode) {
        return recodeError(layerErrorCode, null);
    }

    static LayerErrorCode recodeError(LayerErrorCode layerErrorCode, String yumiLog) {
        String extraMsg = "IQZone errorMsg: null";

        if (!isEmpty(yumiLog)) {
            extraMsg += ", " + yumiLog;
        }

        layerErrorCode.setExtraMsg(extraMsg);
        return layerErrorCode;
    }
}
