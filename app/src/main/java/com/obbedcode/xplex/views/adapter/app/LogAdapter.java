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
import com.obbedcode.shared.xplex.data.XLogLog;
import com.obbedcode.xplex.databinding.AppLogItemBinding;

public class LogAdapter extends ListAdapter<XLogLog, LogAdapter.AppLogGroupViewHolder> {
    private final RequestOptions requestOptions;
    private final LogAdapter.OnItemClickListener onItemClickListener;

    public LogAdapter(Context context, LogAdapter.OnItemClickListener onItemClickListener) {
        super(DIFF_CALLBACK);
        this.onItemClickListener = onItemClickListener;
        //Lower this shit icon does not need to be this big not app list
        //Also how will we resolve icons ?
        this.requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).override(context.getResources().getDimensionPixelSize(com.obbedcode.xplex.R.dimen.app_icon_size));
    }

    @NonNull
    @Override
    public  AppLogGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AppLogItemBinding binding =  AppLogItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AppLogGroupViewHolder(binding, onItemClickListener, requestOptions);
    }

    @Override
    public void onBindViewHolder(@NonNull AppLogGroupViewHolder holder, int position) { holder.bind(getItem(position)); }

    @Override
    public void onViewRecycled(@NonNull AppLogGroupViewHolder holder) { super.onViewRecycled(holder); holder.clearImage(); }

    static class AppLogGroupViewHolder extends RecyclerView.ViewHolder {
        private final AppLogItemBinding binding;
        private final RequestOptions requestOptions;
        private XLogLog log;

        AppLogGroupViewHolder(AppLogItemBinding binding, LogAdapter.OnItemClickListener onItemClickListener, RequestOptions requestOptions) {
            super(binding.getRoot());
            this.binding = binding;
            this.requestOptions = requestOptions;
            binding.getRoot().setOnClickListener(v -> onItemClickListener.onItemClick(log));
            binding.getRoot().setOnLongClickListener(v -> { onItemClickListener.onItemLongClick(log); return true; });
        }

        void bind(XLogLog log) {
            this.log = log;
            binding.logTitle.setText(log.title);
            binding.logCode.setText(String.valueOf(log.code.getValue()));
            binding.logTime.setText(String.valueOf(log.time));
            binding.logMessage.setText(log.message);
        }

        void clearImage() { Glide.with(binding.logIcon.getContext()).clear(binding.logIcon); }
    }

    public interface OnItemClickListener {
        void onItemClick(XLogLog log);
        void onItemLongClick(XLogLog log);
    }

    private static final DiffUtil.ItemCallback<XLogLog> DIFF_CALLBACK = new DiffUtil.ItemCallback<XLogLog>() {
        @Override
        public boolean areItemsTheSame(@NonNull XLogLog oldItem, @NonNull XLogLog newItem) { return oldItem.title.equals(newItem.title);  }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull XLogLog oldItem, @NonNull XLogLog newItem) { return oldItem.equals(newItem); }
    };
}
