package com.example.pinyport.ui.customers;

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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinyport.R;
import com.example.pinyport.adapter.CustomerListAdapter;
import com.example.pinyport.databinding.FilterCustomerDrawerBinding;
import com.example.pinyport.databinding.FragmentCustomersBinding;
import com.example.pinyport.model.Customer;
import com.example.pinyport.model.Order;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CustomersFragment extends Fragment {

    private FragmentCustomersBinding binding;
    private List<Customer> originalCustomerList;  // Store the original customer list
    private List<Customer> filteredCustomerList;  // Store the filtered customer list
    private CustomerListAdapter adapter;
    private List<Customer> customerList = new ArrayList<>();  // Sample data

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CustomersViewModel customersViewModel = new ViewModelProvider(this).get(CustomersViewModel.class);
        binding = FragmentCustomersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Customer List
        originalCustomerList = getCustomerList(); // Fetch the original list of customers
        filteredCustomerList = new ArrayList<>(originalCustomerList); // Start with the original list
        adapter = new CustomerListAdapter(filteredCustomerList, this::onCustomerClick);

        // Initialize RecyclerView
        setupRecyclerView();

        // Initialize Filter UI Components
        initFilterUI(root);

        Button createCustomerButton = binding.createCustomerButton;

        createCustomerButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_create_customer);
        });

        return root;
    }
    private void setupRecyclerView() {
        customerList = getCustomerList(); // Fetch the list of customers
        adapter.updateCustomerList(customerList); // Update the adapter's data
        RecyclerView recyclerView = binding.customerList;
        recyclerView.setAdapter(adapter); // Set the adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void onCustomerClick(Customer customer) {
        // Handle customer click, e.g., navigate to customer detail
        Bundle args = new Bundle();
        args.putSerializable("customer", customer); // Assuming Customer implements Serializable
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.action_customersFragment_to_customerDetailFragment, args);
    }

    private List<Customer> getCustomerList() {
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("C001", "Nguyễn Văn A", "nguyenvana@example.com", "Gold", "01/01/2023"));
        customers.add(new Customer("C002", "Trần Thị B", "tranthib@example.com", "Silver", "15/02/2023"));
        customers.add(new Customer("C003", "Lê Văn C", "levanc@example.com", "Bronze", "20/03/2023"));
        customers.add(new Customer("C004", "Phạm Thị D", "phamthid@example.com", "Gold", "30/04/2023"));
        customers.add(new Customer("C005", "Đỗ Văn E", "dovanE@example.com", "Silver", "05/05/2023"));
        customers.add(new Customer("C006", "Hoàng Thị F", "hoangthif@example.com", "Bronze", "12/06/2023"));
        customers.add(new Customer("C007", "Vũ Văn G", "vuvanG@example.com", "Gold", "25/07/2023"));
        customers.add(new Customer("C008", "Ngô Thị H", "ngothih@example.com", "Silver", "10/08/2023"));
        customers.add(new Customer("C009", "Nguyễn Văn I", "nguyenvani@example.com", "Bronze", "15/09/2023"));
        customers.add(new Customer("C010", "Lê Thị J", "lethij@example.com", "Gold", "20/10/2023"));
        // Add more customers as needed
        return customers;
    }


    private void initFilterUI(View root) {
        FilterCustomerDrawerBinding filterCustomerDrawerBinding = FilterCustomerDrawerBinding.bind(binding.filterCustomerDrawer.getRoot());
        DrawerLayout drawerLayout = binding.drawerLayout;

        Button filterButton = binding.filterButton;
        Button applyFilterButton = filterCustomerDrawerBinding.applyFilterButton;
        Button cancelFilterButton = filterCustomerDrawerBinding.cancelFilterButton;
        Button clearAllButton = filterCustomerDrawerBinding.clearAllButton; // New clear filters button

        filterButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));
        cancelFilterButton.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.END));
        applyFilterButton.setOnClickListener(v -> applyFilters(drawerLayout));
        clearAllButton.setOnClickListener(v -> clearAllFilters(filterCustomerDrawerBinding)); // New clear filters logic

        // Initialize any other UI components for filtering, sorting, etc. as necessary
    }

    private void applyFilters(DrawerLayout drawerLayout) {
        // Logic to filter customers based on selected criteria (e.g., status)
        // Close the drawer after applying filters
        drawerLayout.closeDrawer(GravityCompat.END);
    }

    private void clearAllFilters(FilterCustomerDrawerBinding filterCustomerDrawerBinding) {
        // Logic to reset all filters to their default states
    }

    private void showSortDialog() {
        String[] sortOptions = {"Sort by Name", "Sort by Email", "Sort by Status"};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Select Sort Option")
                .setItems(sortOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sortCustomers(which);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void sortCustomers(int which) {
        switch (which) {
            case 0:
                filteredCustomerList.sort(Comparator.comparing(Customer::getName));
                break;
            case 1:
                filteredCustomerList.sort(Comparator.comparing(Customer::getPhone));
                break;
            case 2:
                filteredCustomerList.sort(Comparator.comparing(Customer::getRank));
                break;
        }
        adapter.notifyDataSetChanged(); // Notify adapter of data changes
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
