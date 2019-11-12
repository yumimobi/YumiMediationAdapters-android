package com.yumi.android.sdk.ads.adapter.playableads;

import com.playableads.PlayableAdsSettings;
import com.playableads.entity.GDPRStatus;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.YumiSettings;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.YumiGDPRStatus;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
class PlayableAdsUtil {
    static AdError recodeError(int playableadsErrorCode, String playableadsErrorMsg) {
        AdError result;
        if (playableadsErrorCode == 2005) { //no ad
            result = new AdError(LayerErrorCode.ERROR_NO_FILL);
        } else {
            result = new AdError(LayerErrorCode.ERROR_INTERNAL);
        }
        result.setErrorMessage("PlayableAds errorMsg: " + playableadsErrorMsg);
        return result;
    }

    static void updateGDPRStatus() {
        if (YumiSettings.getGDPRStatus() == YumiGDPRStatus.NON_PERSONALIZED) {
            PlayableAdsSettings.setGDPRConsent(GDPRStatus.NON_PERSONALIZED);
        } else if (YumiSettings.getGDPRStatus() == YumiGDPRStatus.PERSONALIZED) {
            PlayableAdsSettings.setGDPRConsent(GDPRStatus.PERSONALIZED);
        }
    }

    static String sdkVersion() {
        return "2.6.0";
    }
}
