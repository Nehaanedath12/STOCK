package com.sangsolutions.stock.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sangsolutions.stock.databinding.BodyFrgmentBinding;

public class StockCountBodyFragment extends Fragment {

    BodyFrgmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= BodyFrgmentBinding.inflate(getLayoutInflater());
        return binding.getRoot();

    }
}
