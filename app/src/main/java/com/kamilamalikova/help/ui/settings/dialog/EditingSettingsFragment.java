package com.kamilamalikova.help.ui.settings.dialog;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kamilamalikova.help.R;


public class EditingSettingsFragment extends Fragment {

    private String id;
    private String value;
    private String type;

    public EditingSettingsFragment() {
    }

    public EditingSettingsFragment(String id, String value, String type) {
        this.id = id;
        this.value = value;
        this.type = type;
    }

    public String getValueId() {
        return id;
    }

    public void setValueId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_editing_settings, container, false);
        EditText valueEditText = view.findViewById(R.id.valueSettingTextEdit);
        TextView idTextView = view.findViewById(R.id.idTextView);
        TextView typeTextView = view.findViewById(R.id.typeTextView);

        setValueId(idTextView.getText().toString());
        setValue(valueEditText.getText().toString());
        setType(typeTextView.getText().toString());

        Log.i("Update", id+" - "+value+" - "+type);

        Button saveBtn = view.findViewById(R.id.saveSettingBtn);
        Button cancelBtn = view.findViewById(R.id.saveCancelBtn);


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return view;

    }
}