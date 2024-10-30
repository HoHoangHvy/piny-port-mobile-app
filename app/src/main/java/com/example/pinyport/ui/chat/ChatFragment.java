package com.example.pinyport.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinyport.R;
import com.example.pinyport.adapter.ChatAdapter;
import com.example.pinyport.databinding.FragmentChatBinding;
import com.example.pinyport.model.Chat;
import com.example.pinyport.model.Customer;
import com.example.pinyport.ui.dialog.CustomerDialog;
import com.example.pinyport.ui.orders.OrderDetailFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList;
    private boolean isCloseButton = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        View customView = ((AppCompatActivity) getActivity()).getSupportActionBar().getCustomView();
        TextView customTitle = customView.findViewById(R.id.customTitle);

        recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ItemDecoration(8)); // khoảng cách giữa các item

        binding.newChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleNewChatMode(true, binding);
            }
        });
        binding.exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleNewChatMode(false, binding);
            }
        });

        chatList = new ArrayList<>();
        chatList.add(new Chat("Nguyen Van A", "Hi"));
        chatList.add(new Chat("Nguyen Van B", "How are you"));
        chatList.add(new Chat("Nguyen Van C", "Latest chat"));
        chatList.add(new Chat("Nguyen Van D", "Latest chat"));
        chatList.add(new Chat("Le Thi F", "Latest chat"));

        chatAdapter = new ChatAdapter(chatList);
        recyclerView.setAdapter(chatAdapter);
        chatAdapter.setOnItemClickListener(position -> {
            Chat selectedChat = chatList.get(position);
            Bundle args = new Bundle();
            args.putSerializable("chat", selectedChat);
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_chat_detail, args);
        });
        initCustomerFilter(binding);
        return root;
    }
    private void initCustomerFilter(FragmentChatBinding binding) {
        TextView selectedCustomerTextView = binding.selectedCustomerTextView;
        Button customerFilterButton = binding.customerPicker;

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
    private void toggleNewChatMode(boolean isNewChat, FragmentChatBinding binding) {
        binding.newChatButton.setVisibility(isNewChat ? View.GONE : View.VISIBLE);
        binding.exitButton.setVisibility(isNewChat ? View.VISIBLE : View.GONE);
        binding.newChatLayout.setVisibility(isNewChat ? View.VISIBLE : View.GONE);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}