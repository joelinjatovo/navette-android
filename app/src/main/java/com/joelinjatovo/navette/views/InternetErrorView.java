package com.joelinjatovo.navette.views;

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

import com.joelinjatovo.navette.R;

public class InternetErrorView extends LinearLayout {

    private Button button;

    public InternetErrorView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public InternetErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public InternetErrorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Inflate view
        LayoutInflater  mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (mInflater == null) {
            return;
        }

        View view = mInflater.inflate(R.layout.view_error_internet, this, true);

        button = view.findViewById(R.id.button);

    }

    public Button getButton() {
        return button;
    }
}
