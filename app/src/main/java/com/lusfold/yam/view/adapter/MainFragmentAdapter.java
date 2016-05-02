package com.lusfold.yam.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by lusfold on 4/29/16.
 */
public class MainFragmentAdapter extends FragmentPagerAdapter {
    private Class[] mFragmentClasses;
    private Fragment[] mFragments;

    public MainFragmentAdapter(FragmentManager fm, Class[] fragmentClasses) {
        super(fm);
        mFragmentClasses = fragmentClasses;
        mFragments = new Fragment[mFragmentClasses.length];
    }


    public Fragment getItem(int position) {
        checkInitFragment(position);
        return mFragments[position];
    }

    /**
     * @param position
     */
    private void checkInitFragment(int position) {
        if (mFragments[position] == null) {
            try {
                mFragments[position] = (Fragment) mFragmentClasses[position].newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getCount() {
        return mFragmentClasses.length;
    }
}
