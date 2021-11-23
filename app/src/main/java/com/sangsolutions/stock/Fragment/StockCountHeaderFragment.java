package com.sangsolutions.stock.Fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sangsolutions.stock.Adapter.BodyAdapter.StockHeader;
import com.sangsolutions.stock.Database.DatabaseHelper;
import com.sangsolutions.stock.PublicData;
import com.sangsolutions.stock.R;
import com.sangsolutions.stock.Tools;
import com.sangsolutions.stock.databinding.HeaderFrgmentBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class StockCountHeaderFragment extends Fragment {

    HeaderFrgmentBinding binding;
    DatabaseHelper helper;
    String voucherNo="";
    java.util.Date c;
    int iId;
    List<Warehouse> warehouseList;
    WarehouseAdapter adapter;
    String EditMode = "";
    SimpleDateFormat df;
    @SuppressLint("SimpleDateFormat")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding=HeaderFrgmentBinding.inflate(getLayoutInflater());


        c = Calendar.getInstance().getTime();
        df = new SimpleDateFormat("dd-MM-yyyy");

        helper=new DatabaseHelper(requireContext());
        warehouseList = new ArrayList<>();
        adapter = new WarehouseAdapter(warehouseList);
        LoadWarehouse();
        try {
            if (getArguments() != null) {
                EditMode = getArguments().getString("EditMode");
                iId = getArguments().getInt("iId", 0);

                if (EditMode.equals("edit")) {
                    getEditData();

                } else if (EditMode.equals("new")) {
                    voucherNo = "S-" + DateFormat.format("ddMMyy-HHmmss", new Date());
                    binding.voucherNo.setText(String.format("Voucher No :%s", voucherNo));
                    PublicData.voucher=voucherNo;
                    binding.date.setText(df.format(c));
                    binding.stockDate.setText(df.format(c));
                    PublicData.date = df.format(c);
                    PublicData.stock_date = df.format(c);
                }


            } else {
                Toast.makeText(getActivity(), "Didn't have data to load!", Toast.LENGTH_SHORT).show();
                requireActivity().finish();
            }


            binding.warehouseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    PublicData.warehouse = warehouseList.get(adapterView.getSelectedItemPosition()).getMasterId();

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            binding.date.setOnClickListener(v -> datePick(binding.date,"date"));
            binding.stockDate.setOnClickListener(v -> datePick(binding.stockDate,"stockDate"));


        }catch (Exception e){
            String fnName=new Object() {}.getClass().getName()+"."+ Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName();
            Tools.logWrite(fnName,e,requireContext());
        }

        binding.narration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                PublicData.narration=s.toString();
            }
        });



        return binding.getRoot();
    }

    private void getEditData() {
        try {


        Cursor cursor = helper.GetHeaderData(iId);
        if(cursor!=null && cursor.moveToFirst()){

            PublicData.warehouse = cursor.getInt(cursor.getColumnIndex(StockHeader.I_WAREHOUSE));
            PublicData.voucher=cursor.getString(cursor.getColumnIndex(StockHeader.S_VOUCHER_NO));
            PublicData.date = cursor.getString(cursor.getColumnIndex(StockHeader.D_DATE));
            PublicData.stock_date=cursor.getString(cursor.getColumnIndex(StockHeader.D_STOCK_COUNT_DATE));
            PublicData.narration=cursor.getString(cursor.getColumnIndex(StockHeader.S_NARRATION));
            binding.voucherNo.setText(String.format("Voucher No :%s", cursor.getString(cursor.getColumnIndex(StockHeader.S_VOUCHER_NO))));
            binding.date.setText(cursor.getString(cursor.getColumnIndex(StockHeader.D_DATE)));
            binding.stockDate.setText(cursor.getString(cursor.getColumnIndex(StockHeader.D_STOCK_COUNT_DATE)));
            binding.narration.setText(cursor.getString(cursor.getColumnIndex(StockHeader.S_NARRATION)));

            if (!helper.GetWarehouseById(cursor.getString(cursor.getColumnIndex(StockHeader.I_WAREHOUSE))).equals("")) {
                SetWarehouseSpinner(cursor.getInt(cursor.getColumnIndex(StockHeader.I_WAREHOUSE)));
            } else {
                requireActivity().finish();
            }
        }
        }catch (Exception e){
            String fnName=new Object() {}.getClass().getName()+"."+ Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName();
            Tools.logWrite(fnName,e, requireContext());
        }

    }

    private void SetWarehouseSpinner(int warehouse_id) {
        if(warehouseList.size()!=0){
            for(int i = 0;i<warehouseList.size();i++){
                if(warehouseList.get(i).getMasterId()==(warehouse_id)){
                    binding.warehouseSpinner.setSelection(i);
                }
            }
        }
    }

    private void datePick(EditText date, String publicDate) {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


                String StringDate = Tools.checkDigit(dayOfMonth)+
                        "-" +
                        Tools.checkDigit(month + 1) +
                        "-"+
                        year;
                date.setText(StringDate);
                if(publicDate.equals("date")){
                PublicData.date=StringDate;
                }else {
                    PublicData.stock_date=StringDate;
                }

                Log.d("public_Date", PublicData.date+" "+PublicData.stock_date);
            }
        };
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), onDateSetListener, year, month, day);
        datePickerDialog.show();
    }

    private void LoadWarehouse() {
        try {


            Cursor cursor = helper.GetWarehouse();
            warehouseList.clear();
            if (cursor != null) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    if (!cursor.getString(cursor.getColumnIndex("Name")).equals(" "))
                        warehouseList.add(new Warehouse(cursor.getInt(cursor.getColumnIndex("MasterId")),
                                cursor.getString(cursor.getColumnIndex("Name"))));

                    cursor.moveToNext();
                    if (cursor.getCount() == i + 1) {
                        binding.warehouseSpinner.setAdapter(adapter);
                    }

                }
            }
        }catch (Exception e){
            String fnName=new Object() {}.getClass().getName()+"."+ Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName();
            Tools.logWrite(fnName,e,requireContext());
        }
    }


    private static class Warehouse {

        final int MasterId;
        final String Name;

        public Warehouse(int masterId, String name) {
            MasterId = masterId;
            Name = name;
        }

        public String getName() {
            return Name;
        }

        public int getMasterId() {
            return MasterId;
        }

    }


    private class WarehouseAdapter extends BaseAdapter {
        List<Warehouse>list;

        public WarehouseAdapter(List<Warehouse> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position).MasterId;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(requireActivity()).inflate(R.layout.warehouse_item,parent,false);
            TextView warehouse = view.findViewById(R.id.warehouse);
            warehouse.setText(list.get(position).Name);
            return view;
        }
    }


}
