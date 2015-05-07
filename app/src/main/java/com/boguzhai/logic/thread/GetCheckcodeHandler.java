package com.boguzhai.logic.thread;

import com.boguzhai.logic.utils.Utility;

import org.json.JSONObject;

public class GetCheckcodeHandler extends HttpJsonHandler {
    @Override
    public void handlerData(int code, JSONObject data){
        switch(code){
            case 0:
                Utility.toastMessage("发送验证码成功，请注意查收");
                break;
            default:
                Utility.toastMessage("发送验证码失败，请重新获取验证码");
                break;
        }
    }
}