package com.sangsolutions.stock.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
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
import com.sangsolutions.stock.Adapter.Singleton.StockCountProductSingleton;
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
    int iId;
    String EditMode = "";
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
    Animation slideUp, slideDown;
    boolean selectionActive = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= BodyFrgmentBinding.inflate(getLayoutInflater());
        helper=new DatabaseHelper(requireContext());

        binding.fabDelete.setVisibility(View.GONE);
        binding.fabClose.setVisibility(View.GONE);
        binding.fabAdd.setVisibility(View.VISIBLE);
        slideDown = AnimationUtils.loadAnimation(getContext(), R.anim.move_down);
        slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.move_up);

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
                iId = getArguments().getInt("iId", 0);

                Log.d("lllllB", EditMode + " " + iId);

                if (EditMode.equals("edit")) {
                    getEditData();
                }


            } else {
                Toast.makeText(getActivity(), "Didn't have data to load!", Toast.LENGTH_SHORT).show();
                requireActivity().finish();
            }


            binding.fabAdd.setOnClickListener(v -> newAlert());

            binding.fabDelete.setOnClickListener(v -> deleteAlert());
            binding.fabClose.setOnClickListener(v -> closeSelection());




        }catch (Exception e){
            String fnName=new Object() {}.getClass().getName()+"."+ Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName();
            Tools.logWrite(fnName,e,requireContext());
        }



        return binding.getRoot();

    }

    private void getEditData() {
        try {
        Cursor cursor = helper.GetBodyData(iId);
        if(cursor!=null){
            if(cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    mainList.add(new StockBody(
                            cursor.getString(cursor.getColumnIndex(StockBody.PRODUCT)),
                            "",
                            cursor.getString(cursor.getColumnIndex(StockBody.F_QTY)),
                            cursor.getString(cursor.getColumnIndex(StockBody.S_UNIT)),
                            cursor.getString(cursor.getColumnIndex(StockBody.I_PRODUCT)),
                            cursor.getString(cursor.getColumnIndex(StockBody.S_REMARKS)),
                            cursor.getString(cursor.getColumnIndex(StockBody.BARCODE))
                    ));
                    cursor.moveToNext();

                    if (cursor.getCount() == i + 1) {
                        adapter.notifyDataSetChanged();
                        StockCountProductSingleton.getInstance().setList(mainList);
                        adapter.setOnClickListener(new BodyAdapter.OnClickListener() {
                            @Override
                            public void onItemClick(StockBody product, int pos) {

                                if (!selectionActive) {
                                    EditProduct=true;
                                    position_body_Edit=pos;
                                    EditProductField(product,pos);
                                } else {
                                    enableActionMode(pos);
                                }


                            }

                            @Override
                            public void onItemDeleteClickListener(int pos) {

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
                requireActivity().finish();
                Log.d("error","Body have no data to load!");
            }
        }
        }catch (Exception e){
            String fnName=new Object() {}.getClass().getName()+"."+ Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName();
            Tools.logWrite(fnName,e, requireContext());
        }
    }

    private void deleteAlert() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Delete?")
                .setMessage("Do you want to Delete " + adapter.getSelectedItemCount() + " items?")
                .setPositiveButton("YES", (dialog, which) -> DeleteItems())
                .setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void DeleteItems() {

        List<Integer> listSelectedItem = adapter.getSelectedItems();
        for (int i =listSelectedItem.size()-1;i>=0;i--) {
            for (int j =mainList.size()-1;j>=0;j--) {
                if (listSelectedItem.get(i) == j) {
                    Log.d("listSelectedItem",j+" "+mainList.get(j).getName());
                    mainList.remove(j);
                }
            }

            if (i + 1 == listSelectedItem.size()) {
                adapter.notifyDataSetChanged();
                binding.rvProduct.setAdapter(adapter);
                StockCountProductSingleton.getInstance().setList(mainList);
                closeSelection();
            }
        }
    }

    private void newAlert() {

        try {
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


        productBinding.barcodeI.setOnClickListener(v -> barcodeScanningChecking());

        productBinding.closeAlert.setOnClickListener(v -> {
            mainAlert.dismiss();
            EditProduct=false;
            clearBody();
        });

        productBinding.add.setOnClickListener(v -> {
            if(iProduct!=0 && !productBinding.product.getText().toString().trim().equals("")){
                if(!productBinding.qty.getText().toString().trim().equals("")){
                    String sUnit=productBinding.unit.getSelectedItem().toString();
                    if(!sUnit.trim().equals("")){
                        addProduct();
                    }else {
                        Toast.makeText(requireContext(), "Select Unit", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    productBinding.qty.setError("Enter Valid Qty");
                }
            }else {
                productBinding.product.setError("Enter Valid Product");
            }
        });
        }catch (Exception e){
            String fnName=new Object() {}.getClass().getName()+"."+ Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName();
            Tools.logWrite(fnName,e, requireContext());
        }

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

                if (!selectionActive) {
                    EditProduct=true;
                    position_body_Edit=pos;
                    EditProductField(product,pos);
                } else {
                    enableActionMode(pos);
                }


            }

            @Override
            public void onItemDeleteClickListener(int pos) {

            }

            @Override
            public void onItemLongClick(int pos) {
                enableActionMode(pos);
                selectionActive = true;
            }
        });



    }

    private void enableActionMode(int position) {
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();

        if (count == 1 && binding.fabDelete.getVisibility() != View.VISIBLE) {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                binding.fabDelete.startAnimation(slideUp);
                binding.fabClose.startAnimation(slideUp);
                binding.fabAdd.startAnimation(slideDown);
                binding.fabDelete.setVisibility(View.VISIBLE);
                binding.fabClose.setVisibility(View.VISIBLE);
                binding.fabAdd.setVisibility(View.GONE);
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
        binding.fabAdd.startAnimation(slideUp);
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            binding.fabDelete.setVisibility(View.GONE);
            binding.fabClose.setVisibility(View.GONE);
            binding.fabAdd.setVisibility(View.VISIBLE);
        }, 300);
        selectionActive = false;
    }

    private void EditProductField(StockBody product, int pos) {
        newAlert();
        productBinding.product.setText(product.getName());
        iProduct= Integer.parseInt(product.getiProduct());
        productBinding.product.dismissDropDown();
        productBinding.barcode.setText(product.getBarcode());
        productBinding.qty.setText(product.getQty());
        productBinding.remarks.setText(product.getsRemarks());
        SetUnit(helper.GetProductUnit(product.getBarcode()),pos);

    }

    private void clearBody() {
        EditProduct =false;
        productBinding.qty.setText("");
        productBinding.barcode.setText("");
        productBinding.remarks.setText("");
        productBinding.product.setText("");
        productBinding.product.requestFocus();
        SetUnit("",-1);
        StockCountProductSingleton.getInstance().setList(mainList);
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
                        Cursor cursor = helper.GetProductInfoByBarcode(barcode.valueAt(0).displayValue);

                        if(cursor !=null && cursor.moveToFirst()){
                            iProduct=cursor.getInt(cursor.getColumnIndex(Product.I_ID));
                            productBinding.product.setText(cursor.getString(cursor.getColumnIndex(Product.PRODUCT)));
                            productBinding.product.dismissDropDown();
                            productBinding.qty.requestFocus();
                            productBinding.surfaceView.setVisibility(View.GONE);
                        }
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

                    productsAdapter.setOnClickListener((product, position) -> {
                        productBinding.barcode.setText("");
                        iProduct=product.getMasterId();
                        productBinding.product.setText(product.getName());
                        productBinding.qty.requestFocus();
                        productBinding.barcode.setText(product.getBarcode());
                        productBinding.product.dismissDropDown();
                        SetUnit(product.getUnit(), -1);
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
