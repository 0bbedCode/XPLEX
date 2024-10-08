package com.obbedcode.xplex.views;

import rikka.recyclerview.BaseViewHolder;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.obbedcode.shared.IXPService;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.service.ServiceClient;
import com.obbedcode.shared.utils.ThreadUtils;
import com.obbedcode.shared.utils.UsageUtils;
import com.obbedcode.xplex.R;
import com.obbedcode.xplex.databinding.MainCardContainerBinding;
import com.obbedcode.xplex.databinding.MainCardStatsBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import rikka.recyclerview.BaseViewHolder;

public class HomeStatsViewHolder extends BaseViewHolder<Object> {
    private static final String TAG = "ObbedCode.XP.HomeStatsViewHolder";

    private int errorTimes = 0;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static final Creator<Object> CREATOR = new Creator<Object>() {
        @Override
        public BaseViewHolder<Object> createViewHolder(LayoutInflater inflater, ViewGroup parent) {
            //MainCardContainerBinding outer = MainCardContainerBinding.inflate(inflater, parent, false);
            //MainCardStatsBinding inner = MainCardStatsBinding.inflate(inflater, outer.getRoot(), true);
            //return new HomeStatsViewHolder(inner.getRoot());

            //Inflate "main_card_container.xml" as its the Card View BackGround
            //Inflate "main_card_welcome.xml" as its the actual view that will be placed or inflated onto the Card
            // Inflate "main_card_container.xml" as its the Card View BackGround
            View outerView = inflater.inflate(R.layout.main_card_container, parent, false);
            // Inflate "main_card_welcome.xml" as its the actual view that will be placed or inflated onto the Card
            View innerView = inflater.inflate(R.layout.main_card_stats, (ViewGroup) outerView, true);
            // Return the new ViewHolder instance with the inflated layout
            return new HomeStatsViewHolder(innerView);
        }
    };

    public HomeStatsViewHolder(View itemView) {
        super(itemView);
        final ProgressBar pbRam = itemView.findViewById(R.id.ram_progress_bar);
        final TextView tvRam = itemView.findViewById(R.id.ram_progress_text);

        final ProgressBar pbCpu = itemView.findViewById(R.id.cpu_progress_bar);
        final TextView tvCpu = itemView.findViewById(R.id.cpu_progress_text);
        executorService.submit(() -> {
            while (errorTimes < 50) {
                try {
                    while (errorTimes < 50) {
                        ThreadUtils.sleep(1500);
                        IXPService serv = ServiceClient.waitForService();
                        if(serv != null) {
                            //Log.d(TAG, "Some Log should be null: " + serv.getLog());
                            int ramUsage = (int)Math.round(serv.getOverallMemoryUsage());
                            int cpuUsage = (int)Math.round(serv.getOverallCpuUsage());

                            String ramUsageStr = String.valueOf(ramUsage);
                            String cpuUsageStr = String.valueOf(cpuUsage);

                            new Handler(Looper.getMainLooper()).post(() -> {
                               pbRam.setProgress(ramUsage);
                               tvRam.setText(ramUsageStr);

                               pbCpu.setProgress(cpuUsage);
                               tvCpu.setText(cpuUsageStr);
                            });
                        } else {
                            XLog.e(TAG, "Service is NULL failed to wait for it...");
                        }
                    }
                }catch (Exception e) {
                    errorTimes++;
                    XLog.e(TAG, "Stats Grabbing error: " + e);
                }
            }
        });
    }
}
