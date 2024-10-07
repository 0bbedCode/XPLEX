package com.obbedcode.shared.repositories.filters.shell.interceptors;

import com.obbedcode.shared.BuildConfig;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.repositories.filters.shell.CommandData;
import com.obbedcode.shared.repositories.interfaces.ICommandHook;
import com.obbedcode.shared.repositories.interfaces.ICommandInterceptor;
import com.obbedcode.shared.utils.CommandProcessUtils;
import com.obbedcode.shared.xplex.XParam;

public class StatCommandInterceptor implements ICommandInterceptor {
    private static final String TAG = "ObbedCode.XP.StatCommandInterceptor";

    public String category = "stat";

    public String[] commandRegex = new String[] { "stat"};
    @Override
    public boolean isCommand(CommandData data) {
        if(data != null && data.commands != null) {
            for (String p : data.commands)
                if(p.equalsIgnoreCase(commandRegex[0]))
                    return true;
        }
        return false;
    }

    @Override
    public boolean handleCommand(XParam param, CommandData com, ICommandHook iCommandHook) {
        try {
            XLog.i(TAG, "[shell factoryy] Handling Command");
            String result = param.readCommandOutput();
            XLog.i(TAG, "[shell factoryy] STAT command:\n" + result);
            String newResult = CommandProcessUtils.randomizeStatOutput(result);
            if(BuildConfig.DEBUG)
                XLog.i(TAG, "Old:\n" + result + "New:\n" + newResult);

            param.setResult(param.echoCommand(newResult));
            return true;
        }catch (Exception e) {
            XLog.e(TAG, "Failed to Intercept Stat Command: " + e);
            return false;
        }
    }

    @Override
    public String getCategory() { return category; }
}
