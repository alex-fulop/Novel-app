package com.fulop.novel_v2.api;

import static com.fulop.novel_v2.util.Constants.ACCESS_TOKEN;
import static com.fulop.novel_v2.util.Constants.ACCESS_TOKEN_SECRET;
import static com.fulop.novel_v2.util.Constants.CONSUMER_KEY;
import static com.fulop.novel_v2.util.Constants.CONSUMER_SECRET;

import io.github.redouane59.twitter.TwitterClient;
import io.github.redouane59.twitter.dto.endpoints.AdditionalParameters;
import io.github.redouane59.twitter.dto.tweet.TweetList;
import io.github.redouane59.twitter.signature.TwitterCredentials;

public class TweetsAsyncApiCall implements Runnable {
    private final String term;
    private TweetList result;

    public TweetsAsyncApiCall(String term) {
        this.term = term;
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
            result = twitterClient.searchTweets(term + " lang:en", AdditionalParameters.builder()
                    .recursiveCall(false)
                    .maxResults(10)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TweetList getResult() {
        return result;
    }
}
