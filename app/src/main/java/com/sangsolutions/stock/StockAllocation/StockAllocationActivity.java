package com.sangsolutions.stock.StockAllocation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.sangsolutions.stock.Adapter.BodyAdapter.StockBody;
import com.sangsolutions.stock.Adapter.BodyAdapter.StockHeader;
import com.sangsolutions.stock.Adapter.Singleton.StockCountProductSingleton;
import com.sangsolutions.stock.Adapter.ViewPagerAdapter;
import com.sangsolutions.stock.Database.DatabaseHelper;
import com.sangsolutions.stock.Fragment.StockCountBodyFragment;
import com.sangsolutions.stock.Fragment.StockCountHeaderFragment;
import com.sangsolutions.stock.PublicData;
import com.sangsolutions.stock.Tools;
import com.sangsolutions.stock.databinding.ActivityStockAllocationBinding;
import com.sangsolutions.stock.databinding.ActivityStockAllocationHistoryBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.sangsolutions.stock.PublicData.warehouse;

public class StockAllocationActivity extends AppCompatActivity {

    ActivityStockAllocationBinding binding;

    String EditMode;
    DatabaseHelper helper;
    int iId=0;

    @Override
    public void onBackPressed() {
        Alert("Close");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStockAllocationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        helper=new DatabaseHelper(this);

        try {



        Intent intent =  getIntent();
        if(intent!=null) {

            EditMode = intent.getStringExtra("EditMode");
            iId = intent.getIntExtra("iId",0);
            Log.d("lllllS",EditMode+" "+iId);

            if(EditMode.equals("edit")){
                binding.delete.setVisibility(View.VISIBLE);
            }else {

                binding.delete.setVisibility(View.GONE);
            }

        }
        binding.viewpager.setOffscreenPageLimit(2);
        binding.tabLay.setSelectedTabIndicatorColor(Color.parseColor("#1973A8"));
        binding.tabLay.setTabTextColors(Color.parseColor("#ffffff"), Color.parseColor("#ffffff"));
        binding.tabLay.setupWithViewPager(binding.viewpager);
        setUpViewPager(binding.viewpager);


        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  Alert("Save");
            }
        });
        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alert("Close");
            }
        });

        binding.addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<StockBody> list = StockCountProductSingleton.getInstance().getList();
                if (!list.isEmpty()) {
                    NewVoucherWaringAlert();
                }
            }
        });

        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alert("Delete");
            }
        });
        }catch (Exception e){
            String fnName=new Object() {}.getClass().getName()+"."+ Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName();
            Tools.logWrite(fnName,e, this);
        }

    }

    private void NewVoucherWaringAlert() {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle("Warning!")
                    .setMessage("Do you want to start new before saving ?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            EditMode = "new";
                            PublicData.clearData();
//                            PublicData.voucher = "S-" + DateFormat.format("ddMMyy-HHmmss", new Date());
                            StockCountProductSingleton.getInstance().clearList();
                            setUpViewPager(binding.viewpager);
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    })
                    .create()
                    .show();
    }

    private void Alert(String alert) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(StockAllocationActivity.this);
        builder.setTitle(alert)
                .setMessage("Do you want to "+alert+"?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (alert){
                            case "Close":{
                                StockCountProductSingleton.getInstance().clearList();
                                PublicData.clearData();
                                finish();
                            }break;
                            case "Delete":{
                                if(helper.DeleteStockwithId(String.valueOf(iId))){
                                    Toast.makeText(StockAllocationActivity.this, "Deleted SuccessFully!", Toast.LENGTH_SHORT).show();
                                    StockCountProductSingleton.getInstance().clearList();
                                    PublicData.clearData();
                                    finish();
                                }
                            }break;
                            case "Save":{
                                Save();
                            }break;
                        }

                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void Save() {
        try {


        String s_date,s_narration,s_stock_date,s_voucher_no;
        int iWareHouse;

        s_date= PublicData.date;

        s_stock_date = PublicData.stock_date;

        s_narration=PublicData.narration;

        iWareHouse = warehouse;
        s_voucher_no = PublicData.voucher;
        List<StockBody> list = StockCountProductSingleton.getInstance().getList();

        if(!s_date.isEmpty()&&list.size()>0)
        {


            StockHeader stockHeader=new StockHeader(
                    s_voucher_no,
                    s_date,
                    String.valueOf(DateFormat.format("yyyy-MM-dd hh:mm:ss a", new Date())),
                    s_narration,
                    s_stock_date,
                    iWareHouse);


            if(EditMode.equals("edit")) {
                if(helper.updateStockData(stockHeader,list,iId)){
                    Toast.makeText(this, "Updated SuccessFully!", Toast.LENGTH_SHORT).show();
                    StockCountProductSingleton.getInstance().clearList();
                    PublicData.clearData();
                    this.finish();
                }
            }else {
                if(helper.insertStockData(stockHeader,list)){
                    Toast.makeText(this, "Added SuccessFully!", Toast.LENGTH_SHORT).show();
                    StockCountProductSingleton.getInstance().clearList();
                    PublicData.clearData();
                    this.finish();
                }
            }


        }else {
            Toast.makeText(this, "Please add products", Toast.LENGTH_SHORT).show();
        }
        }catch (Exception e){
            String fnName=new Object() {}.getClass().getName()+"."+ Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName();
            Tools.logWrite(fnName,e, this);
        }

    }

    private void setUpViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.add(new StockCountHeaderFragment(), "Header",EditMode,iId);
        adapter.add(new StockCountBodyFragment(), "Body",EditMode,iId);
        viewPager.setAdapter(adapter);
    }
}