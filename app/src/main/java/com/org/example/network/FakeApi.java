package com.org.example.network;

import com.org.example.model.FakeThing;
import com.org.example.model.FakeToken;

import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class FakeApi {
    private Random random = new Random();

    public Observable<FakeToken> getFakeToken(final String fakeAuth) {
        return Observable.just(fakeAuth).map(new Function<String, FakeToken>() {
            @Override
            public FakeToken apply(String fakeAuth) throws Exception {
                int fakeNetWorkTimeCost = random.nextInt(500) + 500;
                try {
                    Thread.sleep(fakeNetWorkTimeCost);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                FakeToken fakeToken = new FakeToken();
                fakeToken.Token = createToken();
                return fakeToken;
            }
        });
    }

    private String createToken() {
        return "fake_token_" + System.currentTimeMillis() % 10000;
    }

    public Observable<FakeThing> getFakeData(FakeToken token) {
        return Observable.just(token)
                .map(new Function<FakeToken, FakeThing>() {
                    @Override
                    public FakeThing apply(FakeToken token) throws Exception {
                        int fakeNetWorkTimeCost = random.nextInt(500) + 500;
                        try {
                            Thread.sleep(fakeNetWorkTimeCost);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (token.expired) {
                            throw new IllegalArgumentException("Token expired!");
                        }
                        FakeThing fakeThing = new FakeThing();
                        fakeThing.id = (int) (System.currentTimeMillis() % 1000);
                        fakeThing.name = "FAKE_USER_" + fakeThing.id;
                        return fakeThing;
                    }
                });
    }

}
