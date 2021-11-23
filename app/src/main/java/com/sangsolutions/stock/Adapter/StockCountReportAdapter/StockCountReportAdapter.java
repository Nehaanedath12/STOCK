package com.sangsolutions.stock.Adapter.StockCountReportAdapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.stock.R;

import java.util.List;

public class StockCountReportAdapter extends RecyclerView.Adapter<StockCountReportAdapter.ViewHolder> {
    final List<StockCountReport> list;
    final Context context;


    public StockCountReportAdapter(List<StockCountReport> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.stock_count_report_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.vno.setText(list.get(position).getiVoucherNo());
        holder.doc_date.setText(list.get(position).getDocDate());
        holder.warehouse.setText(list.get(position).getWarehouse());
        holder.name.setText(list.get(position).getName());
        holder.code.setText(list.get(position).getCode());
        holder.qty.setText(list.get(position).getfQty());
        holder.unit.setText(list.get(position).getsUnit());
        holder.remarks.setText(list.get(position).getsRemarks());
        if (position % 2 == 0) {
            holder.rl_parent.setBackgroundColor(Color.rgb(234, 234, 234));
        } else {

            holder.rl_parent.setBackgroundColor(Color.rgb(255, 255, 255));
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView vno;
        final TextView doc_date;
        final TextView warehouse;
        final TextView name;
        final TextView code;
        final TextView qty;
        final TextView unit;
        final TextView remarks;
        final RelativeLayout rl_parent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            vno = itemView.findViewById(R.id.vno);
            doc_date = itemView.findViewById(R.id.doc_date);
            warehouse = itemView.findViewById(R.id.warehouse);
            name = itemView.findViewById(R.id.name);
            code = itemView.findViewById(R.id.code);
            qty = itemView.findViewById(R.id.qty);
            unit = itemView.findViewById(R.id.unit);
            remarks = itemView.findViewById(R.id.narration);
            rl_parent = itemView.findViewById(R.id.parent);


        }
    }
}
