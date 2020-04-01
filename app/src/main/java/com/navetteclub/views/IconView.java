package com.joelinjatovo.navette.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joelinjatovo.navette.R;

public class IconView extends LinearLayout {
    private String mTitle;
    private String mSubtitle;
    private Drawable mIcon;

    public IconView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public IconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Inflate view
        LayoutInflater  mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (mInflater == null) {
            return;
        }

        View view = mInflater.inflate(R.layout.view_icon, this, true);

        ImageView imageView = view.findViewById(R.id.icon);
        TextView titleView = view.findViewById(R.id.title);
        TextView subtitleView = view.findViewById(R.id.subtitle);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomView, defStyle, 0);

        if (a.hasValue(R.styleable.CustomView_mIcon)) {
            mIcon = a.getDrawable(R.styleable.CustomView_mIcon);
            if (mIcon != null) {
                imageView.setImageDrawable(mIcon);
                mIcon.setCallback(this);
            }
        }

        mTitle = a.getString(R.styleable.CustomView_mTitle);
        if (mTitle != null) {
            titleView.setText(mTitle);
        }

        mSubtitle = a.getString(R.styleable.CustomView_mSubtitle);
        if (mSubtitle != null) {
            subtitleView.setText(mSubtitle);
        }

        a.recycle();

    }
}
