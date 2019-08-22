package com.yumi.android.sdk.ads.adapter.vungle;

import android.content.Context;

import com.vungle.warren.Vungle;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.YumiSettings;
import com.yumi.android.sdk.ads.publish.enumbean.YumiGDPRStatus;

import static com.vungle.warren.Vungle.Consent.OPTED_IN;
import static com.vungle.warren.Vungle.Consent.OPTED_OUT;
import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_NO_FILL;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
class VungleUtil {
    static AdError recodeError(Throwable extra) {
        AdError result = new AdError(ERROR_NO_FILL);
        result.setErrorMessage("Vungle error: " + extra);
        return result;
    }

    static void updateGDPRStatus(Context context) {
        if (YumiSettings.getGDPRStatus() == YumiGDPRStatus.UNKNOWN) {
            return;
        }

        boolean isConsent = YumiSettings.getGDPRStatus() == YumiGDPRStatus.PERSONALIZED;
        // https://support.vungle.com/hc/en-us/articles/360002922871-Get-Started-with-Vungle-Android-or-Amazon-SDK-v-6
        Vungle.Consent consent = isConsent ? OPTED_IN : OPTED_OUT;
        Vungle.updateConsentStatus(consent, "1.0.0");
    }

    static String sdkVersion() {
        return "6.4.10";
    }
}
