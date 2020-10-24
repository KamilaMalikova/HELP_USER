package com.kamilamalikova.help.ui.report;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.DOCTYPE;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.OrderReport;
import com.kamilamalikova.help.model.RequestFormer;
import com.kamilamalikova.help.model.ResponseErrorHandler;
import com.kamilamalikova.help.model.SessionManager;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


public class OrderReportFragment extends Fragment {
    SessionManager sessionManager;
    LayoutInflater layoutInflater;

    View view;
    LocalDateTime start;
    LocalDateTime end;

    TextView startDateDisplay;
    TextView endDateDisplay;

    DatePickerDialog.OnDateSetListener mDateStartSetListener;
    DatePickerDialog.OnDateSetListener mDateEndSetListener;

    PopupWindow popupWindow;
    Button filterBtn;

    OrderReport report;
    OrderReportAdapter adapter;

    ListView orderReportListView;
    SwipeRefreshLayout reportSwipeRefresh;
    TextView sumNumberTextView;
    TextView tipNumberTextView;
    TextView allSumNumberTextView;

    View popupView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.layoutInflater = inflater;
        view = inflater.inflate(R.layout.fragment_order_report, container, false);
        sessionManager = new SessionManager(view.getContext());
        AndroidThreeTen.init(view.getContext());

        orderReportListView = view.findViewById(R.id.orderReportListView);
        reportSwipeRefresh = view.findViewById(R.id.reportSwipeRefresh);
        sumNumberTextView = view.findViewById(R.id.sumNumberTextView);
        tipNumberTextView = view.findViewById(R.id.tipNumberTextView);
        allSumNumberTextView = view.findViewById(R.id.allSumNumberTextView);

        end = LocalDateTime.now();
        start = LocalDateTime.of(end.getYear(), end.getMonth(), end.getDayOfMonth()-1, 0, 0, 0);
        requestData(URLs.GET_REPORT.getName(), start, end);

        reportSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                end = LocalDateTime.now();
                start = LocalDateTime.of(end.getYear(), end.getMonth(), end.getDayOfMonth()-1, 0, 0, 0);
                requestData(URLs.GET_REPORT.getName(), start, end);
                reportSwipeRefresh.setRefreshing(false);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.filter_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.filter){
            popupView = layoutInflater.inflate(R.layout.fragment_date_filter, null);
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

                    DatePickerDialog dialog = new DatePickerDialog(popupView.getContext(),
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

                    DatePickerDialog dialog = new DatePickerDialog(popupView.getContext(),
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
                    if (start.compareTo(LocalDateTime.now()) > 0){
                        Toast.makeText(popupView.getContext(), "Начальная дата не может быть больше текущей даты", Toast.LENGTH_LONG)
                                .show();
                        return;
                    }
                    String date = dayOfMonth+"/"+month+"/"+year;
                    startDateDisplay.setText(date);
                }
            };

            mDateEndSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month+=1;
                    end = LocalDateTime.of(year, month, dayOfMonth, LocalDateTime.now().getHour(), LocalDateTime.now().getMinute(), 0, 0);
                    if (end.compareTo(start) < 0){
                        Toast.makeText(popupView.getContext(), "Конечная дата не может быть больше начальной", Toast.LENGTH_LONG)
                                .show();
                        return;
                    }
                    String date = dayOfMonth+"/"+month+"/"+year;
                    endDateDisplay.setText(date);
                }
            };


            filterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestData(URLs.GET_REPORT.getName(), start, end);
                    popupWindow.dismiss();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }



    public void requestData(final String url, LocalDateTime from, LocalDateTime to){
        RequestPackage requestPackage = RequestFormer.getFilterRequestPackage(view.getContext(), url, from, to);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());
        client.get(getContext(), requestPackage.getFullUrl(), requestPackage.getEntity(), getString(R.string.content_type), new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Status", statusCode+" in");
                try {
                    JSONObject response = new JSONObject(new String(responseBody));
                    report = new OrderReport(response);
                    adapter = new OrderReportAdapter(getContext(), report.getOrderDetails());
                    orderReportListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    sumNumberTextView.setText((report.getSum()+" "+getString(R.string.uz_sum)));
                    tipNumberTextView.setText((report.getTip_sum()+" "+getString(R.string.uz_sum)));
                    allSumNumberTextView.setText((report.getAll_sum()+" "+getString(R.string.uz_sum)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("Status", statusCode+"");
                ResponseErrorHandler.showErrorMessage(view.getContext(), statusCode);
            }
        });
    }
}