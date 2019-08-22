package com.yumi.android.sdk.ads.adapter.iqzone;

import android.content.Context;

import com.iqzone.android.GDPR;
import com.iqzone.android.GDPRConsent;
import com.iqzone.android.IQzoneBannerView;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.YumiSettings;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.YumiGDPRStatus;

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

    static void updateGDPRStatus(Context context) {
        if (YumiSettings.getGDPRStatus() != YumiGDPRStatus.UNKNOWN) {
            GDPRConsent consent = YumiSettings.getGDPRStatus() == YumiGDPRStatus.PERSONALIZED ? GDPRConsent.CONSENTED : GDPRConsent.DOES_NOT_CONSENT;
            IQzoneBannerView imdBannerAd = new IQzoneBannerView(context);
            imdBannerAd.setGDPRApplies(GDPR.APPLIES, consent);
        }
    }

    static String sdkVersion() {
        return "2.3.2111";
    }
}
