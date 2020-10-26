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
import com.kamilamalikova.help.model.Keyboard;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.Product;
import com.kamilamalikova.help.model.RequestFormer;
import com.kamilamalikova.help.model.ResponseErrorHandler;
import com.kamilamalikova.help.model.SessionManager;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


public class AddDocFragment extends Fragment {

    SessionManager sessionManager;

    View view;
    ListView stockProductListView;
    Spinner docTypeSpinner;
    DocTypeAdapter docTypeAdapter;
    Set<ProductItemObject> itemObjectSet = new LinkedHashSet<>();
    ProductItemAdapter itemAdapter;
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
        view = inflater.inflate(R.layout.fragment_add_doc, container, false);
        sessionManager = new SessionManager(view.getContext());
        docTypeSpinner = view.findViewById(R.id.docTypeSpinner);
        docTypeAdapter = new DocTypeAdapter(docTypeId, docTypeName, view.getContext(), R.layout.spin_item);
        docTypeSpinner.setAdapter(docTypeAdapter);

        stockProductListView = view.findViewById(R.id.docInventoryListView);
        requestData(URLs.GET_ITEMS.getName(), null, "500", null);

        Button addProductToListBtn = view.findViewById(R.id.formDocBtn);
        addProductToListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ArrayList<ProductItemObject> productList = new ArrayList<>();
               List<ProductItemObject> adapterProductsList = itemAdapter.productList;
               if (adapterProductsList.size() > 0){
                   for (int i = 0; i < adapterProductsList.size(); i++){
                       ProductItemObject itemObject = adapterProductsList.get(i);
                       if (itemObject.isChosen()) productList.add(itemObject);
                   }
                   if (productList.size() > 0){
                       Bundle bundle = new Bundle();
                       bundle.putParcelable ("doctype", (DocTypeObject)docTypeSpinner.getSelectedItem());
                       bundle.putParcelableArrayList("inventories",  productList);
                       Keyboard.hideKeyboard(view.getContext());
                       Navigation.findNavController(view).navigate(R.id.nav_in_out_stock_doc, bundle);
                   }else {
                       Toast.makeText(view.getContext(), "Необходимо добавить как минимум один продукт", Toast.LENGTH_SHORT)
                               .show();
                   }
               }else {
                   Toast.makeText(view.getContext(), "Необходимо добавить как минимум один продукт", Toast.LENGTH_SHORT)
                           .show();
               }
            }
        });
        return view;
    }

    private void requestData(final String url, String productName, String categoryId, String category){
        RequestPackage requestPackage = RequestFormer.getProductRequestPackage(view.getContext(), url, productName, categoryId, category);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());

        client.get(view.getContext(), requestPackage.getFullUrl(), requestPackage.getBytes(), getString(R.string.content_type), new AsyncHttpResponseHandler(){
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
                    itemAdapter = new ProductItemAdapter(view.getContext(), responseArray, docTypeSpinner);
                    stockProductListView.setAdapter(itemAdapter);
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