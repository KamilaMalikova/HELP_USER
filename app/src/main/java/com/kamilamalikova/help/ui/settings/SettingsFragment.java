package com.kamilamalikova.help.ui.settings;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.SessionManager;
import com.kamilamalikova.help.model.UserRole;
import com.kamilamalikova.help.ui.settings.adapter.ViewPagerAdapter;
import com.kamilamalikova.help.ui.settings.tabfragments.CategorySettingsFragment;
import com.kamilamalikova.help.ui.settings.tabfragments.IpFragment;
import com.kamilamalikova.help.ui.settings.tabfragments.MeasureUnitsSettingsFragment;
import com.kamilamalikova.help.ui.settings.tabfragments.TablesSettingsFragment;
import com.kamilamalikova.help.ui.settings.tabfragments.TipFragment;


public class SettingsFragment extends Fragment {
    SessionManager sessionManager;
    ViewPager viewPager;
    View view;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        sessionManager = new SessionManager(view.getContext());
        viewPager = view.findViewById(R.id.settingsViewPager);
        addTabs(viewPager);
        ((TabLayout) view.findViewById(R.id.settingsTabLayout)).setupWithViewPager(viewPager);
        return view;
    }

    private void addTabs(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        switch (sessionManager.getRole()){
            case "ROLE_ADMIN":
            case "ADMIN":
            case "ROLE_OWNER":
            case "OWNER":
                adapter.addFrag(new CategorySettingsFragment(), getString(R.string.categories));
                adapter.addFrag(new MeasureUnitsSettingsFragment(), getString(R.string.measeureUnits));
                adapter.addFrag(new TablesSettingsFragment(), getString(R.string.tables));
                adapter.addFrag(new TipFragment(), getString(R.string.just_tip));
                adapter.addFrag(new IpFragment(), getString(R.string.ip));
                break;
            case "ROLE_STUFF":
            case "STUFF":
                adapter.addFrag(new CategorySettingsFragment(), getString(R.string.categories));
                adapter.addFrag(new MeasureUnitsSettingsFragment(), getString(R.string.measeureUnits));
                adapter.addFrag(new IpFragment(), getString(R.string.ip));
                break;
            case "ROLE_WAITER":
            case "WAITER":
                adapter.addFrag(new IpFragment(), getString(R.string.ip));
                break;
        }

        viewPager.setAdapter(adapter);
    }

}