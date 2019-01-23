package com.yumi.android.sdk.ads.adapter.chartboost;

import com.chartboost.sdk.Model.CBError;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
public class ChartboostUtil {
    static LayerErrorCode recodeError(CBError.CBImpressionError chartBoostError) {
        LayerErrorCode result;
        if(chartBoostError == null){
            result = LayerErrorCode.ERROR_INTERNAL;
            result.setExtraMsg("ChartBoost errorMes: null");
            return result;
        }

        switch (chartBoostError) {
            case INTERNAL:
                result = LayerErrorCode.ERROR_INTERNAL;
                break;
            case NO_AD_FOUND:
                result = LayerErrorCode.ERROR_NO_FILL;
                break;
            case INVALID_LOCATION:
                result = LayerErrorCode.ERROR_INVALID;
                break;
            case NETWORK_FAILURE:
                result = LayerErrorCode.ERROR_NETWORK_ERROR;
                break;
            default:
                result = LayerErrorCode.ERROR_INTERNAL;
                break;
        }
        result.setExtraMsg("ChartBoost errorMes: " + chartBoostError.toString());
        return result;
    }
}
