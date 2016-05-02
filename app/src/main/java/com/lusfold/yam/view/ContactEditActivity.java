package com.lusfold.yam.view;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.lusfold.yam.R;
import com.lusfold.yam.event.AddContactEvent;
import com.lusfold.yam.repository.ContactService;
import com.lusfold.yam.repository.DeviceService;
import com.lusfold.yam.repository.bean.Contact;
import com.lusfold.yam.repository.bean.Device;
import com.lusfold.yam.utils.FileUtils;
import com.lusfold.yam.utils.StringUtils;
import com.lusfold.yam.view.widget.GlideCircleTransform;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by lusfold on 5/1/16.
 */
public class ContactEditActivity extends BaseActivity {
    private static final String TAG = "ContactEditActivity";
    public static final String KEY_CONTACT = "contact";
    public static final int Camera = 1000;
    public static final int Album = 1001;

    @BindView(R.id.avatar)
    ImageView avatarImageView;
    @BindView(R.id.email_layout)
    TextInputLayout emailImputLayout;
    @BindView(R.id.email_edit)
    EditText emailEditText;
    @BindView(R.id.nickname_layout)
    TextInputLayout nicknameInputLayout;
    @BindView(R.id.nickname_edit)
    EditText nicknameEditText;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.main_layout)
    View mainLayout;

    private Contact mContact;
    private String avatarFilePath;
    private MaterialDialog mDialog;

    @Override
    public int getLayout() {
        return R.layout.activity_contact_edit;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mToolbar);
        mContact = (Contact) getIntent().getSerializableExtra(KEY_CONTACT);
        if (mContact == null) {
            mContact = new Contact();
        }
        avatarFilePath = FileUtils.AVATAR_PATH + System.currentTimeMillis() + ".jpg";
        initView();
    }

    private void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.title_contact_edit);
        Glide.with(this)
                .load(mContact.getAvatarUrl())
                .placeholder(R.mipmap.default_avatar)
                .error(R.mipmap.default_avatar)
                .transform(new GlideCircleTransform(this))
                .into(avatarImageView);
        emailEditText.setText(mContact.getEmail());
        nicknameEditText.setText(mContact.getNick());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            if (checkData()) {
                saveContact();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkData() {
        String email = emailEditText.getEditableText().toString();
        String nickname = nicknameEditText.getText().toString();
        if (StringUtils.isEmpty(email) || !StringUtils.validateEmail(email)) {
            emailImputLayout.setError(getString(R.string.error_email));
            return false;
        }
        emailImputLayout.setError(null);

        if (StringUtils.isEmpty(nickname)) {
            nicknameEditText.setError(getString(R.string.error_nickname));
            return false;
        }
        nicknameInputLayout.setError(null);
        mContact.setEmail(email);
        mContact.setNick(nickname);
        return true;
    }

    private void saveContact() {
        showLoading(true);
        Device device = DeviceService.getInstance().getDevice();
        if (!DeviceService.getInstance().isDeviceLegal(device)) {
            uploadDevice(device);
            return;
        }
        mContact.setDevice(device);
        ContactService.getInstance().addContact(mContact, new SaveCallback() {
            @Override
            public void done(AVException e) {
                showLoading(false);
                if (e!=null){
                    Snackbar.make(findViewById(R.id.main_layout), R.string.error_save, Snackbar.LENGTH_SHORT).show();
                }else{
                    EventBus.getDefault().post(new AddContactEvent());
                    finish();
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
                    Snackbar.make(mainLayout, R.string.error_save, Snackbar.LENGTH_SHORT).show();
                } else {
                    saveContact();
                }
            }
        });
    }

    @OnClick({R.id.avatar})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.avatar:
                showPickPhotoDialog();
                break;
        }
    }

    private void showPickPhotoDialog() {
        new MaterialDialog.Builder(this)
                .backgroundColor(getResources().getColor(R.color.white))
                .itemsColorRes(R.color.colorPrimary)
                .items(new String[]{getString(R.string.pick_from_album), getString(R.string.camera)})
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                onAlbum();
                                break;
                            case 1:
                                onCamera();
                                break;
                        }
                    }
                })
                .show();
    }

    private void onAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, Album);
    }

    private void onCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(avatarFilePath)));
            startActivityForResult(intent, Camera);
        } catch (ActivityNotFoundException e) {
            Snackbar.make(findViewById(R.id.main_layout), R.string.error_can_not_open_camera, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Camera) {
            UCrop.of(Uri.fromFile(new File(avatarFilePath)), Uri.fromFile(new File(avatarFilePath)))
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(256, 256)
                    .start(this);
        } else if (requestCode == Album) {
            UCrop.of(data.getData(), Uri.fromFile(new File(avatarFilePath)))
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(256, 256)
                    .start(this);

        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            File file = new File(avatarFilePath);
            try {
                AVFile avFile = AVFile.withFile(file.getName(), file);
                mContact.setAvatar(avFile);
                Glide.with(this)
                        .load(file)
                        .placeholder(R.mipmap.default_avatar)
                        .error(R.mipmap.default_avatar)
                        .transform(new GlideCircleTransform(this))
                        .into(avatarImageView);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
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
}
