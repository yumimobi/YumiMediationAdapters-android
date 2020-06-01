package com.yumi.android.sdk.ads.adapter.atmosplay;

import com.atmosplayads.AtmosplayAdsSettings;
import com.atmosplayads.entity.GDPRStatus;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.YumiSettings;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.YumiGDPRStatus;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
class AtmosplayAdsUtil {
    static AdError recodeError(int ErrorCode, String ErrorMsg) {
        AdError result;
        if (ErrorCode == 2005) { //no ad
            result = new AdError(LayerErrorCode.ERROR_NO_FILL);
        } else {
            result = new AdError(LayerErrorCode.ERROR_INTERNAL);
        }
        result.setErrorMessage("Atmosplay errorMsg: " + ErrorMsg);
        return result;
    }

    static void updateGDPRStatus() {
        if (YumiSettings.getGDPRStatus() == YumiGDPRStatus.NON_PERSONALIZED) {
            AtmosplayAdsSettings.setGDPRConsent(GDPRStatus.NON_PERSONALIZED);
        } else if (YumiSettings.getGDPRStatus() == YumiGDPRStatus.PERSONALIZED) {
            AtmosplayAdsSettings.setGDPRConsent(GDPRStatus.PERSONALIZED);
        }
    }

    static String sdkVersion() {
        return "3.1.0";
    }
}
