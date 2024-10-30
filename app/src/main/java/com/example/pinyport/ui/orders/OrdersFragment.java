package com.example.pinyport.ui.orders;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Pair;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinyport.R;
import com.example.pinyport.adapter.OrderListAdapter;
import com.example.pinyport.databinding.FilterOrderDrawerBinding;
import com.example.pinyport.databinding.FragmentOrdersBinding;
import com.example.pinyport.model.Customer;
import com.example.pinyport.model.Order;
import com.example.pinyport.ui.dialog.CustomerDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.slider.RangeSlider;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class OrdersFragment extends Fragment {

    private FragmentOrdersBinding binding;
    private List<Order> orderList = new ArrayList<>();  // Sample data
    private ArrayList<String> filterStatus = new ArrayList<>(
            List.of("Wait", "Preparing", "Delivering", "Success", "Cancelled")
    );
    private int defaultStep = 1000;
    private OrderListAdapter adapter = new OrderListAdapter(orderList, this::onOrderClick);


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        OrdersViewModel ordersViewModel = new ViewModelProvider(this).get(OrdersViewModel.class);

        // Inflate the layout using View Binding
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize RecyclerView
        setupRecyclerView();

        // Initialize Filter UI Components
        initFilterUI(root);

        // Set the default date range for the date picker
        setDefaultDateRange();

        return root;
    }

    private void setupRecyclerView() {
        orderList = getOrderList(); // Fetch the list of orders
        adapter.updateOrderList(orderList); // Update the adapter's data
        RecyclerView recyclerView = binding.orderList;
        recyclerView.setAdapter(adapter); // Set the adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    public void onOrderClick(Order order) {
        // Pass the selected order details to the fragment
        Bundle args = new Bundle();
        args.putSerializable("order", order); // Assuming Order implements Serializable

        // Use NavController to navigate to OrderDetailFragment with arguments
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.action_ordersFragment_to_orderDetailFragment, args);
    }


    private List<Order> getOrderList() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order("MM0876", "12/06/2024", "11:30", "Le Thi Loan", "Wait", 2, "170000 VND"));
        orders.add(new Order("MM0877", "17/06/2024", "15:45", "Nguyen Van A", "Preparing", 3, "30000 VND"));
        orders.add(new Order("MM088", "14/06/2024", "15:45", "Nguyen Van B", "Delivering", 3, "1200000 VND"));
        orders.add(new Order("MM0879", "16/06/2024", "15:45", "Nguyen Van C", "Success", 3, "16000 VND"));
        orders.add(new Order("MM0880", "16/06/2024", "15:45", "Nguyen Van D", "Cancelled", 3, "200000 VND"));
        return orders;
    }
    private void initFilterUI(View root) {
        FilterOrderDrawerBinding filterOrderDrawerBinding = FilterOrderDrawerBinding.bind(binding.filterOrderDrawer.getRoot());
        DrawerLayout drawerLayout = binding.drawerLayout;

        // Filter Button Handlers
        Button filterButton = binding.filterButton;
        Button sortButton = binding.sortButton;
        Button applyFilterButton = filterOrderDrawerBinding.applyFilterButton;
        Button cancelFilterButton = filterOrderDrawerBinding.cancelFilterButton;
        Button clearAllButton = filterOrderDrawerBinding.clearAllButton; // Add this line

        filterButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));
        cancelFilterButton.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.END));
        applyFilterButton.setOnClickListener(v -> applyFilters(root, drawerLayout));
        clearAllButton.setOnClickListener(v -> clearAllFilters(filterOrderDrawerBinding)); // Add this line
        sortButton.setOnClickListener(v -> showSortDialog());

        // Price Range Slider
        setupPriceRangeSlider(filterOrderDrawerBinding);

        // Step Spinner
        setupStepSpinner(filterOrderDrawerBinding.stepSpinner);

        // Date Picker
        setupDatePicker(filterOrderDrawerBinding);

        // Initialize Status Filters
        initStatusFilter(filterOrderDrawerBinding);

        // Initialize Customer Filter
        initCustomerFilter(filterOrderDrawerBinding);
    }

    private void initCustomerFilter(FilterOrderDrawerBinding filterOrderDrawerBinding) {
        TextView selectedCustomerTextView = filterOrderDrawerBinding.selectedCustomerTextView;
        Button customerFilterButton = filterOrderDrawerBinding.customerPicker;

        customerFilterButton.setOnClickListener(v -> {
            List<Customer> customers = getCustomerList(); // Fetch the list of customers
            CustomerDialog.showDialog(getActivity(), customers, selectedCustomerTextView);
        });
    }

    private List<Customer> getCustomerList() {
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("C001", "John Doe", "john.doe@example.com", "Gold"));
        customers.add(new Customer("C002", "Jane Smith", "jane.smith@example.com", "Silver"));
        customers.add(new Customer("C003", "Alice Johnson", "alice.johnson@example.com", "Bronze"));
        // Add more customers as needed
        return customers;
    }

    private void setupPriceRangeSlider(FilterOrderDrawerBinding filterOrderDrawerBinding) {
        RangeSlider rangeSlider = filterOrderDrawerBinding.priceRangeSlider;
        TextView textView = filterOrderDrawerBinding.priceRangeText;

        rangeSlider.setLabelFormatter(value -> formatCurrency(value));
        List<Float> initialValues = rangeSlider.getValues();
        updateTextPrice(initialValues.get(0), initialValues.get(1), textView);

        rangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            updateTextPrice(values.get(0), values.get(1), textView);
        });
    }

    private void setupStepSpinner(Spinner stepSpinner) {
        stepSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                defaultStep = parseCurrencyString(parent.getItemAtPosition(position).toString());
                updateTextPrice(binding.filterOrderDrawer.priceRangeSlider.getValues().get(0),
                        binding.filterOrderDrawer.priceRangeSlider.getValues().get(1),
                        binding.filterOrderDrawer.priceRangeText);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: handle when no item is selected
            }
        });
    }

    private void setupDatePicker(FilterOrderDrawerBinding filterOrderDrawerBinding) {
        Button datePickerButton = filterOrderDrawerBinding.datePickerButton;
        TextView dateTextView = filterOrderDrawerBinding.dateTextView;

        // Get the first and last day of the current month
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        long startDate = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        long endDate = calendar.getTimeInMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        AtomicReference<String> startDateStr = new AtomicReference<>(sdf.format(new Date(startDate)));
        AtomicReference<String> endDateStr = new AtomicReference<>(sdf.format(new Date(endDate)));

        dateTextView.setText("From: " + startDateStr + "\nTo: " + endDateStr);

        datePickerButton.setOnClickListener(v -> {
            MaterialDatePicker<Pair<Long, Long>> dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select a date range")
                    .setSelection(new Pair<>(startDate, endDate))
                    .setTheme(R.style.CustomDatePickerTheme)
                    .build();

            dateRangePicker.show(getChildFragmentManager(), "DATE_RANGE_PICKER");
            dateRangePicker.addOnPositiveButtonClickListener(selection -> {
                startDateStr.set(sdf.format(new Date(selection.first)));
                endDateStr.set(sdf.format(new Date(selection.second)));
                dateTextView.setText("From: " + startDateStr + "\nTo: " + endDateStr);
            });
        });
    }

    private void setDefaultDateRange() {
        // This can be handled in setupDatePicker if preferred
        // Placeholder for additional setup if needed
    }

    private void updateTextPrice(float from, float to, TextView textView) {
        textView.setText("From: " + formatCurrency(from) + " To: " + formatCurrency(to));
    }

    private int parseCurrencyString(String currencyString) {
        currencyString = currencyString.replace(" đ", "").replace(".", "");
        if (currencyString.endsWith("0") && currencyString.length() > 1) {
            currencyString = currencyString.substring(0, currencyString.length() - 1);
        }
        try {
            return Integer.parseInt(currencyString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String formatCurrency(float value) {
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(value * defaultStep);
    }

    private void initStatusFilter(FilterOrderDrawerBinding filterOrderDrawerBinding) {
        setupCardClickListener(filterOrderDrawerBinding.waitStatusFilter, filterOrderDrawerBinding.waitStatusText);
        setupCardClickListener(filterOrderDrawerBinding.cancelledStatusFilter, filterOrderDrawerBinding.cancelledStatusText);
        setupCardClickListener(filterOrderDrawerBinding.successStatusFilter, filterOrderDrawerBinding.successStatusText);
        setupCardClickListener(filterOrderDrawerBinding.deliveringStatusFilter, filterOrderDrawerBinding.deliveringStatusText);
        setupCardClickListener(filterOrderDrawerBinding.preparingStatusFilter, filterOrderDrawerBinding.preparingStatusText);
    }

    private void setupCardClickListener(final MaterialCardView card, final TextView textView) {
        card.setOnClickListener(view -> {
            card.setChecked(!card.isChecked());
            if (card.isChecked()) {
                card.setCardBackgroundColor(getResources().getColor(R.color.blue_500));
                textView.setTextColor(Color.WHITE);
            } else {
                card.setCardBackgroundColor(getResources().getColor(R.color.filter_item_background));
                textView.setTextColor(Color.parseColor("#006FFD"));
            }
        });
    }

    private void applyFilters(View root, DrawerLayout drawerLayout) {
        // Close the drawer after applying filters
        drawerLayout.closeDrawer(GravityCompat.END);
    }

    private void clearAllFilters(FilterOrderDrawerBinding filterOrderDrawerBinding) {
        // Reset Price Range Slider
        RangeSlider rangeSlider = filterOrderDrawerBinding.priceRangeSlider;
        rangeSlider.setValues(20f, 70f);
        updateTextPrice(20f, 70f, filterOrderDrawerBinding.priceRangeText);

        // Reset Step Spinner
        Spinner stepSpinner = filterOrderDrawerBinding.stepSpinner;
        stepSpinner.setSelection(0);

        // Reset Date Picker
        TextView dateTextView = filterOrderDrawerBinding.dateTextView;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        long startDate = calendar.getTimeInMillis();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        long endDate = calendar.getTimeInMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateTextView.setText("From: " + sdf.format(new Date(startDate)) + "\nTo: " + sdf.format(new Date(endDate)));

        // Reset Status Filters
        resetCard(filterOrderDrawerBinding.waitStatusFilter, filterOrderDrawerBinding.waitStatusText);
        resetCard(filterOrderDrawerBinding.cancelledStatusFilter, filterOrderDrawerBinding.cancelledStatusText);
        resetCard(filterOrderDrawerBinding.successStatusFilter, filterOrderDrawerBinding.successStatusText);
        resetCard(filterOrderDrawerBinding.deliveringStatusFilter, filterOrderDrawerBinding.deliveringStatusText);
        resetCard(filterOrderDrawerBinding.preparingStatusFilter, filterOrderDrawerBinding.preparingStatusText);

        // Reset Customer Filter
        TextView selectedCustomerTextView = filterOrderDrawerBinding.selectedCustomerTextView;
        selectedCustomerTextView.setText("Select Customer");
    }

    private void showSortDialog() {
        // Define the sorting options
        String[] sortOptions = {"Sort by Date", "Sort by Price", "Sort by Name"};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity()); // Use requireActivity() for context
        builder.setTitle("Select Sort Option")
                .setItems(sortOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the sorting logic based on the selected option
                        sortOrders(which); // Call the sorting method
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void sortOrders(int which) {
        switch (which) {
            case 0:
                orderList.sort(Comparator.comparing(Order::getDate));
                break;
            case 1:
                orderList.sort(Comparator.comparing(order -> parseCurrencyString(order.getValue())));
                break;
            case 2:
                orderList.sort(Comparator.comparing(Order::getCustomerName));
                break;
        }
        adapter.notifyDataSetChanged();
    }


    private void resetCard(MaterialCardView card, TextView textView) {
        card.setChecked(false);
        card.setCardBackgroundColor(getResources().getColor(R.color.filter_item_background));
        textView.setTextColor(Color.parseColor("#006FFD"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}