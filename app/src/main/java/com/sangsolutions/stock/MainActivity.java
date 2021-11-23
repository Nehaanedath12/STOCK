package com.sangsolutions.stock;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.sangsolutions.stock.Adapter.BodyAdapter.StockBody;
import com.sangsolutions.stock.Adapter.BodyAdapter.StockHeader;
import com.sangsolutions.stock.Database.DatabaseHelper;
import com.sangsolutions.stock.Report.ReportSelectionActivity;
import com.sangsolutions.stock.Service.GetProductService;
import com.sangsolutions.stock.StockAllocation.StockAllocationHistoryActivity;
import com.sangsolutions.stock.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    MainMenuAdapter menuAdapter;
    List<MainMenu> menuList;
    DatabaseHelper helper;
    AlertDialog alertDialog;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        preferences = getSharedPreferences("sync",MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(Commons.STOCK_COUNT_FINISHED,"init").apply();

        menuList = new ArrayList<>();
        menuAdapter = new MainMenuAdapter(menuList);
        helper=new DatabaseHelper(this);

        menuList.add(new MainMenu(R.drawable.ic_sync, "Sync"));
        menuList.add(new MainMenu(R.drawable.ic_stock_count, "Stock Count"));
        menuList.add(new MainMenu(R.drawable.ic_report, "Report"));


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view1 = LayoutInflater.from(this).inflate(R.layout.progress_bar, null, false);
        builder.setView(view1);
        builder.setCancelable(false);
        alertDialog = builder.create();
        
        try {
            if(menuList.size()>0){
                binding.menuGrid.setAdapter(menuAdapter);
                binding.menuGrid.setOnItemClickListener((parent, view12, position, id) -> {
                        switch (menuList.get(position).text) {

                            case "Stock Count":
                                startActivity(new Intent(getApplicationContext(), StockAllocationHistoryActivity.class));
                                break;
                            case "Sync":
                                if(Tools.isConnected(this)) {
                                    PostStockCountData();
                                }else {
                                    Toast.makeText(this, "Please check your internet and try again", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case "Report":
                                startActivity(new Intent(getApplicationContext(), ReportSelectionActivity.class));
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + menuList.get(position).text);
                        }
                });



            }

            binding.logout.setOnClickListener(v -> {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setTitle("Logout?")
                        .setMessage("Do you want to Logout?")
                        .setPositiveButton("YES", (dialog, which) -> {
                            if(helper.DeleteCurrentUser()){

                                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                                finish();
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
            });

        }catch (Exception e){
            String fnName=new Object() {}.getClass().getName()+"."+ Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName();
            Tools.logWrite(fnName,e,this);
        }
    }

    private void PostStockCountData() {
        try {
        alertDialog.show();
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                String cursor_userId=helper.GetUserId();
                Cursor cursorHeader=helper.GetStockCountHeader();

                if(cursorHeader !=null && cursorHeader.getCount() > 0) {
                    for (int i = 0; i < cursorHeader.getCount(); i++) {
                        try {
                            JSONObject jsonObjectMain = new JSONObject();
                            int iId = cursorHeader.getInt(cursorHeader.getColumnIndex(StockHeader.I_ID));

                            Log.d("iTransIdd",iId+"");

                            jsonObjectMain.put("iTransId", 0);
                            jsonObjectMain.put("iVoucherNo", cursorHeader.getString(cursorHeader.getColumnIndex(StockHeader.S_VOUCHER_NO)));
                            jsonObjectMain.put("iDocDate",Tools.dateFormat(cursorHeader.getString(cursorHeader.getColumnIndex(StockHeader.D_DATE))));
                            jsonObjectMain.put("iWarehouse", cursorHeader.getInt(cursorHeader.getColumnIndex(StockHeader.I_WAREHOUSE)));
                            jsonObjectMain.put("iUser", cursor_userId);
                            jsonObjectMain.put("iProcessDate", cursorHeader.getString(cursorHeader.getColumnIndex(StockHeader.D_PROCESSED_DATE)));
                            jsonObjectMain.put("sNarration", cursorHeader.getString(cursorHeader.getColumnIndex(StockHeader.S_NARRATION)));
                            jsonObjectMain.put("iStockDate", Tools.dateFormat(cursorHeader.getString(cursorHeader.getColumnIndex(StockHeader.D_STOCK_COUNT_DATE))));

                            Cursor cursorBody=helper.GetBodyData(iId);
                            JSONArray jsonArray = new JSONArray();
                            if(cursorBody!=null && cursorBody.getCount()>0){
                                for (int j=0;j<cursorBody.getCount();j++){
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("iProduct", cursorBody.getInt(cursorBody.getColumnIndex(StockBody.I_PRODUCT)));
                                    jsonObject.put("fQty", cursorBody.getString(cursorBody.getColumnIndex(StockBody.F_QTY)));
                                    jsonObject.put("sUnit",cursorBody.getString(cursorBody.getColumnIndex(StockBody.S_UNIT)));
                                    jsonObject.put("sRemarks",cursorBody.getString(cursorBody.getColumnIndex(StockBody.S_REMARKS)));

                                    jsonArray.put(jsonObject);
                                    cursorBody.moveToNext();
                                }
                            }
                            jsonObjectMain.put("Body", jsonArray);
                            Log.d("jsonObjectMain "+iId,jsonObjectMain.toString());
                            uploadToAPI(jsonObjectMain,iId,cursorHeader);

                        } catch (JSONException e) {
                            String fnName=new Object() {}.getClass().getName()+"."+ Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName();
                            Tools.logWrite(fnName,e,MainActivity.this);
                        }
                        cursorHeader.moveToNext();


                        if(cursorHeader.getCount()==i+1){
                            alertDialog.dismiss();
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(() -> Toast.makeText(MainActivity.this, "Posting completed SuccessFully!", Toast.LENGTH_SHORT).show());
                        }

                    }
                }else {
                    alertDialog.dismiss();
                    editor.putString(Commons.STOCK_COUNT_FINISHED,"error").apply();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> Toast.makeText(MainActivity.this, "Nothing to Post!", Toast.LENGTH_SHORT).show());
                }


                return null;
            }
        };
        asyncTask.execute();
        } catch (Exception e) {
            alertDialog.dismiss();
            String fnName=new Object() {}.getClass().getName()+"."+ Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName();
            Tools.logWrite(fnName,e,MainActivity.this);
        }
    }

    private void uploadToAPI(JSONObject jsonObjectMain, int iId, Cursor cursorHeader) {
        try {
            if(Tools.isConnected(this)) {
                AndroidNetworking.post("http://" + new Tools().getIP(MainActivity.this) + URLs.PostStockCount)
                        .addJSONObjectBody(jsonObjectMain)
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {

                                Log.d("UploadResponse", response);
                                try {
                                    if (Integer.parseInt(response) > 0) {
                                        helper.DeleteStockwithId(String.valueOf(iId));

                                    }
                                } catch (Exception e) {
                                    String fnName = new Object() {
                                    }.getClass().getName() + "." + Objects.requireNonNull(new Object() {
                                    }.getClass().getEnclosingMethod()).getName();
                                    Tools.logWrite(fnName, e, MainActivity.this);
                                }

                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.d("error", anError.toString());

                            }
                        });
            }else {
                Toast.makeText(this, "Please check your internet and try again", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            String fnName=new Object() {}.getClass().getName()+"."+ Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName();
            Tools.logWrite(fnName,e,MainActivity.this);
        }

    }


    static class MainMenu{
        int Image;
        String text;

        public MainMenu(int image, String text) {
            Image = image;
            this.text = text;
        }

        public int getImage() {
            return Image;
        }

        public void setImage(int image) {
            Image = image;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    class MainMenuAdapter extends BaseAdapter {

        List<MainMenu> list;

        public MainMenuAdapter(List<MainMenu> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint("ViewHolder") View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.menu_item,parent,false);
            TextView text = view.findViewById(R.id.text);
            ImageView image = view.findViewById(R.id.image);

            text.setText(list.get(position).getText());
            image.setImageResource(list.get(position).getImage());
            return view;
        }
    }
}