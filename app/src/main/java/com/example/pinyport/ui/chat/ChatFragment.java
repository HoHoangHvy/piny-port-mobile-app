package com.example.pinyport.ui.chat;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

        binding.newchatButton.setOnClickListener(v -> {
            ImageButton newChatButton = (ImageButton) v;

            if (isCloseButton) {
                binding.title.setText("Chat");
                ViewGroup parent = (ViewGroup) binding.searchUserChat.getParent();

                if (parent != null) {
                    View newEditText = parent.findViewById(R.id.new_edittext);
                    if (newEditText != null) {
                        parent.removeView(newEditText);
                    }
                    parent.addView(binding.searchUserChat, 1);
                }

                newChatButton.setImageResource(R.drawable.newchat_icon);
                isCloseButton = false;
            } else {
                binding.title.setText("New Chat");
                ViewGroup parent = (ViewGroup) binding.searchUserChat.getParent();

                if (parent != null) {
                    parent.removeView(binding.searchUserChat);

                    View existingEditText = parent.findViewById(R.id.new_edittext);
                    if (existingEditText == null) {
                        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                        EditText newEditText = (EditText) layoutInflater.inflate(R.layout.new_edittext, parent, false);
                        newEditText.setId(R.id.new_edittext); // Đảm bảo rằng ID được gán để có thể tìm và xóa nó sau này

                        parent.addView(newEditText, 1);
                    }
                }

                newChatButton.setImageResource(R.drawable.ic_close);
                isCloseButton = true;
            }
        });

        chatList = new ArrayList<>();
        for (int i = 0; i <= 15; i++) {
            chatList.add(new Chat("Customer " + i, "Latest chat"));
        }

        chatAdapter = new ChatAdapter(chatList);
        recyclerView.setAdapter(chatAdapter);
        chatAdapter.setOnItemClickListener(position -> {
            NavController navController = Navigation.findNavController(requireView());
            Bundle bundle = new Bundle();
            bundle.putString("customerName", chatList.get(position).getName());
            navController.navigate(R.id.navigation_newchat, bundle);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}