package com.example.pinyport.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinyport.adapter.MessageAdapter;
import com.example.pinyport.databinding.FragmentChatDetailBinding;
import com.example.pinyport.model.Chat;
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

        // Retrieve chat data from arguments
        Bundle args = getArguments();
        if (args != null) {
            Chat chat = (Chat) args.getSerializable("chat");
            if (chat != null) {
                binding.customerName.setText(chat.getName());
            }
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews();
        setupListeners();

        Bundle args = getArguments();
        if (args != null) {
            if (!args.getBoolean("isNewChat")) {
                loadInitialComments();
            } else {
                Comment comment = (Comment) args.getSerializable("comment");
                if (comment != null) {
                    addMessage(comment.getComment(), comment.getDirection());
                }
            }
        }
    }

    private void initializeViews() {
        chatRecyclerView = binding.chatRecyclerView;
        messageInput = binding.messageInput;
        sendButton = binding.sendButton;
        backButton = binding.backButton;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);  // Display latest messages at the bottom
        chatRecyclerView.setLayoutManager(layoutManager);

        messageAdapter = new MessageAdapter(commentList);
        chatRecyclerView.setAdapter(messageAdapter);
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                addMessage(message, "outgoing");
                messageInput.setText("");  // Clear the input field
            }
        });
    }

    private void addMessage(String message, String direction) {
        Comment newComment = new Comment(String.valueOf(commentList.size()), "User", message, direction);
        commentList.add(newComment);
        messageAdapter.notifyItemInserted(commentList.size() - 1);
        chatRecyclerView.scrollToPosition(commentList.size() - 1);  // Scroll to the latest message
    }

    private void loadInitialComments() {
        // Check if arguments contain a Chat object
        Bundle args = getArguments();
        if (args != null) {
            Chat initialChat = (Chat) args.getSerializable("chat");
            if (initialChat != null) {
                commentList.add(new Comment("0", initialChat.getName(), initialChat.getLatestMessage(), "incoming"));
            }
        }

        // Add sample messages (remove if loading from a real data source)
        commentList.add(new Comment("1", "John", "Hello!", "incoming"));
        commentList.add(new Comment("2", "Jane", "Hi", "outgoing"));
        commentList.add(new Comment("3", "John", "How are you?", "incoming"));

        messageAdapter.notifyDataSetChanged();  // Refresh adapter after loading data
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
