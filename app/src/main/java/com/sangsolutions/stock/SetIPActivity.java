package com.sangsolutions.stock;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sangsolutions.stock.Database.DatabaseHelper;
import com.sangsolutions.stock.Database.User;
import com.sangsolutions.stock.databinding.ActivityMainBinding;
import com.sangsolutions.stock.databinding.ActivitySetIPBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SetIPActivity extends AppCompatActivity {

    ActivitySetIPBinding binding;
    Tools tools;
    AlertDialog alertDialog;
    DatabaseHelper helper;
    SharedPreferences.Editor editor;
    SharedPreferences preferences;
    int iType=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetIPBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view1 = LayoutInflater.from(this).inflate(R.layout.progress_bar, null, false);
        builder.setView(view1);
        builder.setCancelable(false);
        alertDialog = builder.create();

        iType=getIntent().getIntExtra("iType",0);

        helper=new DatabaseHelper(this);
        preferences = getSharedPreferences(Commons.PREFERENCE_SYNC,MODE_PRIVATE);
        editor = preferences.edit();




        tools = new Tools();
        if(!new Tools().getIP(this).isEmpty()){
            binding.ipAddress.setText(tools.getIP(this));
            if(iType==0){
                if(helper.GetLoginStatus()) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }

        }


        binding.save.setOnClickListener(view2 -> {

            if (binding.ipAddress.getText().toString().trim().equals("")) {
                Toast.makeText(SetIPActivity.this, "Enter Ip Address", Toast.LENGTH_SHORT).show();
            } else if (Tools.isConnected(SetIPActivity.this)) {
                alertDialog.show();
                GetUsers();
            }else {
                Toast.makeText(SetIPActivity.this, "Please try when You are Online", Toast.LENGTH_SHORT).show();
            }



        });

    }

    private void GetUsers() {

        try {

            AndroidNetworking.get("http://"+  binding.ipAddress.getText().toString().trim()+ URLs.GetUser)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d("responseUser",response.toString());
                            loadData(response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            alertDialog.dismiss();
                            Log.d("responseUser",anError.toString()+anError.getErrorDetail());
                            Toast.makeText(SetIPActivity.this, "Check Ip Address", Toast.LENGTH_SHORT).show();

                        }
                    });
        }catch (Exception e){
            if(Tools.isConnected(this)) {
                FirebaseCrashlytics.getInstance().recordException(e);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void loadData(JSONArray response) {

        User u = new User();
        try {
            JSONArray jsonArray = new JSONArray(response.toString());
            if(helper.DeleteUser()) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);


                    u.setsId(jsonObject.getString("iId"));
                    u.setsLoginName(jsonObject.getString("sLoginName"));
                    u.setsPassword(jsonObject.getString("sPassword"));
                    u.setsMenuIDs(jsonObject.getString("sMenus"));
                    boolean status = helper.InsertUsers(u);
                    alertDialog.dismiss();
                    if (status) {
                        if(tools.setIP(SetIPActivity.this,binding.ipAddress.getText().toString())){
                            Toast.makeText(SetIPActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                            editor.putString(Commons.WAREHOUSE_FINISHED,"error").apply();
                            editor.putString(Commons.PRODUCT_FINISHED,"error").apply();

                            syncData();
                            startActivity(new Intent(SetIPActivity.this,LoginActivity.class));
                            finishAffinity();

                        }

                        Log.d("Saved ", "Success");
                    } else {
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                        Log.d("failed", "error");
                    }



                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void syncData() {
        new ScheduleJob().SyncProductData(this);
        new ScheduleJob().SyncWarehouse(this);

    }
}