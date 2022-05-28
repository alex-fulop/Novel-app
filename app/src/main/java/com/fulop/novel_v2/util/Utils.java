package com.fulop.novel_v2.util;

import android.content.Context;
import android.widget.ImageView;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fulop.novel_v2.R;

import java.text.DateFormat;
import java.util.Date;

public class Utils {
    public static void loadUrl(String url, ImageView view, int errorDrawable) {
        if (view.getContext() != null) {
            RequestOptions options = RequestOptions
                    .placeholderOf(progressDrawable(view.getContext()))
                    .error(errorDrawable);

            Glide.with(view.getContext().getApplicationContext())
                    .load(url)
                    .apply(options)
                    .into(view);
        }
    }

    public static void loadUrl(String url, ImageView view) {
        int errorDrawable = R.drawable.novel_logo;
        if (view.getContext() != null) {
            RequestOptions options = RequestOptions
                    .placeholderOf(progressDrawable(view.getContext()))
                    .error(errorDrawable);

            Glide.with(view.getContext().getApplicationContext())
                    .load(url)
                    .apply(options)
                    .into(view);
        }
    }

    public static String getDate(Long date) {
        if (date != null) {
            DateFormat dateInstance = DateFormat.getDateInstance();
            return dateInstance.format(new Date(date));
        }
        return "unknown";
    }

    private static CircularProgressDrawable progressDrawable(Context context) {
        CircularProgressDrawable drawable = new CircularProgressDrawable(context);
        drawable.setStrokeWidth(5f);
        drawable.setCenterRadius(30f);
        drawable.start();
        return drawable;
    }
}
