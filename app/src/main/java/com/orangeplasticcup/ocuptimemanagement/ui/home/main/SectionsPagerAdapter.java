package com.orangeplasticcup.ocuptimemanagement.ui.home.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.orangeplasticcup.ocuptimemanagement.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_overview, R.string.tab_new_entry, R.string.tab_compare, R.string.tax_settings};
    private final Context mContext;

    private final OverviewFragment overviewFragment = OverviewFragment.newInstance(0);
    private final NewEntryFragment newEntryFragment = NewEntryFragment.newInstance(1);
    private final CompareFragment compareFragment = CompareFragment.newInstance(2);
    private final SettingsFragment settingsFragment = SettingsFragment.newInstance(3);


    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //return OverviewFragment.newInstance(position + 1);
        switch (position) {
            case 0: return overviewFragment;
            case 1: return newEntryFragment;
            case 2: return compareFragment;
            case 3: return settingsFragment;
            default: return new Fragment();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 4;
    }
}