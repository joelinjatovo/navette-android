package com.navetteclub.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.navetteclub.R;

public class MenuItemView extends RelativeLayout {

    public MenuItemView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public MenuItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public MenuItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Inflate view
        LayoutInflater  mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (mInflater == null) {
            return;
        }

        View view = mInflater.inflate(R.layout.view_menu_item, this, true);

        ImageView imageView = view.findViewById(R.id.icon);
        ImageView endIconImageView = view.findViewById(R.id.endIcon);
        TextView titleView = view.findViewById(R.id.title);
        TextView subtitleView = view.findViewById(R.id.subtitle);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MenuItemView, 0, 0);

        if (a.hasValue(R.styleable.MenuItemView_iconImage)) {
            Drawable mIcon = a.getDrawable(R.styleable.MenuItemView_iconImage);
            if (mIcon != null) {
                imageView.setImageDrawable(mIcon);
                mIcon.setCallback(this);
            }
        }

        boolean endIconEnable = a.getBoolean(R.styleable.MenuItemView_endIconEnable, true);
        if (!endIconEnable) {
            endIconImageView.setVisibility(GONE);
        }

        String mTitle = a.getString(R.styleable.MenuItemView_titleText);
        if (mTitle != null) {
            titleView.setText(mTitle);
        }

        String mSubtitle = a.getString(R.styleable.MenuItemView_subtitleText);
        if (mSubtitle != null) {
            subtitleView.setText(mSubtitle);
        }else{
            subtitleView.setVisibility(GONE);
        }

        boolean enableSubtitle = a.getBoolean(R.styleable.MenuItemView_subtitleEnable, true);
        if (!enableSubtitle) {
            subtitleView.setVisibility(GONE);
        }

        a.recycle();

    }
}
