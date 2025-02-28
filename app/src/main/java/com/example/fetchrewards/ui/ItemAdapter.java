package com.example.fetchrewards.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fetchrewards.Item;
import com.example.fetchrewards.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private final List<ListItem> dataList = new ArrayList<>();
    private final Map<Integer, Boolean> expandStateMap = new HashMap<>();
    private Map<Integer, List<Item>> originalData = new HashMap<>();

    public void updateData(Map<Integer, List<Item>> itemMap) {
        // Store the original data for rebuilding the list
        originalData = new HashMap<>(itemMap);
        rebuildList();
    }

    private void rebuildList() {
        dataList.clear();

        for (Map.Entry<Integer, List<Item>> entry : originalData.entrySet()) {
            int listId = entry.getKey();

            // If first time seeing this listId, default to expanded
            if (!expandStateMap.containsKey(listId)) {
                expandStateMap.put(listId, true); // Default to expanded
            }

            // Add header for this list ID
            dataList.add(new HeaderItem(listId, "List ID: " + listId));

            // Add all items for this list ID if section is expanded
            if (expandStateMap.get(listId)) {
                for (Item item : entry.getValue()) {
                    dataList.add(new ItemData(item));
                }
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position) instanceof HeaderItem ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_HEADER) {
            View view = inflater.inflate(R.layout.list_header, parent, false);
            return new HeaderViewHolder(view, this);
        } else {
            View view = inflater.inflate(R.layout.list_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListItem listItem = dataList.get(position);

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind((HeaderItem) listItem);
        } else if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).bind((ItemData) listItem);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // Method to toggle section expansion
    void toggleSectionExpanded(int listId) {
        // Only proceed if we have this listId in our state map
        if (expandStateMap.containsKey(listId)) {
            // Toggle expansion state
            boolean isCurrentlyExpanded = expandStateMap.get(listId);
            expandStateMap.put(listId, !isCurrentlyExpanded);

            // Find the header position
            int headerPosition = -1;
            for (int i = 0; i < dataList.size(); i++) {
                if (dataList.get(i) instanceof HeaderItem &&
                        ((HeaderItem) dataList.get(i)).getListId() == listId) {
                    headerPosition = i;
                    break;
                }
            }

            if (headerPosition != -1) {
                // Notify that this particular header has changed (for icon rotation)
                notifyItemChanged(headerPosition);

                // Handle expansion or collapse
                if (isCurrentlyExpanded) {
                    // Collapsing - remove child items
                    int itemsRemoved = 0;
                    while (headerPosition + 1 < dataList.size() &&
                            dataList.get(headerPosition + 1) instanceof ItemData) {
                        dataList.remove(headerPosition + 1);
                        itemsRemoved++;
                    }

                    if (itemsRemoved > 0) {
                        notifyItemRangeRemoved(headerPosition + 1, itemsRemoved);
                    }
                } else {
                    // Expanding - add child items
                    List<Item> items = originalData.get(listId);
                    List<ListItem> itemsToAdd = new ArrayList<>();

                    for (Item item : items) {
                        itemsToAdd.add(new ItemData(item));
                    }

                    dataList.addAll(headerPosition + 1, itemsToAdd);
                    notifyItemRangeInserted(headerPosition + 1, itemsToAdd.size());
                }
            }
        }
    }

    // Abstract base class for list items
    private abstract static class ListItem {
    }

    // Header item
    private static class HeaderItem extends ListItem {
        private final int listId;
        private final String title;

        HeaderItem(int listId, String title) {
            this.listId = listId;
            this.title = title;
        }

        public int getListId() {
            return listId;
        }

        public String getTitle() {
            return title;
        }
    }

    // Data item
    private static class ItemData extends ListItem {
        private final Item item;

        ItemData(Item item) {
            this.item = item;
        }

        public Item getItem() {
            return item;
        }
    }

    // ViewHolder for headers
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView headerText;
        private final ImageView expandIcon;
        private HeaderItem currentItem;
        private final ItemAdapter adapter;

        HeaderViewHolder(@NonNull View itemView, ItemAdapter adapter) {
            super(itemView);
            headerText = itemView.findViewById(R.id.headerText);
            expandIcon = itemView.findViewById(R.id.expandIcon);
            this.adapter = adapter;

            // Set click listener for the entire header
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentItem != null) {
                        adapter.toggleSectionExpanded(currentItem.getListId());
                    }
                }
            });
        }

        void bind(HeaderItem header) {
            currentItem = header;
            headerText.setText(header.getTitle());

            // Set the icon based on current expansion state
            boolean isExpanded = adapter.expandStateMap.get(header.getListId());
            expandIcon.setRotation(isExpanded ? 180f : 0f); // Rotate icon based on state
        }
    }

    // ViewHolder for items
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemIdText;
        private final TextView itemNameText;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemIdText = itemView.findViewById(R.id.itemId);
            itemNameText = itemView.findViewById(R.id.itemName);
        }

        void bind(ItemData data) {
            Item item = data.getItem();
            itemIdText.setText("ID: " + item.getId());
            itemNameText.setText(item.getName());
        }
    }
}