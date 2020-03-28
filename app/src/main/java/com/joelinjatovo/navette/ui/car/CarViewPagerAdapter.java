package com.joelinjatovo.navette.ui.car;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.database.entity.CarAndModel;
import com.joelinjatovo.navette.ui.order.CarRecyclerViewAdapter;
import com.joelinjatovo.navette.ui.order.OrderFragment;
import com.joelinjatovo.navette.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CarViewPagerAdapter extends RecyclerView.Adapter<CarViewPagerAdapter.ViewHolder> {

    private List<CarAndModel> mItems;

    private final OrderFragment.OnListFragmentInteractionListener mListener;

    public CarViewPagerAdapter(OrderFragment.OnListFragmentInteractionListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public CarViewPagerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewpager_car, parent, false);
        return new CarViewPagerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CarViewPagerAdapter.ViewHolder holder, int position) {
        holder.mItem = mItems.get(position);
        holder.mNameTextView.setText(mItems.get(position).getCar().getName());
        new Picasso.Builder(holder.mCarImageView.getContext())
                .build()
                .load(Constants.BASE_URL + mItems.get(position).getCar().getImageUrl())
                .resize(360,180).into(holder.mCarImageView);

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onListFragmentInteraction(v, position, holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems==null?0:mItems.size();
    }

    public void setItems(List<CarAndModel> items){
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
                    CarAndModel oldItem = mItems.get(oldItemPosition);
                    CarAndModel newItem = mItems.get(newItemPosition);
                    return oldItem.getCar().getId() == newItem.getCar().getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    CarAndModel oldItem = mItems.get(oldItemPosition);
                    CarAndModel newItem = mItems.get(newItemPosition);
                    return oldItem.getCar().getName()!=null && oldItem.getCar().getName().equals(newItem.getCar().getName());
                }
            });

            mItems = items;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView mCarImageView;
        final TextView mNameTextView;
        CarAndModel mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mCarImageView = view.findViewById(R.id.carImageView);
            mNameTextView = view.findViewById(R.id.nameTextView);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mNameTextView.getText() + "'";
        }
    }
}