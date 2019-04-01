package com.org.example;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;

import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

public abstract class BaseFragment extends Fragment {

    @OnClick(R.id.btn_tip)
    void tip() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getTileRes())
                .setView(getActivity().getLayoutInflater().inflate(getDialogRes(), null))
                .show();
    }

    protected abstract int getDialogRes();

    protected abstract int getTileRes();


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
