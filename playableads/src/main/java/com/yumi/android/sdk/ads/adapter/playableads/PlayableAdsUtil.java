package com.yumi.android.sdk.ads.adapter.playableads;

import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
class PlayableAdsUtil {
    static LayerErrorCode recodeError(int playableadsErrorCode, String playableadsErrorMsg){
        LayerErrorCode result;
        if (playableadsErrorCode == 2005) { //no ad
            result = LayerErrorCode.ERROR_NO_FILL;
        } else {
            result = LayerErrorCode.ERROR_INTERNAL;
        }
        result.setExtraMsg("PlayableAds errorMsg: " + playableadsErrorMsg);
        return result;
    }
}
