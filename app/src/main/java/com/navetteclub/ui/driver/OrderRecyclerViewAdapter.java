package com.navetteclub.ui.driver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.navetteclub.R;
import com.navetteclub.database.entity.Notification;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.Point;
import com.navetteclub.database.entity.Ride;
import com.navetteclub.databinding.ViewholderOrderBinding;
import com.navetteclub.ui.OnClickItemListener;
import com.navetteclub.ui.notification.NotificationFragment;
import com.navetteclub.utils.Log;

import java.util.List;

public class OrderRecyclerViewAdapter extends RecyclerView.Adapter<OrderRecyclerViewAdapter.ViewHolder>{

    private List<OrderWithDatas> mItems;

    private final OnClickItemListener<OrderWithDatas> mListener;

    public OrderRecyclerViewAdapter(OnClickItemListener<OrderWithDatas> listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewholderOrderBinding itemBinding = ViewholderOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

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

    public void setItems(List<OrderWithDatas> items){
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
                    OrderWithDatas oldItem = mItems.get(oldItemPosition);
                    OrderWithDatas newItem = items.get(newItemPosition);
                    return oldItem.getOrder().getId() == newItem.getOrder().getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    OrderWithDatas oldItem = mItems.get(oldItemPosition);
                    OrderWithDatas newItem = items.get(newItemPosition);
                    return oldItem.getOrder()!=null && oldItem.getOrder().equals(newItem.getOrder());
                }
            });

            mItems = items;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ViewholderOrderBinding mBinding;
        OrderWithDatas mItem;

        ViewHolder(ViewholderOrderBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        void setItem(OrderWithDatas item){
            mItem = item;
            if(mItem!=null && mItem.getOrder()!=null){
                Order order = mItem.getOrder();
                mBinding.setAmount(order.getAmountStr());

                // Points
                if(mItem.getPoints()!=null){
                    // Origin
                    if(mItem.getPoints().size()>0){
                        Point point = mItem.getPoints().get(0);
                        if(point!=null){
                            mBinding.setOrigin(point);
                        }
                    }

                    // Destination
                    if(mItem.getPoints().size()>1) {
                        Point point = mItem.getPoints().get(1);
                        if(point!=null) {
                            mBinding.setDestination(point);
                        }
                    }

                    // Retours
                    if(mItem.getPoints().size()>2) {
                        Point point = mItem.getPoints().get(2);
                        if(point!=null) {
                            mBinding.setRetours(point);
                        }
                    }
                }
            }
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mBinding.amountLabel.getText() + "'";
        }
    }
}

