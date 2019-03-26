package com.org.example;

import android.support.v4.app.Fragment;

import io.reactivex.disposables.Disposable;

public abstract class BaseFragment extends Fragment {

    protected Disposable disposable;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unsubscribe();
    }

    protected void unsubscribe() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
