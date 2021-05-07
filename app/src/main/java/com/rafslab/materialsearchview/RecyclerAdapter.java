package com.rafslab.materialsearchview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.rafslab.materialsearchview.models.Data;
import com.rafslab.materialsearchview.spring.RecyclerView.SpringyAdapterAnimationType;
import com.rafslab.materialsearchview.spring.RecyclerView.SpringyAdapterAnimator;
import com.rafslab.materialsearchview.spring.SpringAnimationType;
import com.rafslab.materialsearchview.spring.SpringyAnimator;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private List<Data> dataList;
    private final LayoutInflater inflater;
    private boolean playAnimation = false;
    private final SpringyAdapterAnimator mAnimator;
    private final RequestOptions options;

    public RecyclerAdapter(Context mContext, List<Data> dataList, RecyclerView recyclerView) {
        mAnimator = new SpringyAdapterAnimator(recyclerView);
        mAnimator.setSpringAnimationType(SpringyAdapterAnimationType.SCALE);
        mAnimator.addConfig(85,15);
        this.mContext = mContext;
        this.dataList = dataList;
        this.options = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher);
        inflater = LayoutInflater.from(mContext);
    }
    public void setPlayAnimation(boolean playAnimation){
        this.playAnimation = playAnimation;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        if (playAnimation) {
            mAnimator.onSpringItemCreate(view);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Data data = dataList.get(position);
        holder.title.setText(data.getTitle());
        holder.desc.setText(data.getDescription());
        Glide.with(mContext).load(data.getProfile()).apply(options).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.image);
        if (playAnimation) {
            mAnimator.onSpringItemBind(holder.itemView, position);
        }
        holder.itemView.setOnClickListener(v-> {
            Toast.makeText(mContext, data.getTitle() + " clicked", Toast.LENGTH_SHORT).show();
            animateView(holder.itemView);
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView desc;
        private final ImageView image;
        private final ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.price);
            image = itemView.findViewById(R.id.image);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
    public void setFilter(List<Data> models){
        dataList = new ArrayList<>();
        dataList.addAll(models);
        notifyDataSetChanged();
    }
    private void animateView(View view){
        SpringyAnimator springHelper = new SpringyAnimator(SpringAnimationType.SCALEXY,100,4,0,1);
        springHelper.startSpring(view);
    }
}
