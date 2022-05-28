package com.fulop.novel_v2.listeners;

import com.fulop.novel_v2.models.Novel;

public interface NovelListener {
    void onLayoutClick(Novel novel);
    void onLike(Novel novel);
    void onShare(Novel novel);
}
