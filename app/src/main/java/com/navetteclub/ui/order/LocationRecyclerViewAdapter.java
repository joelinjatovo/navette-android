package com.navetteclub.ui.order;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.navetteclub.database.entity.Location;
import com.navetteclub.databinding.ViewholderLocationBinding;
import com.navetteclub.ui.OnClickItemListener;

import java.util.List;

public class LocationRecyclerViewAdapter extends RecyclerView.Adapter<LocationRecyclerViewAdapter.ViewHolder> {

    private List<Location> mItems;

    private final OnClickItemListener<Location> mListener;

    private int selected;

    public LocationRecyclerViewAdapter(OnClickItemListener<Location> listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderLocationBinding binding = ViewholderLocationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems==null?0:mItems.size();
    }

    public void setItems(List<Location> items){
        if (mItems == null) {
            mItems = items;
            notifyItemRangeInserted(0, mItems.size());
        } else {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mItems.size();
                }

                @Override
                public int getNewListSize() {
                    return items.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    Location oldItem = mItems.get(oldItemPosition);
                    Location newItem = items.get(newItemPosition);
                    return oldItem != null && newItem != null
                            && newItem.getId() != null && oldItem.getId() != null
                            && newItem.getId().equals(oldItem.getId());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Location oldItem = mItems.get(oldItemPosition);
                    Location newItem = items.get(newItemPosition);
                    return oldItem != null && newItem != null
                            && newItem.getId() != null && oldItem.getId() != null
                            && newItem.getId().equals(oldItem.getId())
                            && newItem.getLat() != null && oldItem.getLat() != null
                            && newItem.getLat().equals(oldItem.getLat())
                            && newItem.getLng() != null && oldItem.getLng() != null
                            && newItem.getLng().equals(oldItem.getLng());
                }
            });

            mItems = items;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    public List<Location> getItems() {
        return this.mItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Location mItem;
        ViewholderLocationBinding mBinding;

        public ViewHolder(ViewholderLocationBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        void setItem(Location item){
            mItem = item;
        }
    }
}

