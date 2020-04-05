package com.navetteclub.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.navetteclub.R;

public class LoadingView extends LinearLayout {
    private String mTitle;

    private TextView titleView;

    public LoadingView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Inflate view
        LayoutInflater  mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (mInflater == null) {

            return;
        }

        View view = mInflater.inflate(R.layout.view_loading, this, true);

        titleView = view.findViewById(R.id.title);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomView, defStyle, 0);

        mTitle = a.getString(R.styleable.CustomView_mTitle);
        if (mTitle != null) {
            titleView.setText(mTitle);
        }
        a.recycle();
    }

    public TextView getTitleView() {
        return titleView;
    }

}
