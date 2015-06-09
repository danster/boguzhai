package com.boguzhai.logic.thread;

import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.dao.SharedKeys;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONObject;

/**
 * Created by danster on 5/18/15.
 */
public class LotTypeHandler  extends HttpJsonHandler{
    @Override
    public void handlerData(int code, JSONObject data){
            switch (code) {
                case 0:
                    if (data == null) break;
                    Variable.settings_editor.putString(SharedKeys.lotType, data.toString());
                    Variable.settings_editor.commit();
                    break;
                case 1:
                    Utility.toastMessage("网络错误");
                    break;
            }

    }
}
