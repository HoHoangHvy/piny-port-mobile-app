package com.example.pinyport.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.pinyport.model.VoucherOption;

import java.util.ArrayList;
import java.util.List;

public class VoucherOptionAdapter extends ArrayAdapter<VoucherOption> implements Filterable {
    private List<VoucherOption> originalVouchers;
    private List<VoucherOption> filteredVouchers;

    public VoucherOptionAdapter(Context context, List<VoucherOption> vouchers) {
        super(context, android.R.layout.simple_dropdown_item_1line, vouchers);
        this.originalVouchers = new ArrayList<>(vouchers);
        this.filteredVouchers = new ArrayList<>(vouchers);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    filteredVouchers = new ArrayList<>(originalVouchers);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    filteredVouchers = new ArrayList<>();
                    for (VoucherOption voucher : originalVouchers) {
                        if (voucher.getName().toLowerCase().contains(filterPattern)) {
                            filteredVouchers.add(voucher);
                        }
                    }
                }
                results.values = filteredVouchers;
                results.count = filteredVouchers.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null) {
                    clear();
                    addAll((List<VoucherOption>) results.values);
                }
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (filteredVouchers == null || position >= filteredVouchers.size()) {
            return new View(getContext()); // Return a dummy view
        }

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        VoucherOption voucher = filteredVouchers.get(position);
        textView.setText(voucher.getName());
        return convertView;
    }
}