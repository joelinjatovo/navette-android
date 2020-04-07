package com.navetteclub.ui.driver;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.Point;
import com.navetteclub.database.entity.RideWithDatas;
import com.navetteclub.databinding.ViewholderOrderBinding;
import com.navetteclub.databinding.ViewholderRideBinding;
import com.navetteclub.ui.OnClickItemListener;
import com.navetteclub.ui.order.OrdersFragment;

import java.util.List;

public class RideRecyclerViewAdapter extends RecyclerView.Adapter<RideRecyclerViewAdapter.ViewHolder>{

    private List<RideWithDatas> mItems;

    private final OnClickItemListener<RideWithDatas> mListener;

    public RideRecyclerViewAdapter(OnClickItemListener<RideWithDatas> listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderRideBinding itemBinding = ViewholderRideBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(mItems.get(position));
        holder.mBinding.getRoot().setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onClick(v, position, holder.mItem);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mItems==null?0:mItems.size();
    }

    public void setItems(List<RideWithDatas> items){
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
                    RideWithDatas oldItem = mItems.get(oldItemPosition);
                    RideWithDatas newItem = items.get(newItemPosition);
                    return oldItem.getRide().getId() == newItem.getRide().getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    RideWithDatas oldItem = mItems.get(oldItemPosition);
                    RideWithDatas newItem = items.get(newItemPosition);
                    return oldItem.getRide()!=null && oldItem.getRide().equals(newItem.getRide());
                }
            });

            mItems = items;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ViewholderRideBinding mBinding;
        RideWithDatas mItem;

        ViewHolder(ViewholderRideBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        void setItem(RideWithDatas item){
            mItem = item;
            if(mItem!=null && mItem.getRide()!=null){
                // Nothing
                mBinding.title.setText(mItem.getDriver().getName());
            }
        }
    }
}

