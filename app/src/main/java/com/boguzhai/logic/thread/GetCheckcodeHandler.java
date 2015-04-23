package com.boguzhai.logic.thread;

import com.boguzhai.activity.base.BaseActivity;

import org.json.JSONObject;

public class GetCheckcodeHandler extends HttpJsonHandler {
    private BaseActivity context;

    public GetCheckcodeHandler(BaseActivity context){
        this.context = context;
    }

    @Override
    public void handlerData(int code, JSONObject data){
        switch(code){
            case 0:
                context.toastMessage("发送验证码成功，请注意查收");
                break;
            default:
                context.toastMessage("发送验证码失败，请重新获取");
                break;
        }
    }
}