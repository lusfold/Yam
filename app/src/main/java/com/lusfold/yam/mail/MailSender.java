package com.lusfold.yam.mail;

import android.content.Context;

import com.lusfold.yam.repository.bean.Mail;

/**
 * Created by lusfold on 4/30/16.
 */
public interface MailSender {

    public interface Callback {
        void onResult(Mail mail, Throwable throwable);
    }

    /**
     * send mail with a delay time.
     *
     * @param mail
     * @param time
     * @return
     */
    void sendMail(Mail mail, long time, Callback callback, Context context);
}
