package inmobi;

import android.app.Activity;

import com.inmobi.ads.InMobiAdRequestStatus.StatusCode;
import com.inmobi.sdk.InMobiSdk;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

public class InmobiExtraHolder {

	static boolean isInitlalize = false;

	static void initInmobiSDK(Activity activity, String appid) {
		if (!isInitlalize) {
			InMobiSdk.init(activity, appid);
			InMobiSdk.setLogLevel(InMobiSdk.LogLevel.DEBUG);
			isInitlalize = true;
		}
	}

	static LayerErrorCode decodeError(StatusCode code){
		if (code == StatusCode.NO_FILL) {
			return LayerErrorCode.ERROR_NO_FILL;
		}
		if (code == StatusCode.REQUEST_INVALID) {
			return LayerErrorCode.ERROR_INVALID;
		}
		if (code == StatusCode.NETWORK_UNREACHABLE) {
			return LayerErrorCode.ERROR_NETWORK_ERROR;
		}
		return LayerErrorCode.ERROR_INTERNAL;
	} 
	

	static void onDestroy() {
		isInitlalize = false;
	}

}
