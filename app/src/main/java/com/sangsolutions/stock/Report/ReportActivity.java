package com.sangsolutions.stock.Report;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.sangsolutions.stock.Adapter.StockCountReportAdapter.StockCountReport;
import com.sangsolutions.stock.Adapter.StockCountReportAdapter.StockCountReportAdapter;
import com.sangsolutions.stock.Database.DatabaseHelper;
import com.sangsolutions.stock.R;
import com.sangsolutions.stock.Tools;
import com.sangsolutions.stock.URLs;
import com.sangsolutions.stock.databinding.ActivityReportBinding;
import com.sangsolutions.stock.databinding.ActivityReportSelectionBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReportActivity extends AppCompatActivity {

    ActivityReportBinding binding;
    int iProduct=0;
    String FromDate,ToDate;
    DatabaseHelper helper;
    AlertDialog alertDialog;
    List<StockCountReport> list2;
    StockCountReportAdapter stockCountReportAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        helper=new DatabaseHelper(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view1 = LayoutInflater.from(this).inflate(R.layout.progress_bar, null, false);
        builder.setView(view1);
        builder.setCancelable(false);
        alertDialog = builder.create();

        list2=new ArrayList<>();
        stockCountReportAdapter = new StockCountReportAdapter(list2,this);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));

        binding.back.setOnClickListener(v -> finish());

        Intent intent =  getIntent();
        if(intent!=null) {

            iProduct = intent.getIntExtra("iProduct",0);
            FromDate = intent.getStringExtra("FromDate");
            ToDate = intent.getStringExtra("ToDate");
            Log.d("llllR", iProduct+" "+ FromDate + " " + ToDate);

        }

        loadData();
    }

    private void loadData() {


        try {
            if(Tools.isConnected(this)){
                alertDialog.show();
                AndroidNetworking.get("http://" + new Tools().getIP(ReportActivity.this) + URLs.GetStockCount)
                        .addQueryParameter("fDate", Tools.dateFormat(FromDate))
                        .addQueryParameter("tDate", Tools.dateFormat(ToDate))
                        .addQueryParameter("iUser", helper.GetUserId())
                        .addQueryParameter("iProduct", String.valueOf(iProduct))
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONArray(new JSONArrayRequestListener() {
                            @Override
                            public void onResponse(JSONArray response) {
                                Log.d("responseStock",response+"");
                                try {
                                    binding.emptyFrame.setVisibility(View.INVISIBLE);
                                    JSONArray jsonArray = new JSONArray(response.toString());
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        String DocDate = jsonObject.getString("iDocDate");
                                        String iVoucherNo = jsonObject.getString("iVoucherNo");
                                        String Warehouse = jsonObject.getString("sWarehouse");
                                        String Name = jsonObject.getString("sProduct");
                                        String ProductCode = jsonObject.getString("sProduct");
                                        String fQty = jsonObject.getString("fQty");
                                        String sUnit = jsonObject.getString("sUnit");
                                        String sRemarks = jsonObject.getString("sRemarks");

                                        list2.add(new StockCountReport(DocDate,iVoucherNo,ProductCode,Warehouse,Name,fQty,sUnit,sRemarks));
                                        if (jsonArray.length() == i + 1) {
                                            binding.recycler.setAdapter(stockCountReportAdapter);
                                            alertDialog.dismiss();
                                        }
                                    }
                                    if (jsonArray.length() <= 0) {
                                        alertDialog.dismiss();
                                        binding.emptyFrame.setVisibility(View.VISIBLE);
                                    }
                                } catch (JSONException e) {
                                    alertDialog.dismiss();
                                    binding.emptyFrame.setVisibility(View.VISIBLE);
                                    String fnName=new Object() {}.getClass().getName()+"."+ Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName();
                                    Tools.logWrite(fnName,e,ReportActivity.this);
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                alertDialog.dismiss();
                                binding.emptyFrame.setVisibility(View.VISIBLE);
                                Log.d("errorResponse", anError.getErrorDetail());
                            }
                        });


            }else {
                Toast.makeText(this, "Please try when You are Online", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            String fnName=new Object() {}.getClass().getName()+"."+ Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName();
            Tools.logWrite(fnName,e,this);
        }
    }
}