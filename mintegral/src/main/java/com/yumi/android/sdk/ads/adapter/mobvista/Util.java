package com.yumi.android.sdk.ads.adapter.mobvista;

import android.content.Context;

import com.mintegral.msdk.MIntegralConstans;
import com.mintegral.msdk.MIntegralSDK;
import com.mintegral.msdk.out.MIntegralSDKFactory;
import com.yumi.android.sdk.ads.publish.YumiSettings;
import com.yumi.android.sdk.ads.publish.enumbean.YumiGDPRStatus;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.Map;

/**
 * Description:
 * <p>
 * Created by lgd on 2019-08-22.
 */
public class Util {
    private static final String TAG = "MobvistaUtil";

    static String sdkVersion() {
        return "13.1.11";
    }

    public static void initSDK(Context mContext,String appId, String appKey){
        try {
            ZplayDebug.d(TAG, "init: appId: " + appId + ", appKey: " + appKey);
            MIntegralSDK sdk = MIntegralSDKFactory.getMIntegralSDK();
            Map<String, String> map = sdk.getMTGConfigurationMap(appId, appKey); //appId, appKey
            if (YumiSettings.getGDPRStatus() != YumiGDPRStatus.UNKNOWN) {
                int isConsent = YumiSettings.getGDPRStatus() == YumiGDPRStatus.PERSONALIZED ? MIntegralConstans.IS_SWITCH_ON : MIntegralConstans.IS_SWITCH_OFF;
                sdk.setUserPrivateInfoType(mContext, MIntegralConstans.AUTHORITY_ALL_INFO, isConsent);
            }
            sdk.init(map, mContext);
        } catch (Exception e) {
            ZplayDebug.e(TAG, "init: exception.", e);
        }
    }
}
