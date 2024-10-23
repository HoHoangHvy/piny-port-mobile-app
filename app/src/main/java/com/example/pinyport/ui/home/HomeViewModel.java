package com.example.pinyport.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {

    // Dữ liệu cho BarChart
    private MutableLiveData<ArrayList<BarEntry>> barChartData;
    // Dữ liệu cho PieChart
    private MutableLiveData<ArrayList<PieEntry>> pieChartData;

    // Danh sách màu sắc cho BarChart
    private MutableLiveData<int[]> barChartColors;
    // Danh sách màu sắc cho PieChart
    private MutableLiveData<int[]> pieChartColors;

    public HomeViewModel() {
        barChartData = new MutableLiveData<>();
        pieChartData = new MutableLiveData<>();
        barChartColors = new MutableLiveData<>();
        pieChartColors = new MutableLiveData<>();

        loadChartData();
        loadChartColors(); // Load thêm màu sắc cho biểu đồ
    }

    // Phương thức load dữ liệu cho biểu đồ
    private void loadChartData() {
        // Dữ liệu cho BarChart
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(1f, 2000f)); // Mon
        barEntries.add(new BarEntry(2f, 4000f)); // Tue
        barEntries.add(new BarEntry(3f, 3000f)); // Wed
        barChartData.setValue(barEntries);

        // Dữ liệu cho PieChart
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(75f, "Profit"));
        pieEntries.add(new PieEntry(25f, "Deducted"));
        pieChartData.setValue(pieEntries);
    }

    // Phương thức load màu sắc cho biểu đồ
    private void loadChartColors() {
        // Màu sắc cho BarChart
        int[] barColors = new int[]{
                android.graphics.Color.parseColor("#006FFD"), // Blue 500
                android.graphics.Color.parseColor("#006FFD"), // Blue 500
                android.graphics.Color.parseColor("#006FFD"), // Blue 500
//                android.graphics.Color.parseColor("#0D47A1")  // Dark Blue 900
        };
        barChartColors.setValue(barColors);

        // Màu sắc cho PieChart
        int[] pieColors = new int[]{
                android.graphics.Color.parseColor("#006FFD"), // Blue 500
                android.graphics.Color.parseColor("#03A9F4"),  // Light Blue 500
                android.graphics.Color.parseColor("#0D47A1"), // Dark Blue 900
        };
        pieChartColors.setValue(pieColors);
    }


    // Getter cho BarChart
    public LiveData<ArrayList<BarEntry>> getBarChartData() {
        return barChartData;
    }

    // Getter cho PieChart
    public LiveData<ArrayList<PieEntry>> getPieChartData() {
        return pieChartData;
    }

    // Getter cho màu sắc của BarChart
    public LiveData<int[]> getBarChartColors() {
        return barChartColors;
    }

    // Getter cho màu sắc của PieChart
    public LiveData<int[]> getPieChartColors() {
        return pieChartColors;
    }
}
