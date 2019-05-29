package com.yumi.android.sdk.ads.adapter.unity;

import android.content.Context;

import com.unity3d.ads.UnityAds;
import com.unity3d.ads.metadata.MetaData;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.YumiSettings;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
public class UnityUtil {
    static AdError generateLayerErrorCode(UnityAds.UnityAdsError unityAdsError, String message) {
        AdError result = new AdError(LayerErrorCode.ERROR_INTERNAL);
        result.setErrorMessage("Unity-China errorMsg: " + unityAdsError + ", " + message);
        return result;
    }

    static void updateGDPRStatus(Context context){
        Boolean isConsent = YumiSettings.isGDPRConsent();
        if(isConsent == null){
            return;
        }

        // https://unityads.unity3d.com/help/legal/gdpr
        MetaData gdprMetaData = new MetaData(context);
        gdprMetaData.set("gdpr.consent", isConsent);
        gdprMetaData.commit();
    }
}
