package com.org.example.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.org.example.R;
import com.org.example.model.ElementaryBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ElementaryAdapter extends RecyclerView.Adapter {
    private List<ElementaryBean> mDatas;

    public ElementaryAdapter(List<ElementaryBean> mDatas) {
        this.mDatas = mDatas;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.elementary_item, viewGroup, false);
        return new ElementaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ElementaryViewHolder holder = (ElementaryViewHolder) viewHolder;
        ElementaryBean bean = mDatas.get(position);
        holder.mTvTile.setText(bean.description);
        Glide.with(holder.itemView.getContext())
                .load(bean.image_url)
                .into(holder.mImg);


    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public class ElementaryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView mTvTile;
        @BindView(R.id.img)
        ImageView mImg;

        public ElementaryViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
