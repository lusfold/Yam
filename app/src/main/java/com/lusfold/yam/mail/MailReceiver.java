package com.lusfold.yam.mail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lusfold.yam.repository.CacheService;
import com.lusfold.yam.repository.bean.Mail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lusfold on 5/2/16.
 */
public class MailReceiver extends BroadcastReceiver {
    //due to after API 19 the AlarmManager is not precise.
    private static final long MAX_TIME_OFFSET = 2 * 60 * 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        List<Mail> toSend = CacheService.getEmailsDraft();
        List<Mail> canSend = new ArrayList<>();
        long currTime = System.currentTimeMillis();
        for (int i = 0,count = toSend.size();i< count;i++){
            Mail mail = toSend.get(i);
            if (mail.getStatus() == Mail.DRAFT && (mail.getTimeToSend() - currTime < MAX_TIME_OFFSET || mail.getTimeToSend() - currTime > -MAX_TIME_OFFSET)) {
                canSend.add(mail);
            }
        }
        if (canSend.size() == 0){
            return;
        }
        GmailSender gmailSender = new GmailSender();
        for (int i = 0,count = canSend.size();i<count;i++){
            gmailSender.sendMail(canSend.get(i), 0, new MailSender.Callback() {
                @Override
                public void onResult(Mail mail, Throwable throwable) {
                    CacheService.delete(mail);
                    mail.setStatus(throwable == null?Mail.SENT:Mail.FAIL);
                    CacheService.saveOrUpdateMailSent(mail);
                }
            },context);
        }
    }
}
