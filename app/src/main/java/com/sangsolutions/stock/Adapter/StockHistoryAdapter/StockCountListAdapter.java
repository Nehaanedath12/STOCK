package com.sangsolutions.stock.Adapter.StockHistoryAdapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.sangsolutions.stock.Adapter.Singleton.StockCountProductSingleton;
import com.sangsolutions.stock.PublicData;
import com.sangsolutions.stock.R;
import com.sangsolutions.stock.StockAllocation.StockAllocationActivity;
import com.sangsolutions.stock.Tools;

import java.util.ArrayList;
import java.util.List;

public class StockCountListAdapter extends RecyclerView.Adapter<StockCountListAdapter.ViewHolder> {
final List<StockCountList> list;
final Context context;
    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;
    private OnClickListener onClickListener;


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        selected_items = new SparseBooleanArray();
    }
    public StockCountListAdapter(List<StockCountList> list, Context context) {
        this.list = list;
        this.context = context;
    }


    public void removeData(int position) {
        list.remove(position);
        resetCurrentIndex();
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


//    private void displayImage(ViewHolder holder,StockCountList stockCountList) {
//        if (stockCountList.getVNo().isEmpty()) {
//            holder.img_check.setImageDrawable(null);
//        } else {
//            holder.img_check.setImageResource(R.drawable.ic_check_red);
//        }
//    }

    private void toggleCheckedIcon(ViewHolder holder, int position) {
        if (selected_items.get(position, false)) {
            holder.parent.setBackgroundColor(Color.parseColor("#fca1a3"));
        } else {
            holder.parent.setBackgroundColor(Color.WHITE);
        }
        if (current_selected_idx == position) resetCurrentIndex();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.stock_count_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final StockCountList stockCountList = list.get(position);
//        holder.card.setActivated(selected_items.get(position,false));
    holder.Vno.setText("VNo :"+list.get(position).VNo);
    holder.date.setText("Date :"+ list.get(position).Date);
    holder.totalQty.setVisibility(View.GONE);
    holder.warehouse.setText("Warehouse :"+list.get(position).Warehouse);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onDeleteItemClick(stockCountList, position);

            }
        });

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onItemClick(stockCountList,position);
            }
        });

        holder.parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(onClickListener==null) return false;
                else {
                    onClickListener.onItemLongClick(position);
                }
                return true;
            }
        });
        toggleCheckedIcon(holder, position);
//        displayImage(holder, stockCountList);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onDeleteItemClick(StockCountList stockCountList, int pos);

        void onItemClick(StockCountList stockCountList, int pos);
        void onItemLongClick(int pos);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView Vno;
        final TextView date;
        final TextView totalQty;
        final TextView warehouse;
        final ImageButton delete;
        final CardView card;
        LinearLayout parent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Vno = itemView.findViewById(R.id.vno);
            date = itemView.findViewById(R.id.date);
            totalQty = itemView.findViewById(R.id.total_qty);
            warehouse = itemView.findViewById(R.id.warehouse);
            delete = itemView.findViewById(R.id.delete);
            card = itemView.findViewById(R.id.card);
            parent = itemView.findViewById(R.id.parent);
        }
    }
}
