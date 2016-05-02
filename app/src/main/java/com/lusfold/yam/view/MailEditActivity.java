package com.lusfold.yam.view;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.lusfold.yam.R;
import com.lusfold.yam.event.UpdateMailDraftEvent;
import com.lusfold.yam.event.UpdateMailSentEvent;
import com.lusfold.yam.mail.GmailSender;
import com.lusfold.yam.mail.MailSender;
import com.lusfold.yam.repository.CacheService;
import com.lusfold.yam.repository.ContactService;
import com.lusfold.yam.repository.DeviceService;
import com.lusfold.yam.repository.bean.Account;
import com.lusfold.yam.repository.bean.Contact;
import com.lusfold.yam.repository.bean.Device;
import com.lusfold.yam.repository.bean.Mail;
import com.lusfold.yam.utils.FileUtils;
import com.lusfold.yam.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by lusfold on 5/1/16.
 */
public class MailEditActivity extends BaseActivity {
    private static final String TAG = "MailEditActivity";
    public static final String KEY_MAIL = "mail";
    public static final int Camera = 1000;
    public static final int Album = 1001;

    @BindView(R.id.sender_spinner)
    Spinner senderSpinner;
    @BindView(R.id.recipient_spinner)
    Spinner recipientSpinner;
    @BindView(R.id.time_spinner)
    Spinner timeSpinner;

    @BindView(R.id.subject_layout)
    TextInputLayout subjectLayout;
    @BindView(R.id.subject_edit)
    EditText subjectEditText;
    @BindView(R.id.content_edit)
    EditText contentEditText;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.main_layout)
    View mainLayout;
    private static final String[] TIME_TEXT = {"Right Away", "Delay 30 Minutes ", "Delay 1 Hour", "Delay 2 Hours", "Delay 1 Day"};
    private static final long[] TIME = {0, 30 * 60 * 1000, 60 * 60 * 1000, 2 * 60 * 60 * 1000, 1 * 24 * 60 * 60 * 1000};
    private List<Contact> mContacts;
    private List<Account> mAccounts;
    private MaterialDialog mDialog;
    private List<String> mAttachments;

    @Override
    public int getLayout() {
        return R.layout.activity_mail_edit;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mToolbar);
        initView();
    }

    private void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.title_mail_edit);
        loadData();
    }

    private void loadData() {
        mAttachments = new ArrayList<>();
        showLoading(true);
        mAccounts = CacheService.getAccounts();
        loadContact();
    }

    private void inflateView() {
        timeSpinner.setAdapter(new ArrayAdapter(this, R.layout.item_spinner, TIME_TEXT));
        String[] contactsArray = new String[mContacts.size()];
        int index = 0;
        for (Contact contact : mContacts) {
            contactsArray[index] = contact.getEmail();
            index++;
        }
        recipientSpinner.setAdapter(new ArrayAdapter(this, R.layout.item_spinner, contactsArray));
        index = 0;
        String[] accountArray = new String[mAccounts.size()];
        for (Account account : mAccounts) {
            accountArray[index] = account.getAddress();
            index++;
        }
        senderSpinner.setAdapter(new ArrayAdapter(this, R.layout.item_spinner, accountArray));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mail_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_send) {
            if (checkData()) {
                send();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkData() {
        String subject = subjectEditText.getText().toString();
        if (StringUtils.isEmpty(subject)) {
            subjectLayout.setError(getString(R.string.error_subject));
            return false;
        }
        subjectLayout.setError(null);
        if (mAccounts == null || mAccounts.size() == 0) {
            Toast.makeText(this, R.string.error_no_account, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mContacts == null || mContacts.size() == 0) {
            Toast.makeText(this, R.string.error_no_recipient, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void send() {
        showLoading(true);
        Mail mail = new Mail();
        List<Contact> contacts = new ArrayList<>();
        contacts.add(mContacts.get(recipientSpinner.getSelectedItemPosition()));
        mail.setReciptients(contacts);
        mail.setSubject(subjectEditText.getText().toString());
        mail.setBody(contentEditText.getText().toString());
        mail.setAttachment(mAttachments);
        mail.setSender(mAccounts.get(senderSpinner.getSelectedItemPosition()));

        long timeDelay = TIME[timeSpinner.getSelectedItemPosition()];

        switch (mail.getSender().getType()) {
            case Account.TYPE_GMAIL:
                new GmailSender().sendMail(mail, timeDelay, new MailSender.Callback() {
                    @Override
                    public void onResult(Mail mail, Throwable throwable) {
                        showLoading(false);
                        if (throwable == null) {
                            EventBus.getDefault().post(new UpdateMailSentEvent());
                            EventBus.getDefault().post(new UpdateMailDraftEvent());
                            finish();
                        } else {
                            Toast.makeText(MailEditActivity.this, R.string.error_send, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, this);
                break;
            default:
                //// TODO: 5/2/16
                break;
        }
    }

    private void loadContact() {
        Device device = DeviceService.getInstance().getDevice();
        if (!DeviceService.getInstance().isDeviceLegal(device)) {
            uploadDevice(device);
            return;
        }
        ContactService.getInstance().loadContacts(device, new FindCallback<Contact>() {
            @Override
            public void done(List<Contact> list, AVException e) {
                showLoading(false);
                if (e != null) {
                    finish();
                } else {
                    mContacts = list;
                    inflateView();
                }
            }
        });
    }

    private void uploadDevice(Device device) {
        DeviceService.getInstance().uploadDevice(device, new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e != null) {
                    showLoading(false);
                    finish();
                } else {
                    loadContact();
                }
            }
        });
    }

    private void showLoading(boolean active) {
        if (mDialog == null) {
            mDialog = new MaterialDialog.Builder(this)
                    .content(R.string.loading)
                    .progress(true, 0)
                    .cancelable(false)
                    .build();
        }
        if (active == mDialog.isShowing()) {
            return;
        }
        if (active) {
            mDialog.show();
        } else {
            mDialog.cancel();
        }
    }

    @OnClick(R.id.float_button)
    public void onFloatButtonClicked(View view) {
        onAlbum();
    }

    private void onAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, Album);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Album) {
            if (data != null && data.getData() != null) {
                String path = FileUtils.getPhotoPathFromContentUri(this, data.getData());
                mAttachments.add(path);
            }
        }
    }
}
