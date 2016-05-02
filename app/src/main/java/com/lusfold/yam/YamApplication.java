package com.lusfold.yam;

import android.app.Application;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.lusfold.androidkeyvaluestore.KVStore;
import com.lusfold.yam.repository.bean.Contact;
import com.lusfold.yam.repository.bean.Device;
import com.lusfold.yam.utils.FileUtils;

/**
 * Created by lusfold on 4/29/16.
 */
public class YamApplication extends Application {
    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FileUtils.init();
        initLeanCloud();
        initKVStore();
    }

    private void initLeanCloud() {
        AVOSCloud.initialize(this, Constants.LEANCLOUD_APP_ID, Constants.LEANCLOUD_APP_KEY);
        //setup log level
        AVOSCloud.setLogLevel(BuildConfig.DEBUG ? AVOSCloud.LOG_LEVEL_VERBOSE : AVOSCloud.LOG_LEVEL_NONE);
        //register classes
        AVObject.registerSubclass(Device.class);
        AVObject.registerSubclass(Contact.class);
        //config analytics
        AVAnalytics.setAnalyticsEnabled(!BuildConfig.DEBUG);
        AVAnalytics.enableCrashReport(this, !BuildConfig.DEBUG);
    }

    public static Application getInstance() {
        return instance;
    }

    private void initKVStore() {
        KVStore.init(this, Constants.KVSTORE_NAME);
    }
}
