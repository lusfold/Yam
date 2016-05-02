package com.lusfold.yam.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lusfold.yam.R;
import com.lusfold.yam.view.adapter.MainFragmentAdapter;
import com.lusfold.yam.view.widget.MainTabSwitcher;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private final Class[] mFragmentClasses = {MailFragment.class, DraftFragment.class, ContactFragment.class, AccountFragment.class};
    @BindView(R.id.mail)
    ImageView mail;
    @BindView(R.id.draft)
    ImageView draft;
    @BindView(R.id.contact)
    ImageView contact;
    @BindView(R.id.user)
    ImageView user;
    @BindView(R.id.container)
    ViewPager mPager;
    @BindView(R.id.float_button)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.main_layout)
    RelativeLayout mainLayout;

    private MainTabSwitcher mSwitcher;
    private MainFragmentAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    private void initView() {
        mAdapter = new MainFragmentAdapter(getSupportFragmentManager(), mFragmentClasses);
        mPager.setAdapter(mAdapter);
        mPager.setOffscreenPageLimit(mFragmentClasses.length);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mSwitcher.switchTo(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        View[] tabs = {mail, draft, contact, user};
        mSwitcher = new MainTabSwitcher(tabs);
        mSwitcher.switchTo(0);
    }

    @OnClick({R.id.mail_layout, R.id.draft_layout, R.id.contact_layout, R.id.user_layout})
    public void switchTo(View view) {
        int toPosition = -1;
        switch (view.getId()) {
            case R.id.user_layout:
                toPosition++;
            case R.id.contact_layout:
                toPosition++;
            case R.id.draft_layout:
                toPosition++;
            case R.id.mail_layout:
                toPosition++;
        }
        mSwitcher.switchTo(toPosition);
        mPager.setCurrentItem(toPosition, false);
    }

    @OnClick({})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.float_button:
                onFloatActionButtonClicked();
                //                Mail mail = new Mail();
                //
                //                Account account = new Account();
                //                account.setAddress("lusfold@gmail.com");
                //                account.setPassword("Sr19901221");
                //                account.setType(Account.TYPE_GMAIL);
                //                mail.setSender(account);
                //                mail.setBody("this is a body");
                //                mail.setSubject("this is a subject");
                //                List<Contact> contacts = new ArrayList<>();
                //                Contact contact = new Contact();
                //                contact.setEmail("363200176@qq.com");
                //                contacts.add(contact);
                //                mail.setReciptients(contacts);
                //
                //                new GmailSender().sendMail(mail,0)
                //                        .subscribeOn(Schedulers.io())
                //                        .observeOn(AndroidSchedulers.mainThread())
                //                        .subscribe(new Action1<Boolean>() {
                //                            @Override
                //                            public void call(Boolean aBoolean) {
                //                                KLog.d(TAG,"send result:" + aBoolean);
                //                            }
                //                        });
                break;
        }
    }

    @OnClick(R.id.float_button)
    public void onFloatActionButtonClicked() {
        int index = mPager.getCurrentItem();
        switch (index) {
            case 0:
            case 1:
                startActivity(new Intent(this, MailEditActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, ContactEditActivity.class));
                break;
            case 3:
                startActivity(new Intent(this, AccountEditActivity.class));

                break;
        }
    }
}
