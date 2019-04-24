package com.yumi.android.sdk.ads.adapter.chartboost;

import com.chartboost.sdk.Model.CBError;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_INTERNAL;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
public class ChartboostUtil {
    static AdError recodeError(CBError.CBImpressionError chartBoostError) {
        AdError result;
        if (chartBoostError == null) {
            result = new AdError(ERROR_INTERNAL);
            result.setErrorMessage("ChartBoost errorMes: null");
            return result;
        }

        LayerErrorCode errCode;
        switch (chartBoostError) {
            case INTERNAL:
                errCode = ERROR_INTERNAL;
                break;
            case NO_AD_FOUND:
                errCode = LayerErrorCode.ERROR_NO_FILL;
                break;
            case INVALID_LOCATION:
                errCode = LayerErrorCode.ERROR_INVALID;
                break;
            case NETWORK_FAILURE:
                errCode = LayerErrorCode.ERROR_NETWORK_ERROR;
                break;
            default:
                errCode = ERROR_INTERNAL;
                break;
        }
        result = new AdError(errCode);
        result.setErrorMessage("ChartBoost errorMes: " + chartBoostError.toString());
        return result;
    }
}
