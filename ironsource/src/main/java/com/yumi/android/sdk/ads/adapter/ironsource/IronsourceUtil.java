package com.yumi.android.sdk.ads.adapter.ironsource;

import com.ironsource.mediationsdk.logger.IronSourceError;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
class IronsourceUtil {
    static AdError generateLayerErrorCode(IronSourceError ironSourceError) {
        AdError result;
        if (ironSourceError == null) {
            result = new AdError(LayerErrorCode.ERROR_INTERNAL);
            result.setErrorMessage("IronSource errorMsg: null");
            return result;
        }

        if (ironSourceError.getErrorCode() == IronSourceError.ERROR_BN_LOAD_NO_FILL) {
            result = new AdError(LayerErrorCode.ERROR_NO_FILL);
        } else {
            result = new AdError(LayerErrorCode.ERROR_INTERNAL);
        }
        result.setErrorMessage("IronSource errorMsg: " + ironSourceError.getErrorMessage());
        return result;
    }
}
