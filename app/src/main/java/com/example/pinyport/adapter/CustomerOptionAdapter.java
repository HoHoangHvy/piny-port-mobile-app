package com.example.pinyport.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.pinyport.model.CustomerOption;

import java.util.ArrayList;
import java.util.List;

public class CustomerOptionAdapter extends ArrayAdapter<CustomerOption> implements Filterable {
    private List<CustomerOption> originalCustomers;
    private List<CustomerOption> filteredCustomers;

    public CustomerOptionAdapter(Context context, List<CustomerOption> customers) {
        super(context, android.R.layout.simple_dropdown_item_1line, customers);
        this.originalCustomers = new ArrayList<>(customers);
        this.filteredCustomers = new ArrayList<>(customers);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    filteredCustomers = new ArrayList<>(originalCustomers);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    filteredCustomers = new ArrayList<>();
                    for (CustomerOption customer : originalCustomers) {
                        if (customer.getName().toLowerCase().contains(filterPattern) ||
                                customer.getPhone().toLowerCase().contains(filterPattern)) {
                            filteredCustomers.add(customer);
                        }
                    }
                }
                results.values = filteredCustomers;
                results.count = filteredCustomers.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();
                addAll((List<CustomerOption>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        CustomerOption customer = filteredCustomers.get(position);
        textView.setText(customer.getName() + " - " + customer.getPhone());
        return convertView;
    }
}
