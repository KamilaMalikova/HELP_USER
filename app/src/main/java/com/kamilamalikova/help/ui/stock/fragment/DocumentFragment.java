package com.kamilamalikova.help.ui.stock.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.kamilamalikova.help.LogInActivity;
import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.DOCTYPE;
import com.kamilamalikova.help.model.FileStream;
import com.kamilamalikova.help.model.Keyboard;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.StockDocument;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.kamilamalikova.help.ui.stock.adapter.DocTypeAdapter;
import com.kamilamalikova.help.ui.stock.adapter.DocTypeObject;
import com.kamilamalikova.help.ui.stock.adapter.ProductItemAdapter;
import com.kamilamalikova.help.ui.stock.adapter.ProductItemObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.threeten.bp.LocalDateTime;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


public class DocumentFragment extends Fragment {

    View view;
    Spinner docTypeSpinner;
    ListView docInventoryFinalListView;

    DocTypeObject docTypeObject;
    ArrayList<ProductItemObject> productItemObjectList;

    Button saveBtn;


    public DocumentFragment() {
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

        this.docTypeObject = getArguments().getParcelable("doctype");
        this.productItemObjectList =getArguments().getParcelableArrayList("inventories");

        this.view = inflater.inflate(R.layout.fragment_document, container, false);
        AndroidThreeTen.init(getContext());

        docTypeSpinner = view.findViewById(R.id.docTypeFinalSpinner);
        DocTypeAdapter docTypeAdapter = new DocTypeAdapter(docTypeId, docTypeName, getContext(), R.layout.spin_item);
        docTypeSpinner.setAdapter(docTypeAdapter);
        docTypeSpinner.setSelection(Integer.parseInt(docTypeObject.getId())-1);

        this.docInventoryFinalListView = view.findViewById(R.id.docInventoryFinalListView);
        ProductItemAdapter productItemAdapter = new ProductItemAdapter(getContext(), this.productItemObjectList);
        this.docInventoryFinalListView.setAdapter(productItemAdapter);

        this.saveBtn = view.findViewById(R.id.saveDocBtn);

        this.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type;
                if (((DocTypeObject)docTypeSpinner.getSelectedItem()).getId().equals("1")) type = DOCTYPE.IN.getName();
                        else type = DOCTYPE.OUT.getName();
                saveDocument(0, type, LocalDateTime.now());
            }
        });
        return this.view;
    }

    private void saveDocument(int docId, String docType, LocalDateTime time){
        final RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(URLs.POST_DOC.getName());

        requestPackage.setParam("documentId", Integer.toString(docId));
        requestPackage.setParam("documentType", docType);
        requestPackage.setParam("date", time.toString());


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
        client.post(getContext(), requestPackage.getFullUrl(), entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject responseObject = new JSONObject(new String(responseBody));
                    StockDocument stockDocument = new StockDocument(responseObject);
                    ArrayList<ProductItemObject> productItemsToSave = new ArrayList<>();
                    for (int i = 0; i < docInventoryFinalListView.getAdapter().getCount(); i++){
                        ProductItemObject itemObject = (ProductItemObject) docInventoryFinalListView.getAdapter().getItem(i);
                        if (itemObject.isChosen()) productItemsToSave.add(itemObject);
                    }
                    saveInventory(stockDocument, productItemsToSave);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                switch (statusCode){
                    case 406:
                        Toast.makeText(getContext(), statusCode+ "! На складе не достаточно продуктов для расходования", Toast.LENGTH_LONG)
                                .show();
                        break;
                    default:
                        Toast.makeText(getContext(), statusCode+"! Ошибка", Toast.LENGTH_LONG)
                            .show();
                        break;
                }
                Log.i("Status", statusCode+"");
            }
        });
    }

    private void saveInventory(StockDocument stockDocument, List<ProductItemObject> productItemObjects){
        final RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(URLs.POST_INVENTORY.getName()+"/"+stockDocument.getDocumentId());


        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(requestPackage.getStockInventoryJSONArray(stockDocument, productItemObjects).toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException | JSONException e) {
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
        client.post(getContext(), requestPackage.getFullUrl(), entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject responseObject = new JSONObject(new String(responseBody));
                    StockDocument resultDocument = new StockDocument(responseObject);
                    Log.i("Response", responseObject.toString());
                    Keyboard.hideKeyboard(getContext());
                    Navigation.findNavController(view).navigate(R.id.nav_in_out_stock);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                switch (statusCode){
                    case 406:
                    case 404:
                        Toast.makeText(getContext(), statusCode+ "! На складе не достаточно продуктов для расходования", Toast.LENGTH_LONG)
                                .show();
                        break;
                    default:
                        Toast.makeText(getContext(), statusCode+"! "+new String(responseBody), Toast.LENGTH_LONG)
                                .show();
                        break;

                }
            }
        });
    }

    private void startIntentLogIn(){
        Intent startIntent = new Intent(getContext(), LogInActivity.class);
        startActivity(startIntent);
    }
}