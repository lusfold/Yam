package com.lusfold.yam.repository.bean;

import com.lusfold.yam.repository.cache.Cacheable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lusfold on 4/30/16.
 */
public class Mail extends Cacheable implements Serializable {
    public static final int DRAFT = 0;
    public static final int SENT = 1;
    public static final int FAIL = 2;

    private int status;
    private String subject;
    private String body;
    private List<Contact> reciptients;
    private Account sender;
    private List<String> attachment;
    private long timeToSend;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<Contact> getReciptients() {
        return reciptients;
    }

    public void setReciptients(List<Contact> reciptients) {
        this.reciptients = reciptients;
    }

    public Account getSender() {
        return sender;
    }

    public void setSender(Account sender) {
        this.sender = sender;
    }

    public List<String> getAttachment() {
        return attachment;
    }

    public void setAttachment(List<String> attachment) {
        this.attachment = attachment;
    }


    public long getTimeToSend() {
        return timeToSend;
    }

    public void setTimeToSend(long timeToSend) {
        this.timeToSend = timeToSend;
    }
}
