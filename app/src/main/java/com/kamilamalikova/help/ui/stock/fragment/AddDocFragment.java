package com.kamilamalikova.help.ui.stock.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kamilamalikova.help.LogInActivity;
import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.FileStream;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.Product;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.kamilamalikova.help.ui.stock.adapter.DocTypeAdapter;
import com.kamilamalikova.help.ui.stock.adapter.DocTypeObject;
import com.kamilamalikova.help.ui.stock.adapter.ItemObject;
import com.kamilamalikova.help.ui.stock.adapter.ProductItemAdapter;
import com.kamilamalikova.help.ui.stock.adapter.ProductItemObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


public class AddDocFragment extends Fragment {

    volatile ListView stockProductListView;

    volatile CheckBox isChosenCheckBox;
    volatile TextView stockDocProductNameTextView;
    volatile EditText stockDocQtyTextView;
    volatile TextView stockDocIdTextView;

    volatile ArrayList<ProductItemObject> productList;

    public AddDocFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         String docTypeId[] = new String[]{"1", "2"};
         String docTypeName[] = new String[]{getString(R.string.in), getString(R.string.out)};


        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_add_doc, container, false);
        final Spinner docTypeSpinner = view.findViewById(R.id.docTypeSpinner);
        DocTypeAdapter docTypeAdapter = new DocTypeAdapter(docTypeId, docTypeName, getContext(), R.layout.spin_item);
        docTypeSpinner.setAdapter(docTypeAdapter);

        stockProductListView = view.findViewById(R.id.docInventoryListView);
        requestData(URLs.GET_ITEMS.getName(), null, "500", null);

        stockProductListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProductItemAdapter itemAdapter = (ProductItemAdapter) stockProductListView.getAdapter();
                View item_view = itemAdapter.getView(position, view, null);
                Log.i("Item click", "Clicked");
                isChosenCheckBox = item_view.findViewById(R.id.stockDocProductCheckBox);
                stockDocProductNameTextView = item_view.findViewById(R.id.stockDocProductNameTextView);
                stockDocQtyTextView = item_view.findViewById(R.id.stockDocQtyTextView);
                stockDocIdTextView = item_view.findViewById(R.id.stockDocIdTextView);

            }
        });


        Button addProductToListBtn = view.findViewById(R.id.formDocBtn);
        addProductToListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               productList = new ArrayList<>();
               if (stockProductListView.getAdapter().getCount() > 0){

                   for (int i = 0; i < stockProductListView.getAdapter().getCount(); i++){
                       ProductItemObject itemObject = (ProductItemObject) stockProductListView.getAdapter().getItem(i);
                       if (itemObject.isChosen()) productList.add(itemObject);
                   }
                   if (productList.size() > 0){
                       Bundle bundle = new Bundle();
                       bundle.putParcelable ("doctype", (DocTypeObject)docTypeSpinner.getSelectedItem());
                       bundle.putParcelableArrayList("inventories",  productList);
                       hideKeyboard(getContext());
                       Navigation.findNavController(view).navigate(R.id.nav_in_out_stock_doc, bundle);
                   }else {
                       Toast.makeText(getContext(), "Необходимо добавить как минимум один продукт", Toast.LENGTH_LONG)
                               .show();
                   }
               }else {
                   Toast.makeText(getContext(), "Необходимо добавить как минимум один продукт", Toast.LENGTH_LONG)
                           .show();
               }
            }
        });
        return view;
    }


    private void requestData(final String url, String productName, String categoryId, String category){
        final RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod(RequestType.GET);
        requestPackage.setUrl(url);

        if (productName != null) requestPackage.setParam("name", productName);
        if (!categoryId.equals("0") && !(categoryId.equals("500"))) {
            requestPackage.setParam("id", categoryId);
            requestPackage.setParam("category", category);
        }


        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(requestPackage.getJsonObject().toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        Log.i("SER", requestPackage.getFullUrl() + entity);
        Log.i("SER", requestPackage.getFullUrl() + requestPackage.getJsonObject());

        LoggedInUser loggedInUser = new FileStream().readUser(getActivity().getDir("data", Context.MODE_PRIVATE));

        if (loggedInUser == null){
            startIntentLogIn();
            return;
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), loggedInUser.getAuthorizationToken());

        client.get(getContext(), requestPackage.getFullUrl(), entity, entity.getContentType().toString(), new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Status", statusCode+"");
                try {
                    JSONArray responseArray;
                    if (url.endsWith(URLs.GET_ITEMS.getName())){
                        responseArray = new JSONArray(new String(responseBody));
                    }else {
                        JSONObject responseObject = new JSONObject(new String(responseBody));
                        responseArray = (JSONArray)responseObject.get("content");
                    }
                    Log.i("response", responseArray.toString());
                    ProductItemAdapter itemAdapter = new ProductItemAdapter(getContext(), responseArray);
                    stockProductListView.setAdapter(itemAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("Status", statusCode+"");
            }
        });
    }

    private void startIntentLogIn(){
        Intent startIntent = new Intent(getContext(), LogInActivity.class);
        startActivity(startIntent);
    }


    public static void hideKeyboard( Context context ) {

        try {
            InputMethodManager inputManager = ( InputMethodManager ) context.getSystemService( Context.INPUT_METHOD_SERVICE );

            View view = ( (Activity) context ).getCurrentFocus();
            if ( view != null ) {
                inputManager.hideSoftInputFromWindow( view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}