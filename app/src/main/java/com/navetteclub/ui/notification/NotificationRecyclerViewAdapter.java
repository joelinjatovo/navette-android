package com.navetteclub.ui.notification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.navetteclub.R;
import com.navetteclub.database.entity.Notification;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.databinding.ViewholderNotificationBinding;
import com.navetteclub.ui.OnClickItemListener;
import com.navetteclub.ui.order.OrderRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.BaseViewHolder>{
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    private List<Notification> mItems;

    private final OnClickItemListener<Notification> mListener;

    public NotificationRecyclerViewAdapter(OnClickItemListener<Notification> listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                ViewholderNotificationBinding itemBinding = ViewholderNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

                return new ViewHolder(itemBinding);
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_loader, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, int position) {
        holder.onBind(position);
        holder.setItem(mItems.get(position));
        holder.setListener(mListener);
    }

    @Override
    public int getItemCount() {
        return mItems==null?0:mItems.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            if(position == getItemCount() - 1){
                return VIEW_TYPE_LOADING;
            }
        }
        return VIEW_TYPE_NORMAL;
    }

    public void addLoading() {
        if(mItems!=null){
            isLoaderVisible = true;
            mItems.add(new Notification());
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = getItemCount() - 1;
        Notification item = getItem(position);
        if (item != null && mItems!=null) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        if(mItems!=null) {
            mItems.clear();
            notifyDataSetChanged();
        }
    }

    Notification getItem(int position) {
        if(mItems==null) return null;
        return mItems.get(position);
    }

    public void addItems(List<Notification> items) {
        if (mItems == null) {
            setItems(items);
        }else{
            List<Notification> clone = new ArrayList<>(mItems);
            clone.addAll(items);
            setItems(clone);
        }
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

    public static class ViewHolder extends BaseViewHolder {
        final ViewholderNotificationBinding mBinding;
        private Notification mItem;

        ViewHolder(ViewholderNotificationBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void setItem(Notification item) {
            this.mItem = item;
            if(mItem!=null) {
                switch (mItem.getType()) {
                    case "App\\Notifications\\OrderStatus":
                        mBinding.icon.setImageResource(R.drawable.outline_remove_shopping_cart_24);
                        mBinding.title.setText(R.string.notification_new_order);
                        mBinding.subtitle.setText(R.string.notification_new_order_desc);
                        break;
                    case "AB":
                        break;
                }

                mBinding.getRoot().setOnClickListener(v -> {
                    if (null != mListener) {
                        mListener.onClick(v, getCurrentPosition(), mItem);
                    }
                });
            }
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mBinding.title.getText() + "'";
        }

        @Override
        protected void clear() {

        }
    }

    public static class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void clear() {
        }
    }

    public abstract static class BaseViewHolder extends RecyclerView.ViewHolder {

        private int mCurrentPosition;

        protected Notification mItem;

        protected OnClickItemListener<Notification>  mListener;

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        protected abstract void clear();

        void setListener(OnClickItemListener<Notification> listener){
            mListener = listener;
        }

        void setItem(Notification item){
            mItem = item;
        }

        public void onBind(int position) {
            mCurrentPosition = position;
            clear();
        }

        public int getCurrentPosition() {
            return mCurrentPosition;
        }
    }
}

