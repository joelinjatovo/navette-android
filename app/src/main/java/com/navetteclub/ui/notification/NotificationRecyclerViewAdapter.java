package com.navetteclub.ui.notification;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.navetteclub.R;
import com.navetteclub.database.converter.ObjectConverter;
import com.navetteclub.database.entity.Item;
import com.navetteclub.database.entity.Notification;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.Ride;
import com.navetteclub.databinding.ViewholderDateBinding;
import com.navetteclub.databinding.ViewholderNotificationBinding;
import com.navetteclub.ui.OnClickItemListener;
import com.navetteclub.ui.order.OrderRecyclerViewAdapter;
import com.navetteclub.ui.order.OrderViewFragment;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.BaseViewHolder>{
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_DATE = 2;
    private boolean isLoaderVisible = false;

    private List<Notification> mItems;

    private final OnClickItemListener<Notification> mListener;

    public NotificationRecyclerViewAdapter(OnClickItemListener<Notification> listener) {
        mListener = listener;
        mItems = null;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_DATE:
                ViewholderDateBinding dateBinding = ViewholderDateBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new DateHolder(dateBinding);
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_loader, parent, false));
            default:
                ViewholderNotificationBinding itemBinding = ViewholderNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new ViewHolder(itemBinding);
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

        if(position < getItemCount() && "Date".equals(mItems.get(position).getType())){
            return VIEW_TYPE_DATE;
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

    private List<Notification> prepareItems(List<Notification> items) {
        List<Notification> output = null;
        if (items!=null) {
            output = new ArrayList<>();

            // Remove date
            List<Notification> list  = new ArrayList<>();
            for(int i = 0; i < items.size(); i++){
                if(items.get(i).getType() != null && !items.get(i).getType().equals("Date")){
                    list.add(items.get(i));
                }else if(isLoaderVisible && (i == items.size() - 1)){
                    // Do not remove loader item
                    list.add(items.get(i));
                }
            }

            // Add date
            if(list.size() > 0 && list.get(0).getType()!=null){
                Notification date = new Notification();
                date.setCreatedAt(list.get(0).getCreatedAt());
                date.setType("Date");
                output.add(date);

                for(int i = 1; i < list.size(); i++){
                    Date date0 = items.get(i-1).getCreatedAt();
                    Date date1 = items.get(i).getCreatedAt();
                    if(date0!=null && date1!=null){
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
                        String formatDate0 = format.format(date0);
                        String formatDate1 = format.format(date1);
                        if(!formatDate0.equals(formatDate1)){
                            date = new Notification();
                            date.setCreatedAt(list.get(i).getCreatedAt());
                            date.setType("Date");
                            output.add(date);
                        }
                    }
                    output.add(items.get(i));
                }
            }
        }
        return output;
    }

    public void clear() {
        if(mItems!=null) {
            mItems.clear();
            notifyDataSetChanged();
        }
    }

    private Notification getItem(int position) {
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
        items = prepareItems(items);

        if (mItems == null) {
            mItems = items;
            notifyItemRangeInserted(0, mItems.size());
        } else {
            List<Notification> finalItems = items;

            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mItems.size();
                }

                @Override
                public int getNewListSize() {
                    return finalItems.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    Notification oldItem = mItems.get(oldItemPosition);
                    Notification newItem = finalItems.get(newItemPosition);
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Notification oldItem = mItems.get(oldItemPosition);
                    Notification newItem = finalItems.get(newItemPosition);
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
                        mBinding.subtitle.setText(Utils.formatDateToString(mItem.getCreatedAt()));
                        setTitleByOrder();
                        break;
                    case "App\\Notifications\\ItemStatus":
                        mBinding.icon.setImageResource(R.drawable.outline_access_time_black_24);
                        mBinding.subtitle.setText(Utils.formatDateToString(mItem.getCreatedAt()));
                        setTitleByOrderItem();
                        break;
                    case "App\\Notifications\\RideStatus":
                        mBinding.icon.setImageResource(R.drawable.outline_directions_car_black_24);
                        mBinding.subtitle.setText(Utils.formatDateToString(mItem.getCreatedAt()));
                        setTitleByRide();
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

        private String  getString(@StringRes int string) {
            return mBinding.getRoot().getResources().getString(string);
        }

        private void setTitleByOrder() {
            Object orderData = mItem.getData();
            String orderId = "?";
            try {
                String payload = ObjectConverter.fromObject(orderData);
                JSONObject jsonData = new JSONObject(payload);
                orderId = String.valueOf(jsonData.getInt("order_id"));
                String newStatus = jsonData.getString("newStatus");
                switch (newStatus){
                    case Order.STATUS_PING:
                        mBinding.title.setText(String.format(getString(R.string.notification_order_ping), orderId));
                        break;
                    case Order.STATUS_OK:
                        mBinding.title.setText(String.format(getString(R.string.notification_order_ok), orderId));
                        break;
                    case Order.STATUS_ACTIVE:
                        mBinding.title.setText(String.format(getString(R.string.notification_order_active), orderId));
                        break;
                    case Order.STATUS_COMPLETABLE:
                        mBinding.title.setText(String.format(getString(R.string.notification_order_completable), orderId));
                        break;
                    case Order.STATUS_COMPLETED:
                        mBinding.title.setText(String.format(getString(R.string.notification_order_completed), orderId));
                        break;
                    case Order.STATUS_CANCELED:
                        mBinding.title.setText(String.format(getString(R.string.notification_order_canceled), orderId));
                        break;
                    default:
                        break;
                }
            } catch (JSONException ignored) {
                mBinding.title.setText(String.format(getString(R.string.notification_order_status), orderId));
            }
        }

        private void setTitleByOrderItem() {
            Object itemData = mItem.getData();
            String itemId = "?";
            try {
                String payload = ObjectConverter.fromObject(itemData);
                JSONObject jsonData = new JSONObject(payload);
                itemId = String.valueOf(jsonData.getInt("item_id"));
                String newStatus = jsonData.getString("newStatus");
                switch (newStatus){
                    case Item.STATUS_PING:
                        mBinding.title.setText(String.format(getString(R.string.notification_item_ping), itemId));
                        break;
                    case Item.STATUS_ACTIVE:
                        mBinding.title.setText(String.format(getString(R.string.notification_item_active), itemId));
                        break;
                    case Item.STATUS_NEXT:
                        mBinding.title.setText(String.format(getString(R.string.notification_item_next), itemId));
                        break;
                    case Item.STATUS_ONLINE:
                        mBinding.title.setText(String.format(getString(R.string.notification_item_online), itemId));
                        break;
                    case Item.STATUS_COMPLETED:
                        mBinding.title.setText(String.format(getString(R.string.notification_item_completed), itemId));
                        break;
                    case Item.STATUS_CANCELED:
                        mBinding.title.setText(String.format(getString(R.string.notification_item_canceled), itemId));
                        break;
                }
            } catch (JSONException ignored) {
                mBinding.title.setText(String.format(getString(R.string.notification_item_status), itemId));
            }
        }

        private void setTitleByRide() {
            Object rideData = mItem.getData();
            String rideId = "?";
            try {
                String payload = ObjectConverter.fromObject(rideData);
                JSONObject jsonData = new JSONObject(payload);
                rideId = String.valueOf(jsonData.getInt("ride_id"));
                String newStatus = jsonData.getString("newStatus");
                switch (newStatus){
                    case Ride.STATUS_PING:
                        mBinding.title.setText(String.format(getString(R.string.notification_ride_ping), rideId));
                        break;
                    case Ride.STATUS_ACTIVE:
                        mBinding.title.setText(String.format(getString(R.string.notification_ride_active), rideId));
                        break;
                    case Ride.STATUS_COMPLETABLE:
                        mBinding.title.setText(String.format(getString(R.string.notification_ride_completable), rideId));
                        break;
                    case Ride.STATUS_COMPLETED:
                        mBinding.title.setText(String.format(getString(R.string.notification_ride_completed), rideId));
                        break;
                    case Ride.STATUS_CANCELED:
                        mBinding.title.setText(String.format(getString(R.string.notification_ride_canceled), rideId));
                        break;
                }
            } catch (JSONException ignored) {
                mBinding.title.setText(String.format(getString(R.string.notification_ride_status), rideId));
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

    public static class DateHolder extends BaseViewHolder {
        final ViewholderDateBinding mBinding;

        DateHolder(ViewholderDateBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void setItem(Notification item) {
            this.mItem = item;
            mBinding.setDate(Utils.formatDateToString(item.getCreatedAt(), DateUtils.DAY_IN_MILLIS));
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

