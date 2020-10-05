package com.kamilamalikova.help.ui.stock.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.ui.stock.adapter.DocTypeAdapter;


public class AddDocFragment extends Fragment {

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
        View view = inflater.inflate(R.layout.fragment_add_doc, container, false);
        Spinner docTypeSpinner = view.findViewById(R.id.docTypeSpinner);
        DocTypeAdapter docTypeAdapter = new DocTypeAdapter(docTypeId, docTypeName, getContext(), R.layout.spin_item);
        docTypeSpinner.setAdapter(docTypeAdapter);

        Button addProductToListBtn = view.findViewById(R.id.addProductToDocBtn);
        addProductToListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return view;
    }
}