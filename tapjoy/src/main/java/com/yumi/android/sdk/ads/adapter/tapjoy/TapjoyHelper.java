package com.yumi.android.sdk.ads.adapter.tapjoy;

import android.content.Context;

import com.tapjoy.TJConnectListener;
import com.tapjoy.TJError;
import com.tapjoy.Tapjoy;
import com.tapjoy.TapjoyConnectFlag;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

import java.util.Hashtable;

/**
 * Description:
 * <p>
 * Created by lgd on 2019-06-25.
 */
public class TapjoyHelper {
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
        return new AdError(LayerErrorCode.ERROR_NO_FILL);
    }
}
