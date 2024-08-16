package com.obbedcode.xplex.uiex;

import com.obbedcode.shared.logger.XLog;

import java.util.List;

import rikka.recyclerview.BaseViewHolder;
import rikka.recyclerview.IdBasedRecyclerViewAdapter;

public class IdRecyclerViewUtils {
    public static Object getObjectFromId(IdBasedRecyclerViewAdapter idRc, long id) {
        List<Long> idCopy = idRc.getIds();
        for(int i = 0; i < idCopy.size(); i++) {
            long idTest = idCopy.get(i);
            if (idTest == id) {
                return idRc.getItemAt(i);
            }
        } return null;
    }
}
