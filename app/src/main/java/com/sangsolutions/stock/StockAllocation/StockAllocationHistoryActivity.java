package com.sangsolutions.stock.StockAllocation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sangsolutions.stock.databinding.ActivityLoginBinding;
import com.sangsolutions.stock.databinding.ActivityStockAllocationHistoryBinding;

public class StockAllocationHistoryActivity extends AppCompatActivity {

    ActivityStockAllocationHistoryBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStockAllocationHistoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), StockAllocationActivity.class);
                intent.putExtra("EditMode", "new");
                intent.putExtra("iId",0);

                startActivity(intent);
            }
        });
    }
}