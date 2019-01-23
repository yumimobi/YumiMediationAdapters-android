package com.yumi.android.sdk.ads.adapter.ironsource;

import com.ironsource.mediationsdk.logger.IronSourceError;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
class IronsourceUtil {
    static LayerErrorCode generateLayerErrorCode(IronSourceError ironSourceError) {
        LayerErrorCode result;
        if (ironSourceError == null) {
            result = LayerErrorCode.ERROR_INTERNAL;
            result.setExtraMsg("IronSource errorMsg: null");
            return result;
        }

        if (ironSourceError.getErrorCode() == IronSourceError.ERROR_BN_LOAD_NO_FILL) {
            result = LayerErrorCode.ERROR_NO_FILL;
        } else {
            result = LayerErrorCode.ERROR_INTERNAL;
        }
        result.setExtraMsg("IronSource errorMsg: " + ironSourceError.getErrorMessage());
        return result;
    }
}
