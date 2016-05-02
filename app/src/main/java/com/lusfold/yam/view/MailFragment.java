package com.lusfold.yam.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lusfold.yam.R;
import com.lusfold.yam.event.UpdateMailSentEvent;
import com.lusfold.yam.repository.CacheService;
import com.lusfold.yam.view.adapter.MailRecyclerViewAdapter;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

/**
 * Created by lusfold on 4/29/16.
 */
public class MailFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.srl)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rv)
    RecyclerView recyclerView;
    @BindView(R.id.main_layout)
    View mainLayout;

    private MailRecyclerViewAdapter mAdapter;

    @Override
    public int getLayout() {
        return R.layout.fragment_mail;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout.setOnRefreshListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MailRecyclerViewAdapter(this);
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
    public void onEvent(UpdateMailSentEvent event) {
        onRefresh();
    }


    @Override
    public void onRefresh() {
        loadAccount();
    }

    private void loadAccount() {
        swipeRefreshLayout.setRefreshing(false);
        mAdapter.setData(CacheService.getEmailsSent());
    }
}
