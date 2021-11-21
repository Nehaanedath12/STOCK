package com.sangsolutions.stock.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.sangsolutions.stock.Adapter.BodyAdapter.StockBody;
import com.sangsolutions.stock.Adapter.BodyAdapter.BodyAdapter;
import com.sangsolutions.stock.Adapter.ProductAdapter;
import com.sangsolutions.stock.Adapter.UnitAdapter;
import com.sangsolutions.stock.Database.DatabaseHelper;
import com.sangsolutions.stock.Database.Product;
import com.sangsolutions.stock.R;
import com.sangsolutions.stock.Tools;
import com.sangsolutions.stock.databinding.BodyFrgmentBinding;
import com.sangsolutions.stock.databinding.BodyProductAlertBinding;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StockCountBodyFragment extends Fragment {

    BodyFrgmentBinding binding;
    DatabaseHelper helper;
    String voucherNo;
    java.util.Date c;
    int iId;
    String EditMode = "";
    SimpleDateFormat df;
    Animation move_down_anim, move_up_anim;
    BodyProductAlertBinding productBinding;
    AlertDialog mainAlert;
    List<Product>productsList;
    ProductAdapter productsAdapter;
    int iProduct;
    List<String> unitList;
    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;
    List<StockBody>mainList;
    BodyAdapter adapter;
    boolean EditProduct=false;
    int position_body_Edit=0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= BodyFrgmentBinding.inflate(getLayoutInflater());
        helper=new DatabaseHelper(requireContext());
        move_down_anim = AnimationUtils.loadAnimation(requireActivity(), R.anim.move_down);
        move_up_anim = AnimationUtils.loadAnimation(requireActivity(), R.anim.move_up);

        binding.delete.setVisibility(View.GONE);
        binding.fabClose.setVisibility(View.GONE);
        binding.fabAdd.setVisibility(View.VISIBLE);

        productBinding =BodyProductAlertBinding.inflate(getLayoutInflater(),null,false);
        AlertDialog.Builder builderMain=new AlertDialog.Builder(requireContext(),android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        builderMain.setView(productBinding.getRoot());
        mainAlert=builderMain.create();
        mainAlert.setCancelable(false);

        productsList=new ArrayList<>();
        productsAdapter =new ProductAdapter(requireActivity(),productsList);
        unitList=new ArrayList<>();

        mainList =new ArrayList<>();
        adapter=new BodyAdapter(requireActivity(), mainList);
        binding.rvProduct.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.rvProduct.setAdapter(adapter);

        try {
            if (getArguments() != null) {
                EditMode = getArguments().getString("EditMode");
                iId = getArguments().getInt("voucherNo", 0);

                Log.d("lllllB", EditMode + " " + iId);

                if (EditMode.equals("edit")) {

                } else if (EditMode.equals("new")) {

                }


            } else {
                Toast.makeText(getActivity(), "Didn't have data to load!", Toast.LENGTH_SHORT).show();
                requireActivity().finish();
            }


            binding.fabAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newAlert();
                }
            });




        }catch (Exception e){
            String fnName=new Object() {}.getClass().getName()+"."+ Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName();
            Tools.logWrite(fnName,e,requireContext());
        }



        return binding.getRoot();

    }

    private void newAlert() {

        mainAlert.show();
        productBinding.product.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                productBinding.product.setError(null);
                if(s.toString().equals("")){
                    iProduct=0;
                    productBinding.barcode.setText("");
                    SetUnit("",-1);

                }
                GetProduct(s.toString());
            }
        });

        productBinding.product.setThreshold(1);
        productBinding.product.setAdapter(productsAdapter);
        if (productBinding.surfaceView.getVisibility() == View.VISIBLE) {
            productBinding.product.dismissDropDown();
        }


        productBinding.barcodeI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcodeScanningChecking();
            }
        });

        productBinding.closeAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainAlert.dismiss();
                EditProduct=false;
            }
        });

        productBinding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iProduct!=0 && !productBinding.product.getText().toString().trim().equals("")){
                    if(!productBinding.qty.getText().toString().trim().equals("")){

                        addProduct();
                    }else {
                        productBinding.qty.setError("Empty");
                    }
                }else {
                    productBinding.product.setError("Empty");
                }
            }
        });

    }

    private void addProduct() {


        StockBody product=new StockBody(productBinding.product.getText().toString().trim(),
                "",
                productBinding.qty.getText().toString(),
                productBinding.unit.getSelectedItem().toString(),
                String.valueOf(iProduct),
                productBinding.remarks.getText().toString(),
                productBinding.barcode.getText().toString());
        if(EditProduct){
            mainList.set(position_body_Edit,product);
        }else {
            mainList.add(product);
        }


        adapter.notifyDataSetChanged();
        clearBody();
//        StockCountProductSingleton.getInstance().setList(listProduct);

        adapter.setOnClickListener(new BodyAdapter.OnClickListener() {
            @Override
            public void onItemClick(StockBody product, int pos) {

                EditProduct=true;
                position_body_Edit=pos;
                EditProductField(product,pos);
            }

            @Override
            public void onItemDeleteClickListener(int pos) {

            }

            @Override
            public void onItemLongClick(int pos) {

            }
        });



    }

    private void EditProductField(StockBody product, int pos) {
        newAlert();
        productBinding.product.setText(product.getName());
        iProduct= Integer.parseInt(product.getiProduct());
        productBinding.product.dismissDropDown();
        productBinding.barcode.setText(product.getBarcode());
        productBinding.qty.setText(product.getQty());
        productBinding.remarks.setText(product.getsRemarks());
        SetUnit(product.getUnit(),pos);

    }

    private void clearBody() {
        EditProduct =false;
        productBinding.qty.setText("");
        productBinding.barcode.setText("");
        productBinding.remarks.setText("");
        productBinding.product.setText("");
        productBinding.product.requestFocus();
        SetUnit("",-1);
    }

    private void barcodeScanningChecking() {


        if(ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.CAMERA},101);
        }
        else {

            if (productBinding.surfaceView.getVisibility() == View.VISIBLE) {
                if(cameraSource!=null){
                    cameraSource.stop();
                }
                productBinding.surfaceView.setVisibility(View.GONE);
            }
            else if(productBinding.surfaceView.getVisibility()==View.GONE){
                productBinding.surfaceView.setVisibility(View.VISIBLE);
                barcodeScanning();
            }
        }
    }

    private void barcodeScanning() {
        barcodeDetector = new BarcodeDetector.Builder(requireActivity())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        cameraSource = new CameraSource.Builder(requireActivity(), barcodeDetector)
                .setRequestedPreviewSize(1080, 1920)
                .setAutoFocusEnabled(true)
                .build();

        productBinding.surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {

                if (!barcodeDetector.isOperational()) {
                    Log.d("Detector", "Detector dependencies are not yet available.");
                } else {
                    try {
                        if (cameraSource != null) {
                            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            cameraSource.start(productBinding.surfaceView.getHolder());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                //  barcodeDetector.release();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Log.d("msg", "Barcode scanning stopped");
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcode = detections.getDetectedItems();
                if (barcode.size() > 0) {
                    requireActivity().runOnUiThread(() -> {
                        productBinding.barcode.setText(barcode.valueAt(0).displayValue);
                        SetUnit(helper.GetProductUnit(barcode.valueAt(0).displayValue),-1);

                    });
                }
            }
        });
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

                    productsAdapter.setOnClickListener(new ProductAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(Product product, int position) {
                            productBinding.barcode.setText("");
                            iProduct=product.getMasterId();
                            productBinding.product.setText(product.getName());
                            productBinding.barcode.setText(product.getBarcode());
                            productBinding.product.dismissDropDown();
                            SetUnit(product.getUnit(), -1);
                        }
                    });

                }
            }
        }
    }

    public void SetUnit(String units, int position) {

        unitList = Arrays.asList(units.split("\\s*,\\s*"));
        UnitAdapter unitAdapter = new UnitAdapter(unitList, requireActivity());
        productBinding.unit.setAdapter(unitAdapter);

        if (mainList.size() > 0) {
            if (position != -1)
                for (int i = 0; i < unitList.size(); i++) {
                    if (unitList.get(i).equals(mainList.get(position).getUnit())) {
                        productBinding.unit.setSelection(i);
                    }
                }
        }
    }
}
