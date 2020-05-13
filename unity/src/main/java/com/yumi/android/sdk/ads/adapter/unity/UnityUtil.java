package com.yumi.android.sdk.ads.adapter.unity;

import android.content.Context;

import com.unity3d.ads.UnityAds;
import com.unity3d.ads.metadata.MetaData;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.YumiSettings;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.YumiGDPRStatus;

import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_INTERNAL;
import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_NO_FILL;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
class UnityUtil {
    static AdError generateLayerErrorCode(UnityAds.UnityAdsError unityAdsError, String message) {
        AdError result = new AdError(ERROR_INTERNAL);
        result.setErrorMessage("Unity errorMsg: " + unityAdsError + ", " + message);
        return result;
    }

    static AdError generateLayerErrorCode(UnityAds.PlacementState unityState, String message) {
        LayerErrorCode code = unityState == UnityAds.PlacementState.NO_FILL ? ERROR_NO_FILL : ERROR_INTERNAL;
        AdError result = new AdError(code);
        result.setErrorMessage("Unity errorMsg: " + unityState + ", " + message);
        return result;
    }

    static void updateGDPRStatus(Context context) {
        if (YumiSettings.getGDPRStatus() == YumiGDPRStatus.UNKNOWN) {
            return;
        }
        boolean isConsent = YumiSettings.getGDPRStatus() == YumiGDPRStatus.PERSONALIZED;
        // https://unityads.unity3d.com/help/legal/gdpr
        MetaData gdprMetaData = new MetaData(context);
        gdprMetaData.set("gdpr.consent", isConsent);
        gdprMetaData.commit();
    }

    static String sdkVersion() {
        return "3.4.2";
    }
}
