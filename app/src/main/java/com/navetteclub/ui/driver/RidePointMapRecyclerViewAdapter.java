package com.navetteclub.ui.driver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.navetteclub.R;
import com.navetteclub.database.entity.RidePoint;
import com.navetteclub.database.entity.RidePointWithDatas;
import com.navetteclub.database.entity.RideWithDatas;
import com.navetteclub.databinding.ViewholderRideBinding;
import com.navetteclub.databinding.ViewholderRidePointBinding;
import com.navetteclub.databinding.ViewpagerRidePointBinding;
import com.navetteclub.databinding.ViewpagerRidePointBindingImpl;
import com.navetteclub.ui.OnClickItemListener;
import com.navetteclub.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RidePointMapRecyclerViewAdapter extends RecyclerView.Adapter<RidePointMapRecyclerViewAdapter.BaseViewHolder>{
    private static final int VIEW_TYPE_STARTING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_COMPLETING = 2;

    private List<RidePointWithDatas> mItems;

    private OnClickItemListener<RidePointWithDatas> mListener;

    private boolean hasStarting = false;

    private boolean hasCompleting = false;


    public RidePointMapRecyclerViewAdapter setOnClickListener(OnClickItemListener<RidePointWithDatas> listener){
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
            mItems.add(0, new RidePointWithDatas());
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void addCompleting() {
        if(mItems!=null){
            hasCompleting = true;
            mItems.add(new RidePointWithDatas());
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void removeStarting() {
        int position = 0;
        RidePointWithDatas item = getItem(position);
        if (hasStarting && item != null && mItems!=null) {
            hasStarting = false;
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void removeCompleting() {
        int position = getItemCount() - 1;
        RidePointWithDatas item = getItem(position);
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

    private RidePointWithDatas getItem(int position) {
        if(mItems==null) return null;
        return mItems.get(position);
    }

    public void setItems(List<RidePointWithDatas> items){
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
                    RidePointWithDatas oldItem = mItems.get(oldItemPosition);
                    RidePointWithDatas newItem = items.get(newItemPosition);
                    return oldItem.getRidePoint() != null
                            && oldItem.getRidePoint().getId()!=null
                            && newItem.getRidePoint() != null
                            && newItem.getRidePoint().getId()!=null
                            && oldItem.getRidePoint().getId().equals(newItem.getRidePoint().getId());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    RidePointWithDatas oldItem = mItems.get(oldItemPosition);
                    RidePointWithDatas newItem = items.get(newItemPosition);
                    return oldItem.getRidePoint()!=null && oldItem.getRidePoint().equals(newItem.getRidePoint());
                }
            });

            mItems = items;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    public static class ViewHolder extends BaseViewHolder {
        final ViewpagerRidePointBinding mBinding;
        RidePointWithDatas mItem;

        ViewHolder(ViewpagerRidePointBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        @Override
        protected void clear() {

        }

        void setItem(RidePointWithDatas item){
            mItem = item;
            if(mItem!=null){
                RidePoint ridePoint = mItem.getRidePoint();
                if(ridePoint!=null){
                    mBinding.setRideType(ridePoint.getType());
                    mBinding.setRideStatus(ridePoint.getStatus());
                    if(RidePoint.STATUS_PING.equals(ridePoint.getStatus())){
                        mBinding.actionButton.setText(R.string.button_next);
                        mBinding.actionButton.setVisibility(View.GONE);
                    }
                    if(RidePoint.STATUS_ACTIVE.equals(ridePoint.getStatus())){
                        mBinding.actionButton.setText(R.string.button_go);
                        mBinding.actionButton.setVisibility(View.GONE);
                    }
                    if(RidePoint.STATUS_NEXT.equals(ridePoint.getStatus())){
                        mBinding.actionButton.setText(R.string.button_active);
                        mBinding.actionButton.setVisibility(View.VISIBLE);
                    }
                    if(RidePoint.STATUS_ONLINE.equals(ridePoint.getStatus())){
                        mBinding.actionButton.setVisibility(View.GONE);
                    }
                }
                if(mItem.getUser()!=null){
                    mBinding.setUser(mItem.getUser());
                    if(mItem.getUser().getImageUrl()!=null){
                        Picasso.get()
                                .load(Constants.getBaseUrl() + mItem.getUser().getImageUrl())
                                .placeholder(R.drawable.user_placeholder)
                                .error(R.drawable.user_placeholder)
                                .resize(100, 100)
                                .into(mBinding.avatarImageView);
                    }
                }

                mBinding.getRoot().setOnClickListener(v -> {
                    if (null != mListener) {
                        mListener.onClick(v, getCurrentPosition(), mItem);
                    }
                });
                mBinding.cancelButton.setOnClickListener(v -> {
                    if (null != mListener) {
                        mListener.onClick(v, getCurrentPosition(), mItem);
                    }
                });
                mBinding.callButtom.setOnClickListener(v -> {
                    if (null != mListener) {
                        mListener.onClick(v, getCurrentPosition(), mItem);
                    }
                });
                mBinding.actionButton.setOnClickListener(v -> {
                    if (null != mListener) {
                        mListener.onClick(v, getCurrentPosition(), mItem);
                    }
                });
            }

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

        void setItem(RidePointWithDatas item){
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

        protected RidePointWithDatas mItem;

        protected OnClickItemListener<RidePointWithDatas>  mListener;

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        protected abstract void clear();

        void setListener(OnClickItemListener<RidePointWithDatas> listener){
            mListener = listener;
        }

        void setItem(RidePointWithDatas item){
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

