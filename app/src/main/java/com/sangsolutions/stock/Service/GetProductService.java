package com.sangsolutions.stock.Service;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.sangsolutions.stock.Commons;
import com.sangsolutions.stock.Database.DatabaseHelper;
import com.sangsolutions.stock.Database.Product;
import com.sangsolutions.stock.Tools;
import com.sangsolutions.stock.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GetProductService extends JobService {
    JobParameters params;
    DatabaseHelper helper;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    List<Product> productsList;

    @Override
    public boolean onStartJob(JobParameters params) {
        helper = new DatabaseHelper(this);
        AndroidNetworking.initialize(getApplicationContext());
        preferences = getSharedPreferences(Commons.PREFERENCE_SYNC,MODE_PRIVATE);
        Log.d("kkkk","oooooo");
        editor = preferences.edit();
        productsList=new ArrayList<>();
        GetProduct();
        this.params = params;
        return true;
    }

    private void GetProduct() {
        AndroidNetworking.get("http://"+new Tools().getIP(this)+ URLs.GetProducts)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("responseProduct",response.toString());
                        asyncProduct(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        editor.putString(Commons.PRODUCT_FINISHED,"error").apply();
                        Log.d("ResponseError",anError.getErrorDetail());
                    }
                });
    }

    private void asyncProduct(JSONObject response) {

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    JSONArray jsonArray = new JSONArray(response.getString("Products"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Product p=new Product();
                        p.setMasterId(jsonObject.getInt("MasterId"));
                        p.setCode(jsonObject.getString("Code"));
                        p.setName(jsonObject.getString("Name"));
                        p.setBarcode(jsonObject.getString("Barcode"));
                        p.setUnit(jsonObject.getString("unit"));
                        productsList.add(p);
                        Log.d("GetResponse", "Product "+i);
                        if(jsonArray.length()==i+1){
                            if(helper.InsertProduct(productsList)){
                                Log.d("GetResponseService", "Product Synced");
                            }

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                editor.putString(Commons.PRODUCT_FINISHED,"true").apply();
                jobFinished(params,false);
            }
        };
        asyncTask.execute();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
