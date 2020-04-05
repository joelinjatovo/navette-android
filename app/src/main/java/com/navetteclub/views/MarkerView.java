package com.navetteclub.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.navetteclub.R;
import com.navetteclub.utils.Log;

public class MarkerView extends RelativeLayout {

    private ImageView imageView;
    private TextView titleView;

    public MarkerView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public MarkerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public MarkerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Inflate view
        LayoutInflater  mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (mInflater == null) {
            return;
        }

        View view = mInflater.inflate(R.layout.view_marker, this, true);

        imageView = view.findViewById(R.id.icon);
        titleView = view.findViewById(R.id.title);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MenuItemView, 0, 0);

        if (a.hasValue(R.styleable.MenuItemView_iconImage)) {
            Drawable mIcon = a.getDrawable(R.styleable.MenuItemView_iconImage);
            if (mIcon != null) {
                imageView.setImageDrawable(mIcon);
                mIcon.setCallback(this);
            }
        }

        String mTitle = a.getString(R.styleable.MenuItemView_titleText);
        if (mTitle != null) {
            titleView.setText(mTitle);
        }

        a.recycle();

    }

    public ImageView getImageView(){
        return imageView;
    }

    public TextView getTitleView(){
        return titleView;
    }
}
