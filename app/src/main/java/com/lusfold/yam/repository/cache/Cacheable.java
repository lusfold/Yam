package com.lusfold.yam.repository.cache;

import java.io.Serializable;

/**
 * Created by lusfold on 5/2/16.
 */
public abstract class Cacheable implements Serializable{
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
