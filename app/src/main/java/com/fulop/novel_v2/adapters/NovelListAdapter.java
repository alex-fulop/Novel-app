package com.fulop.novel_v2.adapters;

import static com.fulop.novel_v2.Utils.*;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fulop.novel_v2.Listeners.NovelListener;
import com.fulop.novel_v2.R;
import com.fulop.novel_v2.Utils;
import com.fulop.novel_v2.models.Novel;

import java.util.List;
import java.util.Locale;

public class NovelListAdapter extends RecyclerView.Adapter<NovelListAdapter.NovelViewHolder> {
    private final String userId;
    private final List<Novel> novels;
    private NovelListener listener;

    public NovelListAdapter(String userId, List<Novel> novels) {
        this.userId = userId;
        this.novels = novels;
    }

    public void setListener(NovelListener listener) {
        this.listener = listener;
    }

    public void updateNovels(List<Novel> newNovels) {
        novels.clear();
        novels.addAll(newNovels);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NovelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NovelViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_novel, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NovelViewHolder holder, int position) {
        holder.onBind(userId, novels.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return novels.size();
    }

    static class NovelViewHolder extends RecyclerView.ViewHolder {

        private final ViewGroup layout = itemView.findViewById(R.id.novelLayout);
        private final TextView username = itemView.findViewById(R.id.novelUsername);
        private final TextView text = itemView.findViewById(R.id.novelText);
        private final ImageView image = itemView.findViewById(R.id.novelImage);
        private final TextView date = itemView.findViewById(R.id.novelDate);
        private final ImageView like = itemView.findViewById(R.id.novelLike);
        private final TextView likeCount = itemView.findViewById(R.id.novelLikeCount);
        private final ImageView share = itemView.findViewById(R.id.novelShare);
        private final TextView shareCount = itemView.findViewById(R.id.novelShareCount);

        public NovelViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void onBind(String userId, Novel novel, NovelListener listener) {
            username.setText(novel.getUsername());
            text.setText(novel.getText());
            if (novel.getImageUrl() == null || novel.getImageUrl().isEmpty())
                image.setVisibility(View.GONE);
            else {
                image.setVisibility(View.VISIBLE);
                loadUrl(novel.getImageUrl(), image);
            }
            date.setText(getDate(novel.getTimestamp()));
            likeCount.setText(String.format(Locale.getDefault(), "%d", novel.getLikes().size()));
            shareCount.setText(String.format(Locale.getDefault(), "%d", novel.getUserIds().size() - 1));

            layout.setOnClickListener(unused -> listener.onLayoutClick(novel));
            like.setOnClickListener(unused -> listener.onLike(novel));
            share.setOnClickListener(unused -> listener.onShare(novel));

            if (novel.getLikes().contains(userId))
                like.setImageDrawable(ContextCompat.getDrawable(like.getContext(), R.drawable.heart_active));
            else
                like.setImageDrawable(ContextCompat.getDrawable(like.getContext(), R.drawable.heart));

            if (novel.getUserIds().get(0).equals(userId)) {
                share.setImageDrawable(ContextCompat.getDrawable(like.getContext(), R.drawable.share_own));
                share.setClickable(false);
            } else if (novel.getUserIds().contains(userId))
                share.setImageDrawable(ContextCompat.getDrawable(like.getContext(), R.drawable.share_active));
            else
                share.setImageDrawable(ContextCompat.getDrawable(like.getContext(), R.drawable.share));
        }
    }
}
