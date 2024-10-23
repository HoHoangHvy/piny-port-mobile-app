package com.example.pinyport.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.pinyport.databinding.FragmentHomeBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private BarChart barChart;
    private PieChart pieChart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Binding layout
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Khởi tạo các biểu đồ
        barChart = binding.barChart;
        pieChart = binding.pieChart;

        // Lắng nghe thay đổi dữ liệu BarChart từ ViewModel
        homeViewModel.getBarChartData().observe(getViewLifecycleOwner(), new Observer<ArrayList<BarEntry>>() {
            @Override
            public void onChanged(ArrayList<BarEntry> barEntries) {
                BarDataSet barDataSet = new BarDataSet(barEntries, "Revenue");
                // Lấy màu sắc từ ViewModel
                homeViewModel.getBarChartColors().observe(getViewLifecycleOwner(), new Observer<int[]>() {
                    @Override
                    public void onChanged(int[] barColors) {
                        barDataSet.setColors(barColors); // Sử dụng màu sắc từ ViewModel
                    }
                });
                BarData barData = new BarData(barDataSet);
                barChart.setData(barData);
                barChart.invalidate(); // Refresh chart
                barChart.animateY(1000); // Animation
            }
        });

        // Lắng nghe thay đổi dữ liệu PieChart từ ViewModel
        homeViewModel.getPieChartData().observe(getViewLifecycleOwner(), new Observer<ArrayList<PieEntry>>() {
            @Override
            public void onChanged(ArrayList<PieEntry> pieEntries) {
                PieDataSet pieDataSet = new PieDataSet(pieEntries, "Result");
                // Lấy màu sắc từ ViewModel
                homeViewModel.getPieChartColors().observe(getViewLifecycleOwner(), new Observer<int[]>() {
                    @Override
                    public void onChanged(int[] pieColors) {
                        pieDataSet.setColors(pieColors); // Sử dụng màu sắc từ ViewModel
                    }
                });
                PieData pieData = new PieData(pieDataSet);
                pieChart.setData(pieData);
                pieChart.invalidate(); // Refresh chart
                pieChart.animateY(1000); // Animation
            }
        });

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
