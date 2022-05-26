package com.fulop.novel_v2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.squareup.picasso.Picasso;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    List<String> urlList;

    public MyAdapter(Context context, List<String> urlList) {
        this.context = context;
        this.urlList = urlList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_preview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//Use thread to fetch
        Utils.getJsoupContent(urlList.get(position))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result != null) {
                        //Get element tag
                        Elements metaTags = result.getElementsByTag("meta");
                        for (Element element : metaTags) {
                            //Get data from url
                            if (element.attr("property").equals("og:image"))
                                Picasso.get().load(element.attr("content")).into(holder.img_preview);
                            else if (element.attr("name").equals("title"))
                                holder.txt_title.setText(element.attr("content"));
                            else if (element.attr("name").equals("description"))
                                holder.txt_title.setText(element.attr("content"));
                            else if (element.attr("property").equals("og:url")) {
                                holder.cardView.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        //Navigate web browser
                                        String browserUrl = element.attr("content");
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(browserUrl));
                                        context.startActivity(i);
                                    }
                                });
                            }

                        }
                    } else {
                        Toast.makeText(context, "Result is null", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return urlList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private Unbinder unbinder;

        @BindView(R.id.img_preview)
        ImageView img_preview;
        @BindView(R.id.txt_title)
        TextView txt_title;
        @BindView(R.id.txt_description)
        TextView txt_description;
        @BindView(R.id.layout_preview)
        LinearLayout layout_preview;
        @BindView(R.id.progress_bar)
        CircularProgressIndicator progressBar;
        @BindView(R.id.card_view)
        CardView cardView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }
    }
}
