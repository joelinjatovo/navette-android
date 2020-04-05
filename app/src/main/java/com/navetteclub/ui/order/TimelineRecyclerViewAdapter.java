package com.navetteclub.ui.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.navetteclub.R;
import com.navetteclub.models.Timeline;

import java.util.List;

public class TimelineRecyclerViewAdapter
        extends RecyclerView.Adapter<TimelineRecyclerViewAdapter.ViewHolder> {

    private List<Timeline> mItems;

    private final TimelineFragment.OnListFragmentInteractionListener mListener;

    public TimelineRecyclerViewAdapter(TimelineFragment.OnListFragmentInteractionListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_club, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mItems.get(position);

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onListFragmentInteraction(v, holder.mItem);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mItems==null?0:mItems.size();
    }

    public void setItems(List<Timeline> items){
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
                    Timeline oldItem = mItems.get(oldItemPosition);
                    Timeline newItem = items.get(newItemPosition);
                    return oldItem.getTime() == newItem.getTime();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Timeline oldItem = mItems.get(oldItemPosition);
                    Timeline newItem = items.get(newItemPosition);
                    return oldItem.getTime()!=null && oldItem.getTime().equals(newItem.getTime());
                }
            });

            mItems = items;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView mClubImageView;
        final TextView mNameTextView;
        Timeline mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mClubImageView = view.findViewById(R.id.clubImageView);
            mNameTextView = view.findViewById(R.id.clubNameTextView);
        }

        void setmItem(Timeline timeline){
            this.mItem = timeline;
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mNameTextView.getText() + "'";
        }
    }
}

