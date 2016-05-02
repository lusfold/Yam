package com.lusfold.yam.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by lusfold on 5/1/16.
 */
public class NetworkUtils {

    /**
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        return ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo() != null;
    }
}
