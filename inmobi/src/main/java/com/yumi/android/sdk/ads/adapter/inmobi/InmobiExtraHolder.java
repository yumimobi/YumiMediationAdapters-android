package com.yumi.android.sdk.ads.adapter.inmobi;

import android.app.Activity;

import com.inmobi.sdk.InMobiSdk;
import com.yumi.android.sdk.ads.publish.YumiSettings;
import com.yumi.android.sdk.ads.publish.enumbean.YumiGDPRStatus;

import org.json.JSONException;
import org.json.JSONObject;

public class InmobiExtraHolder {

	static boolean isInitlalize = false;

	static void initInmobiSDK(Activity activity, String appid) {
		if (!isInitlalize) {


			if(YumiSettings.getGDPRStatus() == YumiGDPRStatus.UNKNOWN) {
				InMobiSdk.init(activity, appid);
			}else{

				boolean isConsent = YumiSettings.getGDPRStatus() == YumiGDPRStatus.PERSONALIZED;

				// https://support.inmobi.com/monetize/android-guidelines/
				JSONObject consentObject = new JSONObject();
				try {
                    // Provide 0 if GDPR is not applicable and 1 if applicable
					consentObject.put("gdpr", isConsent ? "1" : "0");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				InMobiSdk.init(activity, appid, consentObject);
			}
			InMobiSdk.setLogLevel(InMobiSdk.LogLevel.DEBUG);
			isInitlalize = true;
		}
	}

	static void onDestroy() {
		isInitlalize = false;
	}

}
