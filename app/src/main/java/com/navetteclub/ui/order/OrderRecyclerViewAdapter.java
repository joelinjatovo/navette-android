package com.navetteclub.ui.order;

import android.text.format.DateUtils;
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
import com.navetteclub.database.entity.Club;
import com.navetteclub.database.entity.ItemWithDatas;
import com.navetteclub.database.entity.Notification;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.Point;
import com.navetteclub.databinding.ViewholderOrderBinding;
import com.navetteclub.ui.OnClickItemListener;
import com.navetteclub.ui.notification.NotificationFragment;
import com.navetteclub.utils.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrderRecyclerViewAdapter extends RecyclerView.Adapter<OrderRecyclerViewAdapter.ViewHolder>{

    private List<OrderWithDatas> mItems;

    private final OnClickItemListener<OrderWithDatas>  mListener;

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
        holder.mBinding.moreButtom.setOnClickListener(v -> {
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
            if(mItem!=null){
                Order order = mItem.getOrder();
                if(order!=null){
                    mBinding.setOrderId(order.getRid());
                    mBinding.setAmount(order.getAmountStr());
                    mBinding.setStatus(order.getStatus());
                    mBinding.setPlace(order.getPlace());

                    long now = System.currentTimeMillis();
                    Date lastUpdated = order.getCreatedAt();
                    CharSequence date = DateUtils.getRelativeTimeSpanString(lastUpdated.getTime(), now, DateUtils.DAY_IN_MILLIS);
                    mBinding.setDate((String) date);

                    // Points
                    if (order.getType()!=null){
                        switch (order.getType()){
                            case Order.TYPE_GO:
                                mBinding.setPointTitle("Pickup");
                                break;
                            case Order.TYPE_BACK:
                                mBinding.setPointTitle("Drop");
                                break;
                            case Order.TYPE_GO_BACK:
                                mBinding.setPointTitle("NN");
                                break;
                        }
                    }
                }

                // Club
                Club club = mItem.getClub();
                if(club!=null){
                    mBinding.setClub(club.getName());
                }

                //Items
                List<ItemWithDatas> items = mItem.getItems();
                if(items!=null && !items.isEmpty()){
                    for(ItemWithDatas itemWithData: items){
                        if(itemWithData!=null && itemWithData.getPoint()!=null) {
                            mBinding.setItem1(itemWithData.getPoint().getName());
                        }
                    }
                }
            }
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mBinding.amountValue.getText() + "'";
        }
    }
}

