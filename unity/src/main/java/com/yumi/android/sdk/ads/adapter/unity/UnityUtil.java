package com.yumi.android.sdk.ads.adapter.unity;

import com.unity3d.ads.UnityAds;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
public class UnityUtil {
    static LayerErrorCode generateLayerErrorCode(UnityAds.UnityAdsError unityAdsError, String message) {
        LayerErrorCode result = LayerErrorCode.ERROR_INTERNAL;
        result.setExtraMsg("Unity errorMsg: " + unityAdsError + ", " + message);
        return result;
    }
}
