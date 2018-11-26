package com.yumi.android.sdk.ads.adapter.baidu;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.baidu.mobad.video.XAdManager;
import com.baidu.mobads.interfaces.IXAdConstants4PDK;
import com.baidu.mobads.rewardvideo.RewardVideoAd;
import com.yumi.android.sdk.ads.beans.YumiProviderBean;
import com.yumi.android.sdk.ads.publish.adapter.YumiCustomerMediaAdapter;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

import java.io.File;

public class BaiduMediaAdapter extends YumiCustomerMediaAdapter {
    private static final String TAG = "BaiduMediaAdapter";
    private RewardVideoAd rewardVideoAd;
    private RewardVideoAd.RewardVideoAdListener rewardVideoAdListener;
    private boolean adLoaded = false;
    private static final int REQUEST_NEXT_MEDIA = 0x001;

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_NEXT_MEDIA:
                    if (rewardVideoAd != null && rewardVideoAdListener != null) {
                        ZplayDebug.d(TAG, "baidu media Video REQUEST_NEXT_MEDIA ", onoff);
                        layerNWRequestReport();
                        adLoaded = false;
                        deleteBaiDuFile(newDeleteCallback());
                    }
                    break;
                default:
                    break;
            }
        }
    };

    protected BaiduMediaAdapter(Activity activity, YumiProviderBean yumiProviderBean) {
        super(activity, yumiProviderBean);

    }

    private DeleteCallback newDeleteCallback() {
        return new DeleteCallback() {
            @Override
            public void onDeleted() {
                if (rewardVideoAd != null && rewardVideoAdListener != null) {
                    rewardVideoAd.load();
                }
            }
        };
    }

    @Override
    protected void onPrepareMedia() {
        adLoaded = false;
        deleteBaiDuFile(newDeleteCallback());
    }

    @Override
    protected void onShowMedia() {
        if (adLoaded && rewardVideoAd != null) {
            rewardVideoAd.show();
        }
    }

    @Override
    protected boolean isMediaReady() {
        return adLoaded;
    }

    @Override
    protected void init() {
        ZplayDebug.i(TAG, "baidu key1 : " + getProvider().getKey1(), onoff);
        ZplayDebug.i(TAG, "baidu key2 : " + getProvider().getKey2(), onoff);
        createrListener();
        XAdManager.getInstance(getActivity()).setAppSid(getProvider().getKey1());
        rewardVideoAd = new RewardVideoAd(getActivity(), getProvider().getKey2(), rewardVideoAdListener);
    }

    private void createrListener() {
        rewardVideoAdListener = new RewardVideoAd.RewardVideoAdListener() {
            @Override
            public void onAdShow() {
                ZplayDebug.i(TAG, "baidu media onAdShow", onoff);
                layerExposure();
                layerMediaStart();
            }

            @Override
            public void onVideoDownloadSuccess() {
                ZplayDebug.i(TAG, "baidu media onVideoDownloadSuccess", onoff);
                adLoaded = true;
                layerPrepared();
            }

            @Override
            public void onVideoDownloadFailed() {
                ZplayDebug.i(TAG, "baidu media onVideoDownloadFailed", onoff);
                layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
                requestAD(getProvider().getNextRequestInterval());
            }

            @Override
            public void onAdClick() {
                ZplayDebug.i(TAG, "baidu media onAdClick", onoff);
                layerClicked();
            }

            @Override
            public void onAdClose() {
                ZplayDebug.i(TAG, "baidu media onAdClose", onoff);
                adLoaded = false;
                layerMediaEnd();
                ZplayDebug.d(TAG, "baidu media get reward", onoff);
                layerIncentived();
                layerClosed();
                requestAD(3);
            }

            @Override
            public void onAdFailed(String s) {
                ZplayDebug.i(TAG, "baidu media onAdFailed:" + s, onoff);
                layerPreparedFailed(LayerErrorCode.ERROR_NO_FILL);
                requestAD(getProvider().getNextRequestInterval());
            }
        };

    }

    private void requestAD(int delaySecond) {
        try {
            if (!mHandler.hasMessages(REQUEST_NEXT_MEDIA)) {
                ZplayDebug.d(TAG, "baidu media Video requestAD delaySecond" + delaySecond, onoff);
                mHandler.sendEmptyMessageDelayed(REQUEST_NEXT_MEDIA, delaySecond * 1000);
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "baidu media requestAD error ", e, onoff);
        }
    }

    @Override
    protected void callOnActivityDestroy() {
        try {
            if (mHandler != null && mHandler.hasMessages(REQUEST_NEXT_MEDIA)) {
                mHandler.removeMessages(REQUEST_NEXT_MEDIA);
            }
        } catch (Exception e) {
            ZplayDebug.e(TAG, "baidu media callOnActivityDestroy error ", e, onoff);
        }
    }

    @Override
    public void onActivityPause() {
        if (rewardVideoAd != null) {
            rewardVideoAd.setActivityState(IXAdConstants4PDK.ActivityState.PAUSE);
        }
    }

    @Override
    public void onActivityResume() {
        if (rewardVideoAd != null) {
            rewardVideoAd.setActivityState(IXAdConstants4PDK.ActivityState.RESUME);
        }
    }


    private static void deleteBaiDuFile(final DeleteCallback cb) {
        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    File file = new File(getSDPath() + "/bddownload");
                    if (!file.exists()) {
                        ZplayDebug.i(TAG, "baidu file not exists :" + getSDPath() + "/bddownload", onoff);
                        return null;
                    }
                    recursionDeleteFile(file);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    if (cb != null) {
                        cb.onDeleted();
                    }
                }
            }.execute();

        } catch (Exception e) {
            ZplayDebug.e(TAG, "baidu deleteBaiDuFile error", e, onoff);
        }
    }

    interface DeleteCallback {
        void onDeleted();
    }

    private static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        return sdDir == null ? "" : sdDir.toString();
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file
     */
    private static void recursionDeleteFile(final File file) {
        ZplayDebug.i(TAG, "baidu RecursionDeleteFile  file:" + file.toString(), onoff);
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                recursionDeleteFile(f);
            }
            file.delete();
        }
    }
}
