package com.example.pinyport.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinyport.R;
import com.example.pinyport.databinding.FragmentNewchatBinding;

import java.util.ArrayList;
import java.util.List;

public class NewChatFragment extends Fragment {

    private FragmentNewchatBinding binding;  // Correct binding for fragment_newchat
    private RecyclerView recyclerView;
    private NewChatAdapter newChatAdapter;
    private List<Chat> chatList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNewchatBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new ItemDecoration(8)); // Custom item decoration

        chatList = new ArrayList<>();
        for (int i = 0; i <= 15; i++) {
            chatList.add(new Chat("Customer " + i, "Latest chat"));
        }

        newChatAdapter = new NewChatAdapter(chatList);
        recyclerView.setAdapter(newChatAdapter);

        newChatAdapter.notifyDataSetChanged();

        return rootView;
    }
}
