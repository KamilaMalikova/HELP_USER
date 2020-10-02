package com.kamilamalikova.help.ui.settings;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.kamilamalikova.help.R;
import com.kamilamalikova.help.ui.settings.adapter.ViewPagerAdapter;
import com.kamilamalikova.help.ui.settings.tabfragments.CategorySettingsFragment;
import com.kamilamalikova.help.ui.settings.tabfragments.MeasureUnitsSettingsFragment;


public class SettingsFragment extends Fragment {

    ViewPager viewPager;
    View mmView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mmView = view;
        viewPager = view.findViewById(R.id.settingsViewPager);
        addTabs(viewPager);
        ((TabLayout) view.findViewById(R.id.settingsTabLayout)).setupWithViewPager(viewPager);
        return view;
    }

    private void addTabs(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(new CategorySettingsFragment(), getString(R.string.categories));
        adapter.addFrag(new MeasureUnitsSettingsFragment(), getString(R.string.measeureUnits));
        viewPager.setAdapter(adapter);
    }

}