package com.kamilamalikova.help.ui.stock.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.se.omapi.Session;
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
import com.kamilamalikova.help.model.RequestFormer;
import com.kamilamalikova.help.model.ResponseErrorHandler;
import com.kamilamalikova.help.model.SessionManager;
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
    SessionManager sessionManager;
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
        sessionManager = new SessionManager(view.getContext());
        AndroidThreeTen.init(view.getContext());

        docTypeSpinner = view.findViewById(R.id.docTypeFinalSpinner);
        DocTypeAdapter docTypeAdapter = new DocTypeAdapter(docTypeId, docTypeName, view.getContext(), R.layout.spin_item);
        docTypeSpinner.setAdapter(docTypeAdapter);
        docTypeSpinner.setSelection(Integer.parseInt(docTypeObject.getId())-1);

        this.docInventoryFinalListView = view.findViewById(R.id.docInventoryFinalListView);
        ProductItemAdapter productItemAdapter = new ProductItemAdapter(view.getContext(), this.productItemObjectList);
        this.docInventoryFinalListView.setAdapter(productItemAdapter);

        this.saveBtn = view.findViewById(R.id.saveDocBtn);

        this.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type;
                if (((DocTypeObject)docTypeSpinner.getSelectedItem()).getId().equals("1")) type = DOCTYPE.IN.getName();
                        else type = DOCTYPE.OUT.getName();
                try {
                    saveDocument(0, type, LocalDateTime.now());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        return this.view;
    }

    public static RequestPackage getDocumentRequestPackage(Context context, int docId, String docType, LocalDateTime time){
        RequestPackage requestPackage = new RequestPackage(context);
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(URLs.POST_DOC.getName());

        requestPackage.setParam("documentId", docId);
        requestPackage.setParam("documentType", docType);
        requestPackage.setParam("date", time);
        return requestPackage;
    }
    private void saveDocument(int docId, String docType, LocalDateTime time) throws UnsupportedEncodingException {
        RequestPackage requestPackage = RequestFormer.getDocumentRequestPackage(view.getContext(), URLs.POST_DOC.getName(), docId, docType, time);

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());
        client.post(getContext(), requestPackage.getFullUrl(), requestPackage.getEntity(), "application/json", new AsyncHttpResponseHandler() {
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

    private void saveInventory(StockDocument stockDocument, List<ProductItemObject> productItemObjects) throws JSONException {
        RequestPackage requestPackage = RequestFormer.getProductItemRequestPackage(view.getContext(), URLs.POST_INVENTORY.getName(), stockDocument, productItemObjects);

        AsyncHttpClient client = new AsyncHttpClient();

        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());

        client.post(getContext(), requestPackage.getFullUrl(), requestPackage.getEntity(), "application/json", new AsyncHttpResponseHandler() {
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
                Log.i("Error", statusCode+" "+new String(responseBody));
                ResponseErrorHandler.showErrorMessage(view.getContext(), statusCode);
            }
        });
    }

    private void startIntentLogIn(){
        Intent startIntent = new Intent(getContext(), LogInActivity.class);
        startActivity(startIntent);
    }
}