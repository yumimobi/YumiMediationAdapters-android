package com.yumi.android.sdk.ads.adapter.inmobi;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
public class InmobUtil {

    public static LayerErrorCode recodeError(InMobiAdRequestStatus inMobiAdRequestStatus) {
        LayerErrorCode result;
        if (inMobiAdRequestStatus == null) {
            result = LayerErrorCode.ERROR_INTERNAL;
            result.setExtraMsg("Inmobi errorMsg: null");
            return result;
        }
        InMobiAdRequestStatus.StatusCode code = inMobiAdRequestStatus.getStatusCode();
        if (code == null) {
            result = LayerErrorCode.ERROR_INTERNAL;
            result.setExtraMsg("Inmobi errorMsg: " + inMobiAdRequestStatus.getMessage());
            return result;
        }

        switch (code) {
            case NO_FILL:
                result = LayerErrorCode.ERROR_NO_FILL;
                break;
            case REQUEST_INVALID:
                result = LayerErrorCode.ERROR_INVALID;
                break;
            case NETWORK_UNREACHABLE:
                result = LayerErrorCode.ERROR_NETWORK_ERROR;
                break;
            default:
                result = LayerErrorCode.ERROR_INTERNAL;
                break;
        }
        result.setExtraMsg("Inmobi errorMsg: " + inMobiAdRequestStatus.getMessage());
        return result;
    }

    public static LayerErrorCode recodeError(LayerErrorCode errorCode) {
        errorCode.setExtraMsg("Inmobi errorMsg: ");
        return errorCode;
    }

}
