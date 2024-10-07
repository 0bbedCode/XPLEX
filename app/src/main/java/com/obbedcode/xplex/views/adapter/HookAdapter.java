package com.obbedcode.xplex.views.adapter;

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
import com.obbedcode.shared.xplex.data.hook.XHookContainer;
import com.obbedcode.xplex.databinding.AppHookOptionBinding;
import com.obbedcode.xplex.views.etc.UiUtil;

public class HookAdapter  extends ListAdapter<XHookContainer, HookAdapter.HookViewHolder> {
    private final RequestOptions requestOptions;
    private final HookAdapter.OnItemClickListener onItemClickListener;

    public HookAdapter(Context context, HookAdapter.OnItemClickListener onItemClickListener) {
        super(DIFF_CALLBACK);
        this.onItemClickListener = onItemClickListener;
        //Lower this shit icon does not need to be this big not app list
        //Also how will we resolve icons ?
        this.requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).override(UiUtil.dpToPx(context, 32));
    }

    @NonNull
    @Override
    public HookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AppHookOptionBinding binding =  AppHookOptionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new HookViewHolder(binding, onItemClickListener, requestOptions);
    }

    @Override
    public void onBindViewHolder(@NonNull HookViewHolder holder, int position) { holder.bind(getItem(position)); }

    @Override
    public void onViewRecycled(@NonNull HookViewHolder holder) { super.onViewRecycled(holder); holder.clearImage(); }

    static class HookViewHolder extends RecyclerView.ViewHolder {
        private final AppHookOptionBinding binding;
        private final RequestOptions requestOptions;

        private XHookContainer definition;

        HookViewHolder(AppHookOptionBinding binding, HookAdapter.OnItemClickListener onItemClickListener, RequestOptions requestOptions) {
            super(binding.getRoot());
            this.binding = binding;
            this.requestOptions = requestOptions;
            binding.getRoot().setOnClickListener(v -> onItemClickListener.onItemClick(definition));
            binding.getRoot().setOnLongClickListener(v -> { onItemClickListener.onItemLongClick(definition); return true; });
        }

        void bind(XHookContainer definition) {
            this.definition = definition;
            binding.hookOptionName.setText(definition.description);
        }

        void clearImage() { /*Glide.with(binding.groupIcon.getContext()).clear(binding.groupIcon);*/ }
    }

    public interface OnItemClickListener {
        void onItemClick(XHookContainer definition);
        void onItemLongClick(XHookContainer definition);
    }

    private static final DiffUtil.ItemCallback<XHookContainer> DIFF_CALLBACK = new DiffUtil.ItemCallback<XHookContainer>() {
        @Override
        public boolean areItemsTheSame(@NonNull XHookContainer oldItem, @NonNull XHookContainer newItem) { return oldItem.description.equals(newItem.description);  }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull XHookContainer oldItem, @NonNull XHookContainer newItem) { return oldItem.equals(newItem); }
    };
}
