package com.lusfold.yam.repository;

import com.alibaba.fastjson.JSON;
import com.lusfold.androidkeyvaluestore.KVStore;
import com.lusfold.yam.repository.bean.Account;
import com.lusfold.yam.repository.bean.Mail;
import com.lusfold.yam.repository.cache.Cacheable;
import com.lusfold.yam.utils.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by lusfold on 5/2/16.
 */
public class CacheService {
    private static final String ACCOUNT_PREFIX = "account";
    private static final String EMAIL_SENT_PREFIX = "email_sent";
    private static final String EMAIL_DRAFT_PREFIX = "email_draft";

    public static List<Account> getAccounts() {
        Map<String, String> data = KVStore.getInstance().getByPrefix(ACCOUNT_PREFIX);
        Iterator<String> iterator = data.values().iterator();
        List<Account> accounts = new ArrayList<>();
        while (iterator.hasNext()) {
            String objStr = iterator.next();
            Account account = JSON.parseObject(objStr, Account.class);
            accounts.add(account);
        }
        return accounts;
    }

    public static void saveOrUpdateAccount(Account account) {
        if (StringUtils.isEmpty(account.getKey())) {
            account.setKey(ACCOUNT_PREFIX + "_" + System.currentTimeMillis());
        }
        KVStore.getInstance().insertOrUpdate(account.getKey(), JSON.toJSONString(account));
    }

    public static List<Mail> getEmailsSent() {
        Map<String, String> data = KVStore.getInstance().getByPrefix(EMAIL_SENT_PREFIX);
        Iterator<String> iterator = data.values().iterator();
        List<Mail> mails = new ArrayList<>();
        while (iterator.hasNext()) {
            String objStr = iterator.next();
            Mail mail = JSON.parseObject(objStr, Mail.class);
            mails.add(mail);
        }
        return mails;
    }

    public static void saveOrUpdateMailSent(Mail mail) {
        if (StringUtils.isEmpty(mail.getKey())) {
            mail.setKey(EMAIL_SENT_PREFIX + "_" + System.currentTimeMillis());
        }
        KVStore.getInstance().insertOrUpdate(mail.getKey(), JSON.toJSONString(mail));
    }

    public static List<Mail> getEmailsDraft() {
        Map<String, String> data = KVStore.getInstance().getByPrefix(EMAIL_DRAFT_PREFIX);
        Iterator<String> iterator = data.values().iterator();
        List<Mail> mails = new ArrayList<>();
        while (iterator.hasNext()) {
            String objStr = iterator.next();
            Mail mail = JSON.parseObject(objStr, Mail.class);
            mails.add(mail);
        }
        return mails;
    }

    public static void saveOrUpdateMailDraft(Mail mail) {
        if (StringUtils.isEmpty(mail.getKey())) {
            mail.setKey(EMAIL_DRAFT_PREFIX + "_" + System.currentTimeMillis());
        }
        KVStore.getInstance().insertOrUpdate(mail.getKey(), JSON.toJSONString(mail));
    }

    public static void delete(Cacheable cacheable) {
        KVStore.getInstance().delete(cacheable.getKey());
    }
}
