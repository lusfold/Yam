package com.lusfold.yam.view.widget;

import android.view.View;

/**
 * Created by lusfold on 4/29/16.
 */
public class MainTabSwitcher {
    private View[] mTabs;
    private int lastPosition = -1;

    /**
     * @param tabs
     */
    public MainTabSwitcher(View[] tabs) {
        mTabs = tabs;
    }

    /**
     * @param position
     */
    public void switchTo(int position) {
        if (position == lastPosition) {
            return;
        }
        if (lastPosition >= 0) {
            mTabs[lastPosition].setSelected(false);
        }
        mTabs[position].setSelected(true);
        lastPosition = position;
    }

}
