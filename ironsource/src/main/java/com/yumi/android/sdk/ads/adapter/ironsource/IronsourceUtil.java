package com.yumi.android.sdk.ads.adapter.ironsource;

import android.content.Context;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.YumiSettings;
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

    static void updateGDPRStatus(Context context){
        Boolean isConsent = YumiSettings.isGDPRConsent(context);
        if(isConsent == null) {
            return;
        }
        // https://developers.ironsrc.com/ironsource-mobile/android/advanced-settings/
        IronSource.setConsent(isConsent);
    }
}
