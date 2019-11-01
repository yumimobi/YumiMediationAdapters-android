package com.yumi.android.sdk.ads.adapter.playableads;

import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

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

    static String sdkVersion() {
        return "2.6.0";
    }
}
