package com.navetteclub.ui.notification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.navetteclub.R;
import com.navetteclub.database.entity.ClubAndPoint;
import com.navetteclub.database.entity.Notification;
import com.navetteclub.databinding.ViewholderNotificationBinding;
import com.navetteclub.databinding.ViewholderOrderBinding;
import com.navetteclub.ui.OnClickItemListener;
import com.navetteclub.ui.order.OrderRecyclerViewAdapter;

import java.util.List;

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.ViewHolder>{

    private List<Notification> mItems;

    private final OnClickItemListener<Notification> mListener;

    public NotificationRecyclerViewAdapter(OnClickItemListener<Notification> listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderNotificationBinding itemBinding = ViewholderNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(mItems.get(position));
        holder.mBinding.getRoot().setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onClick(v, position, holder.mItem);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mItems==null?0:mItems.size();
    }

    public void setItems(List<Notification> items){
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
                    Notification oldItem = mItems.get(oldItemPosition);
                    Notification newItem = items.get(newItemPosition);
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Notification oldItem = mItems.get(oldItemPosition);
                    Notification newItem = items.get(newItemPosition);
                    return oldItem.getData()!=null && oldItem.getData().equals(newItem.getData());
                }
            });

            mItems = items;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ViewholderNotificationBinding mBinding;
        private Notification mItem;

        ViewHolder(ViewholderNotificationBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void setItem(Notification item) {
            this.mItem = item;
            switch (mItem.getType()){
                case "App\\Notifications\\OrderStatus":
                    mBinding.icon.setImageResource(R.drawable.outline_remove_shopping_cart_24);
                    mBinding.title.setText(R.string.notification_new_order);
                    mBinding.subtitle.setText(R.string.notification_new_order_desc);
                    break;
                case "AB":
                    break;
            }
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mBinding.title.getText() + "'";
        }
    }
}

