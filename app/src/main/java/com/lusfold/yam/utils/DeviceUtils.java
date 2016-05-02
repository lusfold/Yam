package com.lusfold.yam.utils;

import android.os.Build;

import com.lusfold.yam.YamApplication;

/**
 * Created by lusfold on 4/29/16.
 */
public class DeviceUtils {
    public static String getUDID() {
        return DeviceUuidFactory.getDeviceUdid(YamApplication.getInstance()).toString();
    }

    public static String getOS() {
        return new StringBuilder().append(android.os.Build.MODEL)
                .append("_")
                .append(Build.VERSION.SDK_INT)
                .append("_")
                .append(android.os.Build.VERSION.RELEASE)
                .toString();
    }
}
