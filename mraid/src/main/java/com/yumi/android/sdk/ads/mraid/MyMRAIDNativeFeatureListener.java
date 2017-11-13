package com.yumi.android.sdk.ads.mraid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.yumi.android.sdk.ads.mraid.internal.MRAIDLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mzk10 on 2017/7/6.
 */

public class MyMRAIDNativeFeatureListener implements MRAIDNativeFeatureListener {

    private Activity context;

    public MyMRAIDNativeFeatureListener(Activity context) {
        this.context = context;
    }

    @Override
    public void mraidNativeFeatureCallTel(String url) {
        MRAIDLog.d("打电话");
        if (url.startsWith("tel:")) {
            boolean isper = PackageManager.PERMISSION_GRANTED == context.checkCallingOrSelfPermission(Manifest.permission.CALL_PHONE);
            if (!isper) {
                checkSelfPermissionYumi(context, Manifest.permission.CALL_PHONE);
            } else {
                String phone = "";
                phone = url.replace("tel:", "");
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

        }
    }

    @Override
    public void mraidNativeFeatureCreateCalendarEvent(String eventJSON) {
        MRAIDLog.d("写日历");
        MRAIDLog.d(eventJSON);
    }

    @Override
    public void mraidNativeFeaturePlayVideo(String url) {

    }

    @Override
    public void mraidNativeFeatureOpenBrowser(String url) {
        context.startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(url)));
    }

    @Override
    public void mraidNativeFeatureStorePicture(String url) {

    }

    @Override
    public void mraidNativeFeatureSendSms(String url) {
        MRAIDLog.d("发短信");
        String phoneNumber = "";
        if (url.startsWith("sms:")) {
            boolean isper = PackageManager.PERMISSION_GRANTED == context.checkCallingOrSelfPermission(Manifest.permission.SEND_SMS);
            if (!isper) {
                checkSelfPermissionYumi(context, Manifest.permission.SEND_SMS);
            } else {
                phoneNumber = url.replace("sms:", "");
                //if(PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)){
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
                intent.putExtra("sms_body", "");
                context.startActivity(intent);
            }
        }

    }

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 101;

    public static void checkSelfPermissionYumi(Activity context, String permission) {
        try {
            if (android.os.Build.VERSION.SDK_INT < 23) {
                return;
            }
            List<String> denyPermissions = findDeniedPermissions(context, permission);

            if (denyPermissions != null && denyPermissions.size() > 0) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_CONTACTS)) {
                    ActivityCompat.requestPermissions(context, (String[]) denyPermissions.toArray(new String[denyPermissions.size()]), MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }

        } catch (Exception e) {
        }
    }

    private static List<String> findDeniedPermissions(Activity context, String... permission) {
        try {
            List<String> denyPermissions = new ArrayList<>();
            for (String value : permission) {
                if (ContextCompat.checkSelfPermission(context, value) != PackageManager.PERMISSION_GRANTED) {
                    denyPermissions.add(value);
                }
            }
            return denyPermissions;
        } catch (Exception e) {
        }
        return null;
    }


}
