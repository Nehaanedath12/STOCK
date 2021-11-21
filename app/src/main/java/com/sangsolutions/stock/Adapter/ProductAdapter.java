package com.sangsolutions.stock.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.sangsolutions.stock.Database.Product;
import com.sangsolutions.stock.R;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {

    Context context;

    List<Product> items, tempItems, suggestions;
    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public interface OnClickListener {
        void onItemClick(Product product, int position);

    }

    public ProductAdapter(Context context, List<Product> items) {
        super(context, 0, items);
        this.context = context;

        this.items = items;
        tempItems = new ArrayList<Product>(items);
        suggestions = new ArrayList<Product>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.adapter_product, parent, false);
        }
        Product product = items.get(position);
        TextView lblName = view.findViewById(R.id.product);
        LinearLayout LinearProduct =view.findViewById(R.id.productLinear);
        if (product != null) {
            if (lblName != null)
                lblName.setText(product.getName());
        }

        LinearProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("iidd", product.getMasterId() + "");
                onClickListener.onItemClick(product, position);
            }
        });

        return view;
    }



    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((Product) resultValue).getName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Product product : tempItems) {
                    if (product.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(product);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Product> filterList = (ArrayList<Product>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (Product people : filterList) {
                    add(people);
                    notifyDataSetChanged();
                }
            }
        }
    };
}

