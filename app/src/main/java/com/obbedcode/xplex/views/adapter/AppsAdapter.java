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
    private static final String TAG = "ObbedCode.XP.AppsAdapter";

    private final RequestOptions requestOptions;
    private final OnItemClickListener onItemClickListener;

    public AppsAdapter(Context context, OnItemClickListener onItemClickListener) {
        super(DIFF_CALLBACK);
        Log.i(TAG, "[init] ....");
        this.onItemClickListener = onItemClickListener;
        this.requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(context.getResources().getDimensionPixelSize(com.obbedcode.xplex.R.dimen.app_icon_size));
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AppItemBinding binding =  AppItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AppViewHolder(binding, onItemClickListener, requestOptions);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public void onViewRecycled(@NonNull AppViewHolder holder) {
        super.onViewRecycled(holder);
        holder.clearImage();
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {

        private final AppItemBinding binding;
        private final OnItemClickListener onItemClickListener;
        private final RequestOptions requestOptions;
        private XApp appInfo;

        AppViewHolder(AppItemBinding binding, OnItemClickListener onItemClickListener, RequestOptions requestOptions) {
            super(binding.getRoot());
            this.binding = binding;
            this.onItemClickListener = onItemClickListener;
            this.requestOptions = requestOptions;

            binding.getRoot().setOnClickListener(v -> onItemClickListener.onItemClick(appInfo));
            binding.getRoot().setOnLongClickListener(v -> {
                onItemClickListener.onItemLongClick(appInfo);
                return true;
            });
        }

        void bind(XApp appInfo) {
            this.appInfo = appInfo;
            //binding.tvHookAppName.setText(appInfo.appName);
            //binding.tvHookAppPackageName.setText(appInfo.packageName);

            //rookie mistake kek int res id not string kek
            //binding.tvHookAppUid.setText(String.valueOf(appInfo.uid));

            //binding.appName.setText(appInfo.getAppName());
            //binding.packageName.setText(appInfo.getPackageName());
            //binding.appVersion.setText(appInfo.getVersionName() + " (" + appInfo.getVersionCode() + ")");
            //Glide.with(binding.ivHookAppIcon.getContext())
            //        .load(appInfo.icon)
            //        .apply(requestOptions)
            //        .into(binding.ivHookAppIcon);

            binding.packageName.setText(appInfo.packageName);
            binding.appName.setText(appInfo.appName);
            binding.appVersion.setText(String.valueOf(appInfo.uid));
            //Glide.with(binding.appIcon.getContext())
            //        .load(appInfo.icon)
            //        .apply(requestOptions)
            //        .into(binding.appIcon);
            if(appInfo.icon < 1) {
                //Default ICON
                binding.appIcon.setImageResource(android.R.drawable.sym_def_app_icon);
            } else {
                Uri uri = Uri.parse("android.resource://" + appInfo.packageName + "/" + appInfo.icon);
                Glide.with(binding.appIcon.getContext())
                        .load(uri)
                        .apply(requestOptions)
                        .into(binding.appIcon);

                //Glide.with(context)
                //        .applyDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_RGB_565))
                //        .load(uri)
                //        .diskCacheStrategy(DiskCacheStrategy.NONE)
                //        .skipMemoryCache(true)
                //        .override(iconSize, iconSize)
                //        .into(ivAppIcon);
            }
        }

        void clearImage() {
            Glide.with(binding.appIcon.getContext()).clear(binding.appIcon);
            //Glide.with(binding.ivHookAppIcon.getContext()).clear(binding.ivHookAppIcon);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(XApp app);
        void onItemLongClick(XApp app);
    }

    private static final DiffUtil.ItemCallback<XApp> DIFF_CALLBACK = new DiffUtil.ItemCallback<XApp>() {
        @Override
        public boolean areItemsTheSame(@NonNull XApp oldItem, @NonNull XApp newItem) {
            return oldItem.packageName.equals(newItem.packageName);
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull XApp oldItem, @NonNull XApp newItem) {
            return oldItem.equals(newItem);
        }
    };
}
