package com.sangsolutions.stock.Report;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.sangsolutions.stock.Adapter.ProductAdapter;
import com.sangsolutions.stock.Database.DatabaseHelper;
import com.sangsolutions.stock.Database.Product;
import com.sangsolutions.stock.PublicData;
import com.sangsolutions.stock.R;
import com.sangsolutions.stock.StockAllocation.StockAllocationActivity;
import com.sangsolutions.stock.Tools;
import com.sangsolutions.stock.databinding.ActivityMainBinding;
import com.sangsolutions.stock.databinding.ActivityReportSelectionBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReportSelectionActivity extends AppCompatActivity {

    ActivityReportSelectionBinding binding;
    List<Product> productsList;
    ProductAdapter productsAdapter;
    int iProduct=0;
    DatabaseHelper helper;
    SimpleDateFormat df;
    java.util.Date c;
    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportSelectionBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        c = Calendar.getInstance().getTime();
        df = new SimpleDateFormat("dd-MM-yyyy");
        binding.from.setText(df.format(c));
        binding.to.setText(df.format(c));

        productsList=new ArrayList<>();
        productsAdapter =new ProductAdapter(this,productsList);
        helper=new DatabaseHelper(this);

        binding.product.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.product.setError(null);
                if(s.toString().equals("")){
                    iProduct=0;
                }
                GetProduct(s.toString());
            }
        });

        binding.product.setThreshold(1);
        binding.product.setAdapter(productsAdapter);
        binding.from.setOnClickListener(v -> datePick(binding.from));
        binding.to.setOnClickListener(v -> datePick(binding.to));

        binding.search.setOnClickListener(v -> {
            Intent intent=new Intent(getApplicationContext(), ReportActivity.class);
            intent.putExtra("FromDate", binding.from.getText().toString());
            intent.putExtra("ToDate",binding.to.getText().toString());
            intent.putExtra("iProduct",iProduct);
            startActivity(intent);
        });

    }

    private void datePick(EditText date) {
        DatePickerDialog.OnDateSetListener onDateSetListener = (view, year, month, dayOfMonth) -> {


            String StringDate = Tools.checkDigit(dayOfMonth)+
                    "-" +
                    Tools.checkDigit(month + 1) +
                    "-"+
                    year;
            date.setText(StringDate);
        };
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, onDateSetListener, year, month, day);
        datePickerDialog.show();
    }

    private void GetProduct(String productKeyword) {
        productsList.clear();
        Cursor cursor=helper.GetProductInfo(productKeyword);
        if(cursor!=null && !productKeyword.equals("")) {
            if (cursor.getCount() > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    Product products = new Product();
                    products.setMasterId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Product.I_ID))));
                    products.setName(cursor.getString(cursor.getColumnIndex(Product.PRODUCT)));
                    products.setCode(cursor.getString(cursor.getColumnIndex(Product.CODE)));
                    products.setBarcode(cursor.getString(cursor.getColumnIndex(Product.BARCODE)));
                    products.setUnit(cursor.getString(cursor.getColumnIndex(Product.UNIT)));
                    productsList.add(products);
                    cursor.moveToNext();
                    if (i + 1 == cursor.getCount()) {
                        productsAdapter.notifyDataSetChanged();
                    }

                    productsAdapter.setOnClickListener((product, position) -> {
                        iProduct=product.getMasterId();
                        binding.product.setText(product.getName());
                        binding.product.dismissDropDown();
                    });

                }
            }
        }
    }
}