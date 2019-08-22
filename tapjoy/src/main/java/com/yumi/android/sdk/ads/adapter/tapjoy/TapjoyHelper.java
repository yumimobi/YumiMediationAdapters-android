package com.yumi.android.sdk.ads.adapter.tapjoy;

import android.content.Context;

import com.tapjoy.TJConnectListener;
import com.tapjoy.TJError;
import com.tapjoy.Tapjoy;
import com.tapjoy.TapjoyConnectFlag;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.YumiSettings;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.util.Hashtable;

import static com.yumi.android.sdk.ads.publish.enumbean.YumiGDPRStatus.NON_PERSONALIZED;
import static com.yumi.android.sdk.ads.publish.enumbean.YumiGDPRStatus.PERSONALIZED;

/**
 * Description:
 * <p>
 * Created by lgd on 2019-06-25.
 */
class TapjoyHelper {
    private static final String TAG = "TapjoyHelper";

    static void connectTapjoy(Context context, String tapjoySDKKey, final TJConnectListener tjConnectListener) {
        Hashtable<String, Object> connectFlags = new Hashtable<>();
        connectFlags.put(TapjoyConnectFlag.ENABLE_LOGGING, "true");

        Tapjoy.connect(context, tapjoySDKKey, connectFlags, new TJConnectListener() {
            @Override
            public void onConnectSuccess() {
                tjConnectListener.onConnectSuccess();
            }

            @Override
            public void onConnectFailure() {
                tjConnectListener.onConnectFailure();
            }
        });
    }

    static AdError recodeError(TJError tjError) {
        // 未找到相关错误码说明
        // 接入文档：https://dev.tapjoy.com/zh/sdk-integration/android/getting-started-guide-publishers-android/
        // API 文档：https://ltv.tapjoy.com/sdk/api/java/index.html
        ZplayDebug.d(TAG, "recodeError: " + tjError.code + ", msg: " + tjError.message);
        return new AdError(LayerErrorCode.ERROR_NO_FILL);
    }

    static void updateGDPRStatus() {
        if (YumiSettings.getGDPRStatus() == PERSONALIZED) {
            Tapjoy.subjectToGDPR(true);
            Tapjoy.setUserConsent("1");
        }

        if (YumiSettings.getGDPRStatus() == NON_PERSONALIZED) {
            Tapjoy.subjectToGDPR(true);
            Tapjoy.setUserConsent("0");
        }
    }

    static String sdkVersion() {
        return "12.3.1";
    }
}
