package com.example.pinyport.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinyport.R;
import com.example.pinyport.databinding.FragmentChatBinding;

import java.util.ArrayList;
import java.util.List;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot(); // Trả về view gốc của fragment

        recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ItemDecoration(8)); // khoảng cách giữa các item

        // Set up the newchatButton click listener
        binding.newchatButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.navigation_newchat); // Navigate to NewChatFragment
        });

        // Create a list of chats
        // Create chat list
        chatList = new ArrayList<>();
        for (int i = 0; i <= 15; i++) {
            chatList.add(new Chat("Customer " + i, "Latest chat"));
        }

        // Set up adapter
        chatAdapter = new ChatAdapter(chatList);
        recyclerView.setAdapter(chatAdapter);

        // Notify adapter after setting the data
        chatAdapter.notifyDataSetChanged();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}