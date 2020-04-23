package com.navetteclub.ui.driver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.navetteclub.R;
import com.navetteclub.database.entity.Car;
import com.navetteclub.database.entity.Notification;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.Point;
import com.navetteclub.database.entity.Ride;
import com.navetteclub.database.entity.RideWithDatas;
import com.navetteclub.databinding.ViewholderNotificationBinding;
import com.navetteclub.databinding.ViewholderOrderBinding;
import com.navetteclub.databinding.ViewholderRideBinding;
import com.navetteclub.ui.OnClickItemListener;
import com.navetteclub.ui.notification.NotificationRecyclerViewAdapter;
import com.navetteclub.ui.order.OrdersFragment;
import com.navetteclub.utils.Constants;
import com.navetteclub.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RideRecyclerViewAdapter extends RecyclerView.Adapter<RideRecyclerViewAdapter.BaseViewHolder>{
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    private List<RideWithDatas> mItems;

    private final OnClickItemListener<RideWithDatas> mListener;

    public RideRecyclerViewAdapter(OnClickItemListener<RideWithDatas> listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING) {
            return new ProgressHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_loader, parent, false));
        }
        ViewholderRideBinding itemBinding = ViewholderRideBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, int position) {
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
            mItems.add(new RideWithDatas());
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = getItemCount() - 1;
        RideWithDatas item = getItem(position);
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

    RideWithDatas getItem(int position) {
        if(mItems==null) return null;
        return mItems.get(position);
    }

    public void addItems(List<RideWithDatas> items) {
        if (mItems == null) {
            setItems(items);
        }else{
            List<RideWithDatas> clone = new ArrayList<>(mItems);
            clone.addAll(items);
            setItems(clone);
        }
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

    static class ViewHolder extends BaseViewHolder {
        final ViewholderRideBinding mBinding;
        RideWithDatas mItem;

        ViewHolder(ViewholderRideBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        @Override
        protected void clear() {

        }

        void setItem(RideWithDatas item){
            mItem = item;
            if(mItem!=null){
                Car car = mItem.getCar();
                /*
                if(car!=null && car.getImageUrl()!=null) {
                    Picasso.get()
                            .load(Constants.getBaseUrl() + car.getImageUrl())
                            .placeholder(R.drawable.car_placeholder)
                            .error(R.drawable.car_placeholder)
                            .resize(80,80)
                            .into(mBinding.carImageView);
                }
                */
                Ride ride = mItem.getRide();
                if(ride!=null) {
                    mBinding.setRideId(String.valueOf(ride.getId()));
                    mBinding.setDate(Utils.formatDateToString(ride.getStartedAt()));
                    mBinding.setStatus(ride.getStatus());
                    if(ride.getStatus()!=null){
                        switch (ride.getStatus()) {
                            case Ride.STATUS_PING:
                                mBinding.statusTextView.setText(R.string.status_ping);
                                mBinding.statusTextView.setBackgroundResource(R.drawable.bg_text_alert_default);
                                mBinding.statusTextView.setTextColor(mBinding.getRoot().getContext().getResources().getColor(R.color.white));
                                break;
                            case Ride.STATUS_COMPLETED:
                                mBinding.statusTextView.setText(R.string.status_completed);
                                mBinding.statusTextView.setBackgroundResource(R.drawable.bg_text_alert_success);
                                mBinding.statusTextView.setTextColor(mBinding.getRoot().getContext().getResources().getColor(R.color.colorText));
                                break;
                            case Ride.STATUS_CANCELED:
                                mBinding.statusTextView.setText(R.string.status_canceled);
                                mBinding.statusTextView.setBackgroundResource(R.drawable.bg_text_alert_error);
                                mBinding.statusTextView.setTextColor(mBinding.getRoot().getContext().getResources().getColor(R.color.white));
                                break;
                            case Ride.STATUS_ACTIVE:
                                mBinding.statusTextView.setText(R.string.status_active);
                                mBinding.statusTextView.setBackgroundResource(R.drawable.bg_text_alert_success);
                                mBinding.statusTextView.setTextColor(mBinding.getRoot().getContext().getResources().getColor(R.color.colorText));
                                break;
                            case Ride.STATUS_COMPLETABLE:
                                mBinding.statusTextView.setText(R.string.status_completable);
                                mBinding.statusTextView.setBackgroundResource(R.drawable.bg_text_alert_error);
                                mBinding.statusTextView.setTextColor(mBinding.getRoot().getContext().getResources().getColor(R.color.white));
                                break;
                        }

                    }
                }
                if(mItem.getPoints()!=null){
                    mBinding.setPointCount(mItem.getPoints().size());
                }
            }

            mBinding.getRoot().setOnClickListener(v -> {
                if (null != mListener) {
                    mListener.onClick(v, getCurrentPosition(), mItem);
                }
            });

            mBinding.moreButtom.setOnClickListener(
                    v -> {
                        if (null != mListener) {
                            mListener.onClick(v, getCurrentPosition(), mItem);
                        }
                    });
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

        protected RideWithDatas mItem;

        protected OnClickItemListener<RideWithDatas>  mListener;

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        protected abstract void clear();

        void setListener(OnClickItemListener<RideWithDatas> listener){
            mListener = listener;
        }

        void setItem(RideWithDatas item){
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

