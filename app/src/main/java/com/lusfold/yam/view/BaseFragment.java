package com.lusfold.yam.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVAnalytics;
import com.lusfold.yam.event.BaseEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;

/**
 * Created by lusfold on 4/29/16.
 */
public abstract class BaseFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), null);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    public abstract int getLayout();

    @Override
    public void onPause() {
        super.onPause();
        AVAnalytics.onFragmentStart(getClass().getSimpleName());
    }

    @Override
    public void onResume() {
        super.onResume();
        AVAnalytics.onFragmentEnd(getClass().getSimpleName());
    }

    @Subscribe
    public void onEventMainThread(BaseEvent event){

    }
}
