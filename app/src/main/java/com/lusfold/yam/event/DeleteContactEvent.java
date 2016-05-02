package com.lusfold.yam.event;

import com.lusfold.yam.repository.bean.Contact;

/**
 * Created by lusfold on 5/2/16.
 */
public class DeleteContactEvent extends BaseEvent {
    private Contact contact;

    public DeleteContactEvent(Contact contact){
        this.contact = contact;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
