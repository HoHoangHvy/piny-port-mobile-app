package com.example.pinyport.ui.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinyport.databinding.FragmentChatviewBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatView extends Fragment {
    private FragmentChatviewBinding binding;
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ImageButton backButton;
    private MessageAdapter messageAdapter;
    private List<String> messageList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews();
        setupListeners();
    }


    private void initializeViews() {
        chatRecyclerView = binding.chatRecyclerView;
        messageInput = binding.messageInput;
        sendButton = binding.sendButton;
        backButton = binding.backButton;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);

        messageAdapter = new MessageAdapter(messageList);
        chatRecyclerView.setAdapter(messageAdapter);
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            Log.d("ChatView", "Message: " + message);
            if (!message.isEmpty()) {
                addMessage(message);
                messageInput.setText("");
            }
        });

    }

    private void addMessage(String message) {
        messageList.add(message);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        chatRecyclerView.scrollToPosition(messageList.size() - 1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; 
    }
}
