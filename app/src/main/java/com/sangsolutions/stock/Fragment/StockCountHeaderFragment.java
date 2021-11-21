package com.sangsolutions.stock.Fragment;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
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
    String voucherNo="",Date ="",dStockCountDate="", Narration ="";
    java.util.Date c;
    int iId;
    List<Warehouse> list;
    WarehouseAdapter adapter;
    String EditMode = "";
    SimpleDateFormat df;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding=HeaderFrgmentBinding.inflate(getLayoutInflater());


        c = Calendar.getInstance().getTime();
        df = new SimpleDateFormat("dd-MM-yyyy");

        helper=new DatabaseHelper(requireContext());
        list = new ArrayList<>();
        adapter = new WarehouseAdapter(list);
        LoadWarehouse();
        try {
            if (getArguments() != null) {
                EditMode = getArguments().getString("EditMode");
                iId = getArguments().getInt("voucherNo", 0);

                Log.d("lllllH", EditMode + " " + iId);

                if (EditMode.equals("edit")) {
//                warehouse_id = getArguments().getString("warehouse");
//                if (!helper.GetWarehouseById(warehouse_id).equals("")) {
//                    SetWarehouseSpinner(warehouse_id);
//                } else {
//                    Objects.requireNonNull(getActivity()).finish();
//                }
//                setData(voucherNo);

                } else if (EditMode.equals("new")) {
                    voucherNo = "S-" + DateFormat.format("ddMMyy-HHmmss", new Date());
                    binding.voucherNo.setText("Voucher No :" + voucherNo);
                    binding.date.setText(df.format(c));
                    binding.stockDate.setText(df.format(c));
                    PublicData.date = Tools.dateFormat(df.format(c));
                    PublicData.stock_date = Tools.dateFormat(df.format(c));
                }


            } else {
                Toast.makeText(getActivity(), "Didn't have data to load!", Toast.LENGTH_SHORT).show();
                requireActivity().finish();
            }


            binding.warehouseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    PublicData.warehouse = list.get(adapterView.getSelectedItemPosition()).getMasterId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            binding.date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePick(binding.date,"date");
                }
            });
            binding.stockDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePick(binding.stockDate,"stockDate");
                }
            });





        }catch (Exception e){
            String fnName=new Object() {}.getClass().getName()+"."+ Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName();
            Tools.logWrite(fnName,e,requireContext());
        }



        return binding.getRoot();
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
                PublicData.date=Tools.dateFormat(StringDate);
                }else {
                    PublicData.stock_date=Tools.dateFormat(StringDate);
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
            list.clear();
            if (cursor != null) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    if (!cursor.getString(cursor.getColumnIndex("Name")).equals(" "))
                        list.add(new Warehouse(cursor.getString(cursor.getColumnIndex("MasterId")), cursor.getString(cursor.getColumnIndex("Name"))));

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

        final String MasterId;
        final String Name;

        public Warehouse(String masterId, String name) {
            MasterId = masterId;
            Name = name;
        }

        public String getMasterId() {
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
