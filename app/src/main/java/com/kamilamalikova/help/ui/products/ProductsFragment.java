package com.kamilamalikova.help.ui.products;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kamilamalikova.help.NavigationActivity;
import com.kamilamalikova.help.R;
import com.kamilamalikova.help.ui.products.fragments.AddProductFragment;


public class ProductsFragment extends Fragment {


    public ProductsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_products, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fabAddProduct);
        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
//                AddProductFragment addProductFragment = new AddProductFragment();
//                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragmentProductsLayout, addProductFragment);
//                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                transaction.addToBackStack(null);
//                transaction.commit();

                try {
                    Navigation.findNavController(view).navigate(R.id.action_product_to_add_product);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        return view;
    }
}