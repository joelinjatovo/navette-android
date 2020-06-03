package com.navetteclub.ui.driver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.navetteclub.BuildConfig;
import com.navetteclub.R;
import com.navetteclub.database.entity.Point;
import com.navetteclub.database.entity.RidePoint;
import com.navetteclub.databinding.ViewpagerRidePointBinding;
import com.navetteclub.ui.OnClickItemListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RidePointMapRecyclerViewAdapter extends RecyclerView.Adapter<RidePointMapRecyclerViewAdapter.BaseViewHolder>{
    private static final int VIEW_TYPE_STARTING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_COMPLETING = 2;

    private List<RidePoint> mItems;

    private OnClickItemListener<RidePoint> mListener;

    private boolean hasStarting = false;

    private boolean hasCompleting = false;


    public RidePointMapRecyclerViewAdapter setOnClickListener(OnClickItemListener<RidePoint> listener){
        mListener = listener;
        return this;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_STARTING:
                return new StartHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_ride_starting, parent, false));
            case VIEW_TYPE_COMPLETING:
                return new CompleteHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_ride_completing, parent, false));
            default:
                ViewpagerRidePointBinding itemBinding = ViewpagerRidePointBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new ViewHolder(itemBinding);
        }
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
        if(hasStarting && (position == 0)){
            return VIEW_TYPE_STARTING;
        }

        if(hasCompleting && (position == getItemCount() - 1)){
            return VIEW_TYPE_COMPLETING;
        }

        return VIEW_TYPE_NORMAL;
    }

    public void addStarting() {
        if(mItems!=null){
            hasStarting = true;
            mItems.add(0, new RidePoint());
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void addCompleting() {
        if(mItems!=null){
            hasCompleting = true;
            mItems.add(new RidePoint());
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void removeStarting() {
        int position = 0;
        RidePoint item = getItem(position);
        if (hasStarting && item != null && mItems!=null) {
            hasStarting = false;
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void removeCompleting() {
        int position = getItemCount() - 1;
        RidePoint item = getItem(position);
        if (hasCompleting && item != null && mItems!=null) {
            hasCompleting = false;
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

    private RidePoint getItem(int position) {
        if(mItems==null) return null;
        return mItems.get(position);
    }

    public void setItems(List<RidePoint> items){
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
                    RidePoint oldItem = mItems.get(oldItemPosition);
                    RidePoint newItem = items.get(newItemPosition);
                    return oldItem != null
                            && oldItem.getId()!=null
                            && newItem != null
                            && newItem.getId()!=null
                            && oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    RidePoint oldItem = mItems.get(oldItemPosition);
                    RidePoint newItem = items.get(newItemPosition);
                    return oldItem!=null && oldItem.equals(newItem);
                }
            });

            mItems = items;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    public static class ViewHolder extends BaseViewHolder {
        final ViewpagerRidePointBinding mBinding;
        RidePoint mItem;

        ViewHolder(ViewpagerRidePointBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        @Override
        protected void clear() {

        }

        void setItem(RidePoint item){
            mItem = item;
            if(mItem!=null){
                if(mItem.getUser()!=null){
                    mBinding.setUser(mItem.getUser());
                    if(mItem.getUser().getImageUrl()!=null){
                        Picasso.get()
                                .load(BuildConfig.BASE_URL + mItem.getUser().getImageUrl())
                                .placeholder(R.drawable.user_placeholder)
                                .error(R.drawable.user_placeholder)
                                .resize(300, 300)
                                .into(mBinding.avatarImageView);
                    }
                }

                Point point = mItem.getPoint();
                if(point!=null){
                    mBinding.setPoint(point.getName());
                }

                RidePoint ridePoint = mItem;
                if(ridePoint!=null){
                    mBinding.setRideType(ridePoint.getType());
                    if(ridePoint.getStatus()!=null){
                        switch(ridePoint.getStatus()){
                            case RidePoint.STATUS_PING:
                                mBinding.setRideStatus(getString(R.string.status_ping));
                                mBinding.statusTextView.setBackgroundResource(R.drawable.bg_text_alert_default);
                                mBinding.buttonAction.setText(R.string.button_next);
                                mBinding.buttonAction.setVisibility(View.GONE);
                            break;
                            case RidePoint.STATUS_ACTIVE:
                                mBinding.setRideStatus(getString(R.string.status_active));
                                mBinding.statusTextView.setBackgroundResource(R.drawable.bg_text_alert_default);
                                mBinding.buttonAction.setText(R.string.button_go);
                                mBinding.buttonAction.setVisibility(View.GONE);
                            break;
                            case RidePoint.STATUS_NEXT:
                                mBinding.setRideStatus(getString(R.string.status_next));
                                mBinding.statusTextView.setBackgroundResource(R.drawable.bg_text_alert_error);
                                if(RidePoint.TYPE_PICKUP.equals(ridePoint.getType())){
                                    mBinding.buttonAction.setText(R.string.button_arrive);
                                }else{
                                    mBinding.buttonAction.setText(R.string.button_drop_off);
                                }
                                mBinding.buttonAction.setVisibility(View.VISIBLE);
                            break;
                            case RidePoint.STATUS_ARRIVED:
                                mBinding.setRideStatus(getString(R.string.status_arrived));
                                mBinding.statusTextView.setBackgroundResource(R.drawable.bg_text_alert_error);
                                if(RidePoint.TYPE_PICKUP.equals(ridePoint.getType())){
                                    mBinding.buttonAction.setText(R.string.button_pickup);
                                }else{
                                    mBinding.buttonAction.setText(R.string.button_drop_off);
                                }
                                mBinding.buttonAction.setVisibility(View.VISIBLE);
                            break;
                            case RidePoint.STATUS_STARTED:
                                mBinding.statusTextView.setBackgroundResource(R.drawable.bg_text_alert_success);
                                mBinding.setRideStatus(getString(R.string.status_started));
                                mBinding.buttonAction.setVisibility(View.GONE);
                            break;
                        }
                    }
                }

                mBinding.getRoot().setOnClickListener(v -> {
                    if (null != mListener) {
                        mListener.onClick(v, getCurrentPosition(), mItem);
                    }
                });
                mBinding.buttonCancel.setOnClickListener(v -> {
                    if (null != mListener) {
                        mListener.onClick(v, getCurrentPosition(), mItem);
                    }
                });
                mBinding.buttonCall.setOnClickListener(v -> {
                    if (null != mListener) {
                        mListener.onClick(v, getCurrentPosition(), mItem);
                    }
                });
                mBinding.buttonAction.setOnClickListener(v -> {
                    if (null != mListener) {
                        mListener.onClick(v, getCurrentPosition(), mItem);
                    }
                });
            }

        }

        private String getString(@StringRes int res) {
            return mBinding.getRoot().getResources().getString(res);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString();
        }
    }


    public static class StartHolder extends BaseViewHolder {
        StartHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void clear() {
        }
    }

    public static class CompleteHolder extends BaseViewHolder {
        private Button button;

        CompleteHolder(View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.button_complete);
        }

        @Override
        protected void clear() {
        }

        void setItem(RidePoint item){
            mItem = item;
            button.setOnClickListener(v -> {
                if(mListener!=null){
                    mListener.onClick(v, getCurrentPosition(), mItem);
                }
            });
        }
    }

    public abstract static class BaseViewHolder extends RecyclerView.ViewHolder {

        private int mCurrentPosition;

        protected RidePoint mItem;

        protected OnClickItemListener<RidePoint>  mListener;

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        protected abstract void clear();

        void setListener(OnClickItemListener<RidePoint> listener){
            mListener = listener;
        }

        void setItem(RidePoint item){
            mItem = item;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener!=null){
                        mListener.onClick(v, getCurrentPosition(), mItem);
                    }
                }
            });
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

