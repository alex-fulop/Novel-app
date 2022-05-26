package com.fulop.novel_v2;

import android.content.Context;
import android.widget.ImageView;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import io.reactivex.rxjava3.core.Observable;

public class Utils {
    public static Observable<Document> getJsoupContent(String url) {
        return Observable.fromCallable(() -> {
            try {
                Document document = Jsoup.connect(url).timeout(0).get();
                return document;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

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
