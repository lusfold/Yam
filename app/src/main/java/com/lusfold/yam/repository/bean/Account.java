package com.lusfold.yam.repository.bean;

import com.lusfold.yam.repository.cache.Cacheable;

import java.io.Serializable;

/**
 * Created by lusfold on 4/30/16.
 */
public class Account extends Cacheable implements Serializable {
    public static final int TYPE_GMAIL = 0;

    private int type;
    private String password;
    private String address;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
