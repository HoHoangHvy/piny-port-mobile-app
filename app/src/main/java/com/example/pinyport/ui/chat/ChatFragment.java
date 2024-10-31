package com.example.pinyport.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinyport.R;
import com.example.pinyport.adapter.ChatAdapter;
import com.example.pinyport.databinding.FragmentChatBinding;
import com.example.pinyport.model.Chat;
import com.example.pinyport.model.Comment;
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
        binding.sendButton.setOnClickListener(v -> {
            String message = binding.messageInput.getText().toString();
            String customerName = binding.selectedCustomerTextView.getText().toString().replace("Customer: ", "");

            if (!message.isEmpty() && (!customerName.isEmpty() || !customerName.equals("None"))) {
                // Create a new Chat object
                Chat newChat = new Chat(customerName, message);

                // Create a bundle to pass the Chat object to ChatDetailFragment
                Bundle args = new Bundle();
                args.putSerializable("chat", newChat);
                args.putSerializable("comment", new Comment("123", "example", message, "outgoing"));
                args.putBoolean("isNewChat", true);
                // Hide Action Bar when fragment is visible
                AppCompatActivity activity = (AppCompatActivity) requireActivity();
                if (activity.getSupportActionBar() != null) {
                    activity.getSupportActionBar().hide(); // Hide the action bar
                }
                // Navigate to ChatDetailFragment with arguments
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.navigation_chat_detail, args);
            } else {
                Toast.makeText(getContext(), "Please select a customer and enter a message.", Toast.LENGTH_SHORT).show();
            }
        });

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