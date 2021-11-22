package com.sangsolutions.stock.StockAllocation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.sangsolutions.stock.Adapter.BodyAdapter.StockHeader;
import com.sangsolutions.stock.Adapter.StockHistoryAdapter.StockCountList;
import com.sangsolutions.stock.Adapter.StockHistoryAdapter.StockCountListAdapter;
import com.sangsolutions.stock.Database.DatabaseHelper;
import com.sangsolutions.stock.R;
import com.sangsolutions.stock.Service.GetProductService;
import com.sangsolutions.stock.Tools;
import com.sangsolutions.stock.databinding.ActivityStockAllocationHistoryBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StockAllocationHistoryActivity extends AppCompatActivity {

    ActivityStockAllocationHistoryBinding binding;

    List<StockCountList> list;
    StockCountListAdapter adapter;
    DatabaseHelper helper;
    Animation slideUp, slideDown;
    boolean selectionActive = false;

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStockAllocationHistoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        helper=new DatabaseHelper(this);

        list=new ArrayList<>();
        adapter = new StockCountListAdapter(list,this);
        binding.rvSummary.setLayoutManager(new LinearLayoutManager(this));

        slideDown = AnimationUtils.loadAnimation(this, R.anim.move_down);
        slideUp = AnimationUtils.loadAnimation(this, R.anim.move_up);
        binding.fabDelete.setVisibility(View.GONE);
        binding.fabClose.setVisibility(View.GONE);

        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), StockAllocationActivity.class);
                intent.putExtra("EditMode", "new");
                intent.putExtra("iId",0);

                startActivity(intent);
            }
        });


        binding.fabClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSelection();
            }
        });
        binding.fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAlert();
            }
        });
    }

    private void deleteAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete?")
                .setMessage("Do you want to Delete " + adapter.getSelectedItemCount() + " items?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteItems();

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

    private void DeleteItems() {
        try {

        List<String>idList=new ArrayList<>();
        List<Integer> listSelectedItem = adapter.getSelectedItems();
        for (int i =0;i<listSelectedItem.size();i++) {
            for (int j =0;j<list.size();j++) {
                if (listSelectedItem.get(i) == j) {
                    idList.add(String.valueOf(list.get(j).getiId()));
                }
            }
            if (i + 1 == listSelectedItem.size()) {
                String listTransId="";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    listTransId= String.join(", ", idList);
                }
                deleteFromDB(listTransId);
                adapter.notifyDataSetChanged();
                closeSelection();
            }
        }
        }catch (Exception e){
            String fnName=new Object() {}.getClass().getName()+"."+ Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName();
            Tools.logWrite(fnName,e, this);
        }
    }

    private void loadData() {
        try {


        list.clear();
        Cursor cursor = helper.GetStockCountList();
        if (cursor != null) {
            cursor.moveToFirst();
            binding.emptyFrame.setVisibility(View.GONE);
            for (int i = 0; i < cursor.getCount(); i++) {
                String Warehouse, Date, TotalQty="", VoucherNo,warehouseId;

                Warehouse = helper.GetWarehouseById(cursor.getString(cursor.getColumnIndex(StockHeader.I_WAREHOUSE)));
                Date = cursor.getString(cursor.getColumnIndex(StockHeader.D_DATE));
                VoucherNo = cursor.getString(cursor.getColumnIndex(StockHeader.S_VOUCHER_NO));
//                TotalQty = cursor.getString(cursor.getColumnIndex("SumQty"));
                warehouseId = cursor.getString(cursor.getColumnIndex(StockHeader.I_WAREHOUSE));
                int iId=cursor.getInt(cursor.getColumnIndex(StockHeader.I_ID));

                list.add(new StockCountList(VoucherNo,Date,TotalQty,Warehouse,warehouseId,iId));
                cursor.moveToNext();

                if (i + 1 == cursor.getCount()) {
                    binding.rvSummary.setAdapter(adapter);
//                    StockCountSingleton.getInstance().setList(list);
                    cursor.close();

                    adapter.setOnClickListener(new StockCountListAdapter.OnClickListener() {
                        @Override
                        public void onDeleteItemClick(StockCountList stockCountList, int pos) {
                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(StockAllocationHistoryActivity.this);
                            builder.setTitle("Delete?")
                                    .setMessage("Do you want to Delete this item?")
                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteFromDB(String.valueOf(stockCountList.getiId()));
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

                        @Override
                        public void onItemClick(StockCountList stockCountList, int pos) {


                            if(!selectionActive){
                                Intent intent=new Intent(getApplicationContext(), StockAllocationActivity.class);
                                intent.putExtra("EditMode", "edit");
                                intent.putExtra("iId",stockCountList.getiId());
                                startActivity(intent);
                            }else {
                                enableActionMode(pos);
                            }
                        }

                        @Override
                        public void onItemLongClick(int pos) {
                            enableActionMode(pos);
                            selectionActive = true;
                        }
                    });

                }
            }
        }else {
            binding.emptyFrame.setVisibility(View.VISIBLE);
            binding.rvSummary.setAdapter(adapter);
        }
        }catch (Exception e){
            String fnName=new Object() {}.getClass().getName()+"."+ Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName();
            Tools.logWrite(fnName,e, this);
        }
    }

    private void enableActionMode(int position) {
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();

        if (count == 1 && binding.fabDelete.getVisibility() != View.VISIBLE) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    binding.fabDelete.startAnimation(slideUp);
                    binding.fabClose.startAnimation(slideUp);
                    binding.fabDelete.setVisibility(View.VISIBLE);
                    binding.fabClose.setVisibility(View.VISIBLE);
                }
            }, 300);
        }

        if (count == 0) {
            closeSelection();
        }
    }

    private void closeSelection() {

        adapter.clearSelections();
        binding.fabDelete.startAnimation(slideDown);
        binding.fabClose.startAnimation(slideDown);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.fabDelete.setVisibility(View.GONE);
                binding.fabClose.setVisibility(View.GONE);
            }
        }, 300);
        selectionActive = false;
    }

    private void deleteFromDB(String iId) {

        if(helper.DeleteStockwithId(iId)){
            Toast.makeText(this, "Deleted SuccessFully!", Toast.LENGTH_SHORT).show();
            loadData();
        }

    }
}