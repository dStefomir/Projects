package com.securegroup.githubapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Takes care of the View Pager pages(fragments)
 * <p>
 *
 * @author Stefomir Dimitrov </p>
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mFragmentTitleList = new ArrayList<>();

    /**
     * Constructor
     *
     * @param fragmentManager fragment manager retrieved from activity
     */
    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {

        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    /**
     * Inserts a fragment in the view pager adapter to be loaded in the parent activity
     *
     * @param fragment      instance of the fragment to be added in the adapter data structure
     * @param fragmentTitle name of the fragment which is going to be shown as a tab in the activity toolbar
     */
    public void addFragment(
            Fragment fragment,
            String fragmentTitle) {

        mFragmentList.add(fragment);
        mFragmentTitleList.add(fragmentTitle);
    }
}
