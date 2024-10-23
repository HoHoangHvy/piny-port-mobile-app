package com.example.pinyport.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pinyport.R;
import com.example.pinyport.databinding.FragmentNewchatBinding;
import java.util.ArrayList;
import java.util.List;

public class NewChatFragment extends Fragment {

    private FragmentNewchatBinding binding;  // Correct binding for fragment_newchat
    private RecyclerView recyclerView;
    private NewChatAdapter newchatAdapter;
    private List<Chat> chatList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout cho fragment_newchat
        binding = FragmentNewchatBinding.inflate(inflater, container, false); // Use correct binding for fragment_newchat
        View view = binding.getRoot(); // Get root view from the correct binding

        // Setup RecyclerView
        recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ItemDecoration(8)); // Custom item decoration

        // Tạo danh sách chat
        chatList = new ArrayList<>();
        for (int i = 0; i <= 15; i++) {
            chatList.add(new Chat("Customer " + i, "Latest chat"));
        }

        // Set adapter cho RecyclerView
        newchatAdapter = new NewChatAdapter(chatList);
        recyclerView.setAdapter(newchatAdapter);

        // Thông báo adapter cập nhật dữ liệu
        newchatAdapter.notifyDataSetChanged();

        // Inflate the new custom action bar for this fragment
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            // Tắt custom action bar hiện tại
            activity.getSupportActionBar().setDisplayShowCustomEnabled(false);

            // Inflate và thêm custom action bar mới
            View newCustomActionBarView = inflater.inflate(R.layout.custom_toolbar, null); // Thay thế bằng layout của custom action bar mới
            activity.getSupportActionBar().setCustomView(newCustomActionBarView);
            activity.getSupportActionBar().setDisplayShowCustomEnabled(true);

//            ImageButton backButton = newCustomActionBarView.findViewById(R.id.homeButton);
//            backButton.setOnClickListener(v -> {
//                // Sử dụng NavController để quay lại ChatFragment
//                NavController navController = Navigation.findNavController(view);
//                navController.navigateUp(); // Quay lại
//            });
        }


        return view;  // Return the correct view for the fragment
    }
}
