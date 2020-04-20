package com.navetteclub.ui.order;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderRecyclerViewAdapter extends RecyclerView.Adapter<OrderRecyclerViewAdapter.BaseViewHolder>{
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    private List<OrderWithDatas> mItems;

    private final OnClickItemListener<OrderWithDatas>  mListener;

    public OrderRecyclerViewAdapter(OnClickItemListener<OrderWithDatas> listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                ViewholderOrderBinding itemBinding = ViewholderOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

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
            mItems.add(new OrderWithDatas());
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = getItemCount() - 1;
        OrderWithDatas item = getItem(position);
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

    OrderWithDatas getItem(int position) {
        if(mItems==null) return null;
        return mItems.get(position);
    }

    public void addItems(List<OrderWithDatas> items) {
        if (mItems == null) {
            setItems(items);
        }else{
            List<OrderWithDatas> clone = new ArrayList<>(mItems);
            clone.addAll(items);
            setItems(clone);
        }
    }

    private void setItems(List<OrderWithDatas> items){
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
                    return oldItem.getOrder() != null &&
                            newItem.getOrder() != null &&
                            oldItem.getOrder().getId() == newItem.getOrder().getId();
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

    public static class ViewHolder extends BaseViewHolder {
        final ViewholderOrderBinding mBinding;

        ViewHolder(ViewholderOrderBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
        }

        @Override
        void setItem(OrderWithDatas item){
            super.setItem(item);
            if(mItem!=null){
                Order order = mItem.getOrder();
                if(order!=null){
                    mBinding.setOrderId(order.getRid());
                    mBinding.setAmount(order.getAmountStr());
                    mBinding.setStatus(order.getStatus());
                    mBinding.setPlace(order.getPlace());

                    long now = System.currentTimeMillis();
                    Date lastUpdated = order.getCreatedAt();
                    CharSequence date = DateUtils.getRelativeTimeSpanString(lastUpdated.getTime(), now, DateUtils.MINUTE_IN_MILLIS);
                    mBinding.setDate((String) date);
                }

                // Club
                Club club = mItem.getClub();
                if(club!=null){
                    mBinding.setPoint2Title("Club");
                    mBinding.setPoint2(club.getName());
                }

                //Items
                List<ItemWithDatas> items = mItem.getItems();
                int i = 0;
                if(items!=null && !items.isEmpty()){
                    for(ItemWithDatas itemWithData: items){
                        if(itemWithData!=null && itemWithData.getItem()!=null && itemWithData.getPoint()!=null) {
                            if(Order.TYPE_BACK.equals(itemWithData.getItem().getType())){
                                mBinding.setPoint3Title("Drop");
                                mBinding.setPoint3(itemWithData.getPoint().getName());
                            }else{
                                mBinding.setPoint1Title("Pickup");
                                mBinding.setPoint1(itemWithData.getPoint().getName());
                            }
                            i++;
                            if(i>2){
                                break;
                            }
                        }
                    }
                }

                mBinding.getRoot().setOnClickListener(v -> {
                    if (null != mListener) {
                        mListener.onClick(v, getCurrentPosition(), mItem);
                    }
                });
                mBinding.moreButtom.setOnClickListener(v -> {
                    if (null != mListener) {
                        mListener.onClick(v, getCurrentPosition(), mItem);
                    }
                });
            }
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mBinding.amountValue.getText() + "'";
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

        protected OrderWithDatas mItem;

        protected OnClickItemListener<OrderWithDatas>  mListener;

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        protected abstract void clear();

        void setListener(OnClickItemListener<OrderWithDatas> listener){
            mListener = listener;
        }

        void setItem(OrderWithDatas item){
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

