package com.lusfold.yam.repository.bean;

import android.os.Parcel;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;

import java.io.Serializable;

/**
 * Created by lusfold on 4/29/16.
 */
@AVClassName("Contact")
public class Contact extends AVObject implements Serializable {
    public static final Creator CREATOR = AVObjectCreator.instance;
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_AVATAR = "avatar";
    public static final String FIELD_NICK = "nick";
    public static final String FIELD_DEVICE = "device";
    public static final String FIELD_DELETE = "delete";

    public static final int DELETE_YES = 1;
    public static final int DELETE_NO = 0;

    public Contact() {
        super();
    }

    public Contact(Parcel in) {
        super(in);
    }

    public String getEmail() {
        return getString(FIELD_EMAIL);
    }

    public void setEmail(String email) {
        put(FIELD_EMAIL, email);
    }

    public AVFile getAvatar() {
        return getAVFile(FIELD_AVATAR);
    }

    public String getAvatarUrl() {
        AVFile avatar = getAVFile(FIELD_AVATAR);
        return avatar == null ? null : avatar.getUrl();
    }

    public void setAvatar(AVFile file) {
        put(FIELD_AVATAR, file);
    }

    public String getNick() {
        return getString(FIELD_NICK);
    }

    public void setNick(String nick) {
        put(FIELD_NICK, nick);
    }

    public Device getDevice() {
        try {
            return getAVObject(FIELD_DEVICE, Device.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setDevice(Device device) {
        put(FIELD_DEVICE, device);
    }

    public boolean isDeleted() {
        return getInt(FIELD_DELETE) == DELETE_YES;
    }

    public void setDelete(boolean delete) {
        put(FIELD_DELETE, delete ? DELETE_YES : DELETE_NO);
    }

}

