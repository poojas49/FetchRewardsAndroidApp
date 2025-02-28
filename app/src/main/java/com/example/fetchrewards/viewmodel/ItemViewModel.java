package com.example.fetchrewards.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fetchrewards.Item;
import com.example.fetchrewards.network.ApiService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemViewModel extends ViewModel {

    public enum ApiStatus {
        LOADING,
        SUCCESS,
        ERROR
    }

    private final MutableLiveData<ApiStatus> status = new MutableLiveData<>();
    private final MutableLiveData<Map<Integer, List<Item>>> items = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public ItemViewModel() {
        loadItems();
    }

    public LiveData<ApiStatus> getStatus() {
        return status;
    }

    public LiveData<Map<Integer, List<Item>>> getItems() {
        return items;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadItems() {
        status.setValue(ApiStatus.LOADING);

        ApiService.getInstance().getItems().enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    processItems(response.body());
                    status.setValue(ApiStatus.SUCCESS);
                } else {
                    errorMessage.setValue("Error: " + response.code());
                    status.setValue(ApiStatus.ERROR);
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                errorMessage.setValue("Network error: " + t.getMessage());
                status.setValue(ApiStatus.ERROR);
            }
        });
    }

    private void processItems(List<Item> itemList) {
        // Filter out items with null or blank names
        List<Item> filteredItems = new ArrayList<>();
        for (Item item : itemList) {
            if (item.getName() != null && !item.getName().trim().isEmpty()) {
                filteredItems.add(item);
            }
        }

        // Group by listId
        Map<Integer, List<Item>> groupedItems = new HashMap<>();
        for (Item item : filteredItems) {
            int listId = item.getListId();
            if (!groupedItems.containsKey(listId)) {
                groupedItems.put(listId, new ArrayList<>());
            }
            groupedItems.get(listId).add(item);
        }

        // Sort each group by name
        for (Map.Entry<Integer, List<Item>> entry : groupedItems.entrySet()) {
            Collections.sort(entry.getValue(), new Comparator<Item>() {
                @Override
                public int compare(Item item1, Item item2) {
                    return item1.getName().compareTo(item2.getName());
                }
            });
        }

        // Sort by listId using TreeMap
        Map<Integer, List<Item>> sortedMap = new TreeMap<>(groupedItems);

        items.setValue(sortedMap);
    }

    public void refresh() {
        loadItems();
    }
}