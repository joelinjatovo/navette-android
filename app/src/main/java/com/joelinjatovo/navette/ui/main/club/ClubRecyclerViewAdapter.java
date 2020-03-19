package com.joelinjatovo.navette.ui.main.club;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.database.entity.Club;
import com.joelinjatovo.navette.ui.main.club.ClubsFragment.OnListFragmentInteractionListener;

import java.util.List;

public class ClubRecyclerViewAdapter extends RecyclerView.Adapter<ClubRecyclerViewAdapter.ViewHolder> {

    private List<Club> mItems;

    private final OnListFragmentInteractionListener mListener;

    public ClubRecyclerViewAdapter(OnListFragmentInteractionListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_clubs, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mItems.get(position);
        holder.mIdView.setText(mItems.get(position).getName());
        holder.mContentView.setText(mItems.get(position).getCreatedAt().toString());

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onListFragmentInteraction(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setItems(List<Club> items){
        mItems = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mIdView;
        final TextView mContentView;
        Club mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mContentView = view.findViewById(R.id.content);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}

