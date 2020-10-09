package com.kamilamalikova.help.ui.stock.fragment;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.DOCTYPE;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.ui.settings.adapter.ViewPagerAdapter;

import org.threeten.bp.LocalDateTime;

import java.util.Calendar;
import java.util.Locale;

public class InOutStockFragment extends Fragment {

    volatile LayoutInflater layoutInflater;
    volatile View view;

    volatile TextView startDateDisplay;
    volatile TextView endDateDisplay;

    volatile DatePickerDialog.OnDateSetListener mDateStartSetListener;
    volatile DatePickerDialog.OnDateSetListener mDateEndSetListener;

    PopupWindow popupWindow;
    Button filterBtn;

    InStockFragment inStockFragment = new InStockFragment();
    OutStockFragment outStockFragment = new OutStockFragment();

    LocalDateTime start;
    LocalDateTime end;

    public InOutStockFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AndroidThreeTen.init(getContext());
        layoutInflater = inflater;
        view = inflater.inflate(R.layout.fragment_in_out_stock, container, false);

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
        adapter.addFrag(inStockFragment, getString(R.string.in));
        adapter.addFrag(outStockFragment, getString(R.string.out));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.filter_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.filter){
            View popupView = layoutInflater.inflate(R.layout.fragment_date_filter, null);
            int width = LinearLayout.LayoutParams.MATCH_PARENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;

            popupWindow = new PopupWindow(popupView, width, height, true);
            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(true);
            //popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.setFocusable(true);

            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);




            startDateDisplay = popupView.findViewById(R.id.dateStartFilterTextView);
            startDateDisplay.setInputType(InputType.TYPE_NULL);

            endDateDisplay = popupView.findViewById(R.id.dateEndFilterTextView);
            endDateDisplay.setInputType(InputType.TYPE_NULL);

            startDateDisplay.requestFocus();
            filterBtn = popupView.findViewById(R.id.filterDateBtn);

            startDateDisplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(getContext(),
                            R.style.Theme_AppCompat_Light_Dialog,
                            mDateStartSetListener,
                            year, month, day);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setGravity(Gravity.CENTER);

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    dialog.show();
                }
            });

            endDateDisplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(getContext(),
                            R.style.Theme_AppCompat_Light_Dialog,
                            mDateEndSetListener,
                            year, month, day);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setGravity(Gravity.CENTER);

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    dialog.show();
                }
            });

            mDateStartSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month+=1;
                    start = LocalDateTime.of(year, month, dayOfMonth, LocalDateTime.now().getHour(), LocalDateTime.now().getMinute(), 0, 0);
                    String date = dayOfMonth+"/"+month+"/"+year;
                    startDateDisplay.setText(date);
                }
            };

            mDateEndSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month+=1;
                    end = LocalDateTime.of(year, month, dayOfMonth, LocalDateTime.now().getHour(), LocalDateTime.now().getMinute(), 0, 0);
                    String date = dayOfMonth+"/"+month+"/"+year;
                    endDateDisplay.setText(date);
                }
            };

//            popupView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//
//                    popupWindow.dismiss();
//                    return true;
//                }
//            });

            filterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inStockFragment.requestData(URLs.GET_DOCS.getName()+"/1", DOCTYPE.IN.getName(), start, end);
                    outStockFragment.requestData(URLs.GET_DOCS.getName()+"/1", DOCTYPE.OUT.getName(), start, end);
                    popupWindow.dismiss();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }



}