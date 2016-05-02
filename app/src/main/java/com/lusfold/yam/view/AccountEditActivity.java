package com.lusfold.yam.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.lusfold.yam.R;
import com.lusfold.yam.event.UpdateAccountEvent;
import com.lusfold.yam.repository.CacheService;
import com.lusfold.yam.repository.bean.Account;
import com.lusfold.yam.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

/**
 * Created by lusfold on 5/1/16.
 */
public class AccountEditActivity extends BaseActivity {
    private static final String TAG = "AccountEditActivity";
    public static final String KEY_ACCOUNT = "account";
    public static final int Camera = 1000;
    public static final int Album = 1001;

    @BindView(R.id.email_layout)
    TextInputLayout emailImputLayout;
    @BindView(R.id.email_edit)
    EditText emailEditText;
    @BindView(R.id.password_layout)
    TextInputLayout passwordInputLayout;
    @BindView(R.id.password_edit)
    EditText passwordEditText;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.main_layout)
    View mainLayout;

    private Account mAccount;

    @Override
    public int getLayout() {
        return R.layout.activity_account_edit;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mToolbar);
        mAccount = (Account) getIntent().getSerializableExtra(KEY_ACCOUNT);
        if (mAccount == null) {
            mAccount = new Account();
        }
        initView();
    }

    private void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.title_account_edit);
        emailEditText.setText(mAccount.getAddress());
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
        String password = passwordEditText.getText().toString();
        if (StringUtils.isEmpty(email) || !StringUtils.validateEmail(email)) {
            emailImputLayout.setError(getString(R.string.error_email));
            return false;
        }
        emailImputLayout.setError(null);

        if (StringUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.error_password));
            return false;
        }
        passwordInputLayout.setError(null);
        mAccount.setAddress(email);
        mAccount.setPassword(password);
        return true;
    }

    private void saveContact() {
        CacheService.saveOrUpdateAccount(mAccount);
        EventBus.getDefault().post(new UpdateAccountEvent());
        finish();
    }
}
