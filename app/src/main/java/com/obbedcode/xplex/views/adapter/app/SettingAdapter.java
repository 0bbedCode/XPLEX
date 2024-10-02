package com.obbedcode.xplex.views.adapter.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.obbedcode.shared.xplex.data.XSetting;
import com.obbedcode.xplex.databinding.AppSettingItemBinding;

public class SettingAdapter extends ListAdapter<XSetting, SettingAdapter.SettingLogGroupViewHolder> {
    private final RequestOptions requestOptions;
    private final SettingAdapter.OnItemClickListener onItemClickListener;

    public SettingAdapter(Context context, SettingAdapter.OnItemClickListener onItemClickListener) {
        super(DIFF_CALLBACK);
        this.onItemClickListener = onItemClickListener;
        //Lower this shit icon does not need to be this big not app list
        //Also how will we resolve icons ?
        this.requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).override(context.getResources().getDimensionPixelSize(com.obbedcode.xplex.R.dimen.app_icon_size));
    }

    @NonNull
    @Override
    public SettingLogGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AppSettingItemBinding binding =  AppSettingItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SettingLogGroupViewHolder(binding, onItemClickListener, requestOptions);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingLogGroupViewHolder holder, int position) { holder.bind(getItem(position)); }

    @Override
    public void onViewRecycled(@NonNull SettingLogGroupViewHolder holder) { super.onViewRecycled(holder); holder.clearImage(); }

    static class SettingLogGroupViewHolder extends RecyclerView.ViewHolder {
        private final AppSettingItemBinding binding;
        private final RequestOptions requestOptions;
        private XSetting setting;

        SettingLogGroupViewHolder(AppSettingItemBinding binding, SettingAdapter.OnItemClickListener onItemClickListener, RequestOptions requestOptions) {
            super(binding.getRoot());
            this.binding = binding;
            this.requestOptions = requestOptions;
            binding.getRoot().setOnClickListener(v -> onItemClickListener.onItemClick(setting));
            binding.getRoot().setOnLongClickListener(v -> { onItemClickListener.onItemLongClick(setting); return true; });
        }

        void bind(XSetting setting) {
            this.setting = setting;
            binding.settingName.setText(setting.name);
            binding.settingExtra.setText(setting.value);
        }

        void clearImage() { /*Glide.with(binding.logIcon.getContext()).clear(binding.logIcon);*/ }
    }

    public interface OnItemClickListener {
        void onItemClick(XSetting setting);
        void onItemLongClick(XSetting setting);
    }

    private static final DiffUtil.ItemCallback<XSetting> DIFF_CALLBACK = new DiffUtil.ItemCallback<XSetting>() {
        @Override
        public boolean areItemsTheSame(@NonNull XSetting oldItem, @NonNull XSetting newItem) { return oldItem.name.equals(newItem.name);  }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull XSetting oldItem, @NonNull XSetting newItem) { return oldItem.equals(newItem); }
    };
}
