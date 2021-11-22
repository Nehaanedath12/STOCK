package com.sangsolutions.stock.Adapter.BodyAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.sangsolutions.stock.R;

import java.util.ArrayList;
import java.util.List;

public class BodyAdapter extends RecyclerView.Adapter<BodyAdapter.ViewHolder> {

    private final Context context;
    private final List<StockBody> list;
    private OnClickListener onClickListener;
    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        selected_items = new SparseBooleanArray();
    }

    public BodyAdapter(Context context, List<StockBody> list) {
        this.context = context;
        this.list = list;
    }


    private void resetCurrentIndex() {
        current_selected_idx = -1;
    }

    public void toggleSelection(int pos) {
        current_selected_idx = pos;
        if (selected_items.get(pos, false)) {
            selected_items.delete(pos);
        } else {
            selected_items.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selected_items.clear();
        notifyDataSetChanged();
    }


    public int getSelectedItemCount() {
        return selected_items.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selected_items.size());
        for (int i = 0; i < selected_items.size(); i++) {
            items.add(selected_items.keyAt(i));
        }
        return items;
    }


    private void toggleCheckedIcon(ViewHolder holder, int position) {
        if (selected_items.get(position, false)) {
            holder.img_check.setVisibility(View.VISIBLE);
            holder.parent.setBackgroundColor(Color.parseColor("#fca1a3"));
        } else {
            holder.img_check.setVisibility(View.GONE);
            holder.parent.setBackgroundColor(Color.WHITE);
        }
        if (current_selected_idx == position) resetCurrentIndex();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final StockBody productList = list.get(position);
        holder.Name.setText(list.get(position).getName());
        holder.Code.setText(list.get(position).getCode());
        holder.Qty.setText(list.get(position).getQty());
        holder.unit.setText(list.get(position).getUnit());
        holder.remarks.setText(list.get(position).getsRemarks());

//        if(!EditMode.equals("view")) {
//            holder.delete.setVisibility(View.VISIBLE);
//            holder.delete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onClickListener.onItemDeleteClickListener(position);
//                }
//            });
            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onItemClick(productList, position);
                }
            });
            holder.parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (onClickListener == null) return false;
                    else {
                        onClickListener.onItemLongClick(position);
                    }
                    return true;
                }
            });
//        } else {
//            holder.delete.setVisibility(View.GONE);
//        }
        toggleCheckedIcon(holder, position);
//         displayImage(holder, productList);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onItemClick(StockBody product, int pos);

        void onItemDeleteClickListener(int pos);

        void onItemLongClick(int pos);
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView Name;
        final TextView Code;
        final TextView Qty;
        final TextView unit;
        final TextView remarks;
        final ImageView delete;
        final LinearLayout parent;
        final ImageView img_check;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.product_name);
            Code = itemView.findViewById(R.id.product_code);
            Qty = itemView.findViewById(R.id.product_qty);
            unit = itemView.findViewById(R.id.product_unit);
            remarks = itemView.findViewById(R.id.product_remarks);
            delete = itemView.findViewById(R.id.delete);
            parent = itemView.findViewById(R.id.parent);
            img_check = itemView.findViewById(R.id.check);
        }
    }
}


