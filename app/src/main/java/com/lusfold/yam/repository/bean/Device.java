package com.lusfold.yam.repository.bean;

import android.os.Parcel;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

/**
 * Created by lusfold on 4/29/16.
 */
@AVClassName("Device")
public class Device extends AVObject {
    public static final Creator CREATOR = AVObjectCreator.instance;

    public static final String FIELD_UDID = "udid";
    public static final String FIELD_OS = "os";

    public Device() {
        super();
    }

    public Device(Parcel in) {
        super(in);
    }

    public String getUdid() {
        return getString(FIELD_UDID);
    }

    public void setUdid(String udid) {
        put(FIELD_UDID, udid);
    }

    public String getOs() {
        return getString(FIELD_OS);
    }

    public void setOs(String os) {
        put(FIELD_OS, os);
    }
}
