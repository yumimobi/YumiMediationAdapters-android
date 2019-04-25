package com.yumi.android.sdk.ads.adapter.unity;

import com.unity3d.ads.UnityAds;
import com.yumi.android.sdk.ads.publish.AdError;
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
}
