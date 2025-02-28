package com.example.fetchrewards;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fetchrewards.ui.ItemAdapter;
import com.example.fetchrewards.viewmodel.ItemViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ItemViewModel viewModel;
    private ItemAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView errorText;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        errorText = findViewById(R.id.errorText);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        // Set up RecyclerView
        setupRecyclerView();

        // Set up SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refresh();
            }
        });

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ItemViewModel.class);

        // Observe data
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new ItemAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void observeViewModel() {
        // Observe API status
        viewModel.getStatus().observe(this, new Observer<ItemViewModel.ApiStatus>() {
            @Override
            public void onChanged(ItemViewModel.ApiStatus apiStatus) {
                switch (apiStatus) {
                    case LOADING:
                        progressBar.setVisibility(View.VISIBLE);
                        errorText.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        break;
                    case ERROR:
                        progressBar.setVisibility(View.GONE);
                        errorText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        break;
                    case SUCCESS:
                        progressBar.setVisibility(View.GONE);
                        errorText.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
                        break;
                }
            }
        });

        // Observe items
        viewModel.getItems().observe(this, new Observer<Map<Integer, List<Item>>>() {
            @Override
            public void onChanged(Map<Integer, List<Item>> integerListMap) {
                adapter.updateData(integerListMap);
            }
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                if (message != null && !message.isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
                    errorText.setText(getString(R.string.error_message, message));
                }
            }
        });
    }
}