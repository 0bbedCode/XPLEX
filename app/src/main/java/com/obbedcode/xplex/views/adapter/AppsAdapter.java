package com.obbedcode.xplex.views.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.obbedcode.shared.data.XApp;
import com.obbedcode.xplex.R;
import com.obbedcode.xplex.databinding.AppItemBinding;
import com.bumptech.glide.request.RequestOptions;


public class AppsAdapter extends ListAdapter<XApp, AppsAdapter.AppViewHolder> {
    private final RequestOptions requestOptions;
    private final OnItemClickListener onItemClickListener;

    public AppsAdapter(Context context, OnItemClickListener onItemClickListener) {
        super(DIFF_CALLBACK);
        this.onItemClickListener = onItemClickListener;
        this.requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).override(context.getResources().getDimensionPixelSize(com.obbedcode.xplex.R.dimen.app_icon_size));
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AppItemBinding binding =  AppItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AppViewHolder(binding, onItemClickListener, requestOptions);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) { holder.bind(getItem(position)); }

    @Override
    public void onViewRecycled(@NonNull AppViewHolder holder) {
        super.onViewRecycled(holder);
        holder.clearImage();
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        private final AppItemBinding binding;
        private final RequestOptions requestOptions;
        private XApp appInfo;
        AppViewHolder(AppItemBinding binding, OnItemClickListener onItemClickListener, RequestOptions requestOptions) {
            super(binding.getRoot());
            this.binding = binding;
            this.requestOptions = requestOptions;
            binding.getRoot().setOnClickListener(v -> onItemClickListener.onItemClick(appInfo));
            binding.getRoot().setOnLongClickListener(v -> {
                onItemClickListener.onItemLongClick(appInfo);
                return true;
            });
        }

        void bind(XApp appInfo) {
            this.appInfo = appInfo;
            binding.packageName.setText(appInfo.packageName);
            binding.appName.setText(appInfo.appName);
            binding.appVersion.setText(String.valueOf(appInfo.uid));
            if(appInfo.icon < 1) {
                binding.appIcon.setImageResource(android.R.drawable.sym_def_app_icon);
            } else {
                Uri uri = Uri.parse("android.resource://" + appInfo.packageName + "/" + appInfo.icon);
                Glide.with(binding.appIcon.getContext())
                        .load(uri)
                        .apply(requestOptions)
                        .into(binding.appIcon);
            }
        }

        void clearImage() { Glide.with(binding.appIcon.getContext()).clear(binding.appIcon); }
    }

    public interface OnItemClickListener {
        void onItemClick(XApp app);
        void onItemLongClick(XApp app);
    }

    private static final DiffUtil.ItemCallback<XApp> DIFF_CALLBACK = new DiffUtil.ItemCallback<XApp>() {
        @Override
        public boolean areItemsTheSame(@NonNull XApp oldItem, @NonNull XApp newItem) { return oldItem.packageName.equals(newItem.packageName);  }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull XApp oldItem, @NonNull XApp newItem) { return oldItem.equals(newItem); }
    };
}
