package com.example.pinyport.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinyport.R;
import com.example.pinyport.model.Topping;

import java.util.ArrayList;
import java.util.List;

public class ToppingsAdapter extends RecyclerView.Adapter<ToppingsAdapter.ToppingViewHolder> {
    private List<Topping> toppings;
    private List<Topping> selectedToppings;
    private OnToppingSelectedListener listener;

    public ToppingsAdapter(List<Topping> toppings) {
        this.toppings = toppings;
        this.selectedToppings = new ArrayList<>();
    }

    public void setOnToppingSelectedListener(OnToppingSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ToppingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topping_items, parent, false);
        return new ToppingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToppingViewHolder holder, int position) {
        Topping topping = toppings.get(position);
        holder.bind(topping);
    }

    @Override
    public int getItemCount() {
        return toppings.size();
    }

    public List<Topping> getSelectedToppings() {
        return selectedToppings;
    }

    public class ToppingViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private Topping topping;

        public ToppingViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_topping);

            // Handle checkbox state changes
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedToppings.add(topping);
                } else {
                    selectedToppings.remove(topping);
                }

                // Notify the listener when a topping is selected or deselected
                if (listener != null) {
                    listener.onToppingSelected(selectedToppings);
                }
            });
        }

        public void bind(Topping topping) {
            this.topping = topping;
            checkBox.setText(topping.getName() + " + " + topping.getPrice());
            checkBox.setChecked(selectedToppings.contains(topping)); // Maintain state on rebind
        }
    }

    // Interface to notify when toppings are selected or deselected
    public interface OnToppingSelectedListener {
        void onToppingSelected(List<Topping> selectedToppings);
    }
}