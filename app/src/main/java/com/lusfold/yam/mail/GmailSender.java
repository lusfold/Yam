package com.lusfold.yam.mail;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.lusfold.yam.mail.core.JSSEProvider;
import com.lusfold.yam.repository.CacheService;
import com.lusfold.yam.repository.bean.Contact;
import com.lusfold.yam.repository.bean.Mail;
import com.lusfold.yam.utils.StringUtils;

import java.io.File;
import java.security.Security;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;


/**
 * Created by lusfold on 4/30/16.
 */
public class GmailSender implements MailSender {
    public static final String MAIL_HOST = "smtp.gmail.com";


    static {
        Security.addProvider(new JSSEProvider());
    }

    private Properties getSessionProps() {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", MAIL_HOST);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");
        return props;
    }

    private String getRecipients(List<Contact> contacts) {
        if (contacts == null) {
            return null;
        }

        if (contacts.size() == 1) {
            return contacts.get(0).getEmail();
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0, count = contacts.size(); i < count; i++) {
            stringBuilder.append(contacts.get(i).getEmail())
                    .append(",");
        }
        String recipients = stringBuilder.toString();
        return recipients.substring(0, recipients.length() - 2);
    }

    private Authenticator getAuthenticator(final String user, final String password) {
        return new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        };
    }

    @Override
    public void sendMail(final Mail mail, final long time, final Callback callback, Context context) {
        if (time > 0) {
            mail.setStatus(Mail.DRAFT);
            mail.setTimeToSend(System.currentTimeMillis() + time);
            CacheService.saveOrUpdateMailDraft(mail);
            Intent intent = new Intent(context,MailReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(
                    context, 0, intent, 0);
            // Schedule the alarm!
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP,mail.getTimeToSend(), sender);
            callback.onResult(mail, null);
        }
        new Thread(){
            @Override
            public void run() {
                Session session = Session.getDefaultInstance(getSessionProps(), getAuthenticator(mail.getSender().getAddress(), mail.getSender().getPassword()));
                Multipart _multipart = new MimeMultipart();
                BodyPart _contentPart = new MimeBodyPart();
                MimeMessage message = new MimeMessage(session);
                try {
                    _contentPart.setContent(mail.getBody(), "text/plain");
                    _multipart.addBodyPart(_contentPart);

                    List<String> attachments = mail.getAttachment();
                    if (attachments != null) {
                        for (int i = 0, num = attachments.size(); i < num; i++) {
                            String attachmentFilePath = attachments.get(i);
                            if (!StringUtils.isEmpty(attachmentFilePath)) {
                                File attachmentFile = new File(attachmentFilePath);
                                if (attachmentFile != null && attachmentFile.exists()) {
                                    BodyPart attachmentBodyPart = new MimeBodyPart();
                                    DataSource source = new FileDataSource(attachmentFile);
                                    attachmentBodyPart.setDataHandler(new DataHandler(source));
                                    attachmentBodyPart.setFileName(MimeUtility.encodeWord(attachmentFile.getName()));
                                    _multipart.addBodyPart(attachmentBodyPart);
                                }
                            }
                        }
                    }

                    message.setContent(_multipart);
                    message.setSender(new InternetAddress(mail.getSender().getAddress()));
                    message.setSubject(mail.getSubject());

                    String recipients = getRecipients(mail.getReciptients());
                    if (recipients.indexOf(',') > 0)
                        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
                    else
                        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
                    Transport.send(message);
                    mail.setStatus(Mail.SENT);
                    CacheService.delete(mail);
                    mail.setKey(null);
                    CacheService.saveOrUpdateMailSent(mail);
                    if (callback != null) {
                        callback.onResult(mail, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    CacheService.delete(mail);
                    mail.setKey(null);
                    mail.setStatus(Mail.FAIL);
                    CacheService.saveOrUpdateMailSent(mail);
                    callback.onResult(mail, e);
                }
            }
        }.start();
    }
}
