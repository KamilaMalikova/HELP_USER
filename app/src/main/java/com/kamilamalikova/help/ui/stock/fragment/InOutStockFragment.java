package com.kamilamalikova.help.ui.stock.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.kamilamalikova.help.NavigationActivity;
import com.kamilamalikova.help.R;
import com.kamilamalikova.help.ui.settings.adapter.ViewPagerAdapter;
import com.kamilamalikova.help.ui.settings.tabfragments.CategorySettingsFragment;
import com.kamilamalikova.help.ui.settings.tabfragments.MeasureUnitsSettingsFragment;

public class InOutStockFragment extends Fragment {


    public InOutStockFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_in_out_stock, container, false);

        ViewPager viewPager = view.findViewById(R.id.stockViewPager);
        addTabs(viewPager);
        ((TabLayout) view.findViewById(R.id.stockDocTabLayout)).setupWithViewPager(viewPager);

        FloatingActionButton fab = view.findViewById(R.id.fabAddDoc);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.nav_add_in_out_stock_doc);
            }
        });
        return view;
    }

    private void addTabs(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(new InStockFragment(), getString(R.string.in));
        adapter.addFrag(new OutStockFragment(), getString(R.string.out));
        viewPager.setAdapter(adapter);
    }
}