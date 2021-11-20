package com.sangsolutions.stock;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.sangsolutions.stock.Service.GetProductService;
import com.sangsolutions.stock.Service.GetWareHouseService;

public class ScheduleJob {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void SyncProductData(Context context) {
        JobScheduler js =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        @SuppressLint("JobSchedulerService") JobInfo job = new JobInfo.Builder(
                0,
                new ComponentName(context, GetProductService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        assert js != null;
        js.schedule(job);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void SyncWarehouse(Context context) {
        JobScheduler js =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        @SuppressLint("JobSchedulerService") JobInfo job = new JobInfo.Builder(
                1,
                new ComponentName(context, GetWareHouseService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        assert js != null;
        js.schedule(job);
    }
}
