package com.fulop.novel_v2.api;

import static com.fulop.novel_v2.util.Constants.ACCESS_TOKEN;
import static com.fulop.novel_v2.util.Constants.ACCESS_TOKEN_SECRET;
import static com.fulop.novel_v2.util.Constants.CONSUMER_KEY;
import static com.fulop.novel_v2.util.Constants.CONSUMER_SECRET;

import io.github.redouane59.twitter.TwitterClient;
import io.github.redouane59.twitter.dto.user.User;
import io.github.redouane59.twitter.signature.TwitterCredentials;

public class UsersAsyncApiCall implements Runnable {
    private final String userId;
    private User user;

    public UsersAsyncApiCall(String userId) {
        this.userId = userId;
    }

    @Override
    public void run() {
        TwitterClient twitterClient = new TwitterClient(TwitterCredentials.builder()
                .accessToken(ACCESS_TOKEN)
                .accessTokenSecret(ACCESS_TOKEN_SECRET)
                .apiKey(CONSUMER_KEY)
                .apiSecretKey(CONSUMER_SECRET)
                .build());

        try {
            user = twitterClient.getUserFromUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User getUser() {
        return user;
    }
}
