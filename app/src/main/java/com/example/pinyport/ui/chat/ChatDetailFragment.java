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

import com.example.pinyport.adapter.MessageAdapter;
import com.example.pinyport.databinding.FragmentChatDetailBinding;
import com.example.pinyport.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class ChatDetailFragment extends Fragment {
    private FragmentChatDetailBinding binding;
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ImageButton backButton;
    private MessageAdapter messageAdapter;
    private List<Comment> commentList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews();
        loadCommentList();
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

        messageAdapter = new MessageAdapter(commentList);
        chatRecyclerView.setAdapter(messageAdapter);
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                addMessage(message, "outgoing");
                messageInput.setText("");
            }
        });
    }

    private void addMessage(String message, String direction) {
        Comment comment = new Comment(String.valueOf(commentList.size()), "User", message, direction);
        commentList.add(comment);
        messageAdapter.notifyItemInserted(commentList.size() - 1);
        chatRecyclerView.scrollToPosition(commentList.size() - 1);
    }

    private void loadCommentList() {
        commentList.add(new Comment("1", "John", "Hello!", "incoming"));
        commentList.add(new Comment("2", "Jane", "Hi", "outgoing"));
        commentList.add(new Comment("3", "John", "How are you?", "incoming"));
        messageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
