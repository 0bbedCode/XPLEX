package com.obbedcode.xplex.views.adapter.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.obbedcode.shared.xplex.data.hook.XHookGroup;
import com.obbedcode.xplex.databinding.AppHookItemBinding;
import com.obbedcode.xplex.views.etc.UiUtil;


public class HookGroupAdapter extends ListAdapter<XHookGroup, HookGroupAdapter.AppHookGroupViewHolder> {
    private final RequestOptions requestOptions;
    private final HookGroupAdapter.OnItemClickListener onItemClickListener;

    public HookGroupAdapter(Context context, HookGroupAdapter.OnItemClickListener onItemClickListener) {
        super(DIFF_CALLBACK);
        this.onItemClickListener = onItemClickListener;
        //Lower this shit icon does not need to be this big not app list
        //Also how will we resolve icons ?
        this.requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).override(UiUtil.dpToPx(context, 32));
    }

    @NonNull
    @Override
    public AppHookGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AppHookItemBinding binding =  AppHookItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AppHookGroupViewHolder(binding, onItemClickListener, requestOptions);
    }

    @Override
    public void onBindViewHolder(@NonNull AppHookGroupViewHolder holder, int position) { holder.bind(getItem(position)); }

    @Override
    public void onViewRecycled(@NonNull AppHookGroupViewHolder holder) { super.onViewRecycled(holder); holder.clearImage(); }

    static class AppHookGroupViewHolder extends RecyclerView.ViewHolder {
        private final AppHookItemBinding binding;
        private final RequestOptions requestOptions;
        private XHookGroup group;

        AppHookGroupViewHolder(AppHookItemBinding binding, HookGroupAdapter.OnItemClickListener onItemClickListener, RequestOptions requestOptions) {
            super(binding.getRoot());
            this.binding = binding;
            this.requestOptions = requestOptions;
            binding.getRoot().setOnClickListener(v -> onItemClickListener.onItemClick(group));
            binding.getRoot().setOnLongClickListener(v -> { onItemClickListener.onItemLongClick(group); return true; });
        }

        void bind(XHookGroup group) {
            this.group = group;
            binding.groupName.setText(group.name);
            binding.groupDescription.setText(group.description);
        }

        void clearImage() { Glide.with(binding.groupIcon.getContext()).clear(binding.groupIcon); }
    }

    public interface OnItemClickListener {
        void onItemClick(XHookGroup group);
        void onItemLongClick(XHookGroup group);
    }

    private static final DiffUtil.ItemCallback<XHookGroup> DIFF_CALLBACK = new DiffUtil.ItemCallback<XHookGroup>() {
        @Override
        public boolean areItemsTheSame(@NonNull XHookGroup oldItem, @NonNull XHookGroup newItem) { return oldItem.name.equals(newItem.name);  }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull XHookGroup oldItem, @NonNull XHookGroup newItem) { return oldItem.equals(newItem); }
    };
}
