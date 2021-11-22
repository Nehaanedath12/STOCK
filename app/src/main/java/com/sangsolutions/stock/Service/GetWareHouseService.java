package com.sangsolutions.stock.Service;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.sangsolutions.stock.Commons;
import com.sangsolutions.stock.Database.DatabaseHelper;
import com.sangsolutions.stock.Database.Warehouse;
import com.sangsolutions.stock.Tools;
import com.sangsolutions.stock.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GetWareHouseService extends JobService {
    JobParameters params;
    DatabaseHelper helper;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    List<Warehouse> wareHouseList;

    @Override
    public boolean onStartJob(JobParameters params) {
        helper = new DatabaseHelper(this);
        AndroidNetworking.initialize(getApplicationContext());
        preferences = getSharedPreferences(Commons.PREFERENCE_SYNC,MODE_PRIVATE);
        editor = preferences.edit();
        wareHouseList =new ArrayList<>();
        GetWareHouse();
        this.params = params;
        return true;
    }

    private void GetWareHouse() {
        AndroidNetworking.get("http://"+new Tools().getIP(this)+ URLs.GetWarehouse)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        asyncWareHose(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        editor.putString(Commons.WAREHOUSE_FINISHED,"error").apply();
                        Log.d("GetResponseError",anError.getErrorDetail());
                    }
                });
    }

    private void asyncWareHose(JSONObject response) {

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    JSONArray jsonArray = new JSONArray(response.getString("Warehouse"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Warehouse w=new Warehouse();
                        w.setName(jsonObject.getString("Name"));
                        w.setMasterId(jsonObject.getString("MasterId"));
                        wareHouseList.add(w);
                        if(jsonArray.length()==i+1){
                            if(helper.InsertWareHose(wareHouseList)){
                                Log.d("GetResponseService", "WareHose Synced");
                            }

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    String fnName=new Object() {}.getClass().getName()+"."+ Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName();
                    Tools.logWrite(fnName,e,GetWareHouseService.this);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                editor.putString(Commons.WAREHOUSE_FINISHED,"true").apply();
                if (preferences.getString(Commons.PRODUCT_FINISHED, "false").equals("true")  &&
                        preferences.getString(Commons.WAREHOUSE_FINISHED, "false").equals("true")  ){
                    Log.d("insertEndStatus","success");
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(GetWareHouseService.this, "Synced", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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
