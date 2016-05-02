package com.lusfold.yam.repository;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.lusfold.yam.repository.bean.Contact;
import com.lusfold.yam.repository.bean.Device;

/**
 * Created by lusfold on 4/29/16.
 */
public class ContactService {
    private static ContactService instance;

    private ContactService() {
    }

    /**
     * @return
     */
    public static ContactService getInstance() {
        if (instance == null) {
            synchronized (ContactService.class) {
                if (instance == null) {
                    instance = new ContactService();
                }
            }
        }
        return instance;
    }

    /**
     * @param device
     * @return
     */
    public void loadContacts(final Device device, FindCallback<Contact> callback) {
        AVQuery<Contact> contactAVQuery = new AVQuery<>("Contact");
        contactAVQuery.whereEqualTo(Contact.FIELD_DELETE, Contact.DELETE_NO);
        contactAVQuery.whereEqualTo(Contact.FIELD_DEVICE, AVObject.createWithoutData("Device", device.getObjectId()));
        contactAVQuery.findInBackground(callback);
    }

    /**
     * @param contact
     * @return
     */
    public void deleteContact(final Contact contact, SaveCallback callback) {
        contact.setDelete(true);
        contact.saveInBackground(callback);
    }

    /**
     * @param contact
     * @return
     */

    public void addContact(final Contact contact, SaveCallback callback) {
        contact.saveInBackground(callback);
    }
}
