package com.sangsolutions.stock.StockAllocation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.sangsolutions.stock.Adapter.ViewPagerAdapter;
import com.sangsolutions.stock.Database.DatabaseHelper;
import com.sangsolutions.stock.Fragment.StockCountBodyFragment;
import com.sangsolutions.stock.Fragment.StockCountHeaderFragment;
import com.sangsolutions.stock.databinding.ActivityStockAllocationBinding;
import com.sangsolutions.stock.databinding.ActivityStockAllocationHistoryBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StockAllocationActivity extends AppCompatActivity {

    ActivityStockAllocationBinding binding;

    String voucherNo="";
    String EditMode;
    DatabaseHelper helper;
    Toolbar toolbar;
    int iId=0;

    private SimpleDateFormat df;
    Date c;
    int current_position=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStockAllocationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);



        Intent intent =  getIntent();
        if(intent!=null) {

            EditMode = intent.getStringExtra("EditMode");
            iId = intent.getIntExtra("iId",0);
            Log.d("lllllS",EditMode+" "+iId);

//            if(EditMode.){
//                voucherNo = intent.getStringExtra("voucherNo");
//                current_position=intent.getIntExtra("Position",0);
//            }else {
//                PublicData.voucher = "S-" + DateFormat.format("ddMMyy-HHmmss", new Date());
//                warehouse = "";
//                voucherNo = "S-" + DateFormat.format("ddMMyy-HHmmss", new Date());
//                binding.delete.setVisibility(View.GONE);
//            }

        }
        binding.viewpager.setOffscreenPageLimit(2);
        binding.tabLay.setSelectedTabIndicatorColor(Color.parseColor("#1973A8"));
        binding.tabLay.setTabTextColors(Color.parseColor("#ffffff"), Color.parseColor("#ffffff"));
        binding.tabLay.setupWithViewPager(binding.viewpager);
        setUpViewPager(binding.viewpager);


    }

    private void setUpViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.add(new StockCountHeaderFragment(), "Header",EditMode,iId);
        adapter.add(new StockCountBodyFragment(), "Body",EditMode,iId);
        viewPager.setAdapter(adapter);
    }
}