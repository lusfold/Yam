package com.lusfold.yam.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.lusfold.yam.R;
import com.lusfold.yam.event.AddContactEvent;
import com.lusfold.yam.event.BaseEvent;
import com.lusfold.yam.event.DeleteContactEvent;
import com.lusfold.yam.event.EditContactEvent;
import com.lusfold.yam.repository.ContactService;
import com.lusfold.yam.repository.DeviceService;
import com.lusfold.yam.repository.bean.Contact;
import com.lusfold.yam.repository.bean.Device;
import com.lusfold.yam.view.adapter.ContactRecyclerViewAdapter;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;


/**
 * Created by lusfold on 5/1/16.
 */
public class ContactFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.srl)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rv)
    RecyclerView recyclerView;
    @BindView(R.id.main_layout)
    View mainLayout;

    private MaterialDialog mDialog;

    private ContactRecyclerViewAdapter mAdapter;

    @Override
    public int getLayout() {
        return R.layout.fragment_contact;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setOnRefreshListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ContactRecyclerViewAdapter(this);
        recyclerView.setAdapter(mAdapter);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
    }

    @Subscribe
    public void onEvent(BaseEvent event) {
        if (event instanceof EditContactEvent || event instanceof AddContactEvent){
            swipeRefreshLayout.setRefreshing(true);
            onRefresh();
        }else if(event instanceof DeleteContactEvent){
            Contact contact = ((DeleteContactEvent) event).getContact();
            deleteContact(contact);
        }
    }


    @Override
    public void onRefresh() {
        loadContact();
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
                swipeRefreshLayout.setRefreshing(false);
                if (e != null) {
                    Snackbar.make(mainLayout, R.string.error_load, Snackbar.LENGTH_SHORT).show();
                } else {
                    mAdapter.setData(list);
                }
            }
        });
    }

    private void uploadDevice(Device device) {
        DeviceService.getInstance().uploadDevice(device, new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e != null) {
                    swipeRefreshLayout.setRefreshing(false);
                    Snackbar.make(mainLayout, R.string.error_load, Snackbar.LENGTH_SHORT).show();
                } else {
                    loadContact();
                }
            }
        });
    }

    private void deleteContact(Contact contact){
        showLoading(true);
        ContactService.getInstance().deleteContact(contact, new SaveCallback() {
            @Override
            public void done(AVException e) {
                showLoading(false);
                if (e!= null){
                    Snackbar.make(mainLayout, R.string.error_delete, Snackbar.LENGTH_SHORT).show();
                }else{
                    swipeRefreshLayout.setRefreshing(true);
                    onRefresh();
                }
            }
        });
    }

    private void showLoading(boolean active) {
        if (mDialog == null) {
            mDialog = new MaterialDialog.Builder(getActivity())
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
