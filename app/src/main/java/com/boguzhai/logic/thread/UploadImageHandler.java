package com.boguzhai.logic.thread;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.boguzhai.logic.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class UploadImageHandler extends HttpJsonHandler{
    private StringBuffer image_url = null;
    private ImageView iv = null;
    private Bitmap bitmap = null;

    public UploadImageHandler(StringBuffer image_url, ImageView iv, Bitmap bitmap){
        super();
        this.image_url = image_url;
        this.iv = iv;
        this.bitmap = bitmap;
    }

    @Override
    public void handlerData(int code, JSONObject data) {
        switch(code){
            case 0:
                Utility.toastMessage("上传图像成功");
                try {
                    image_url = image_url.replace(0,image_url.length(),data.getString("filepath")); //赋值
                    iv.setImageBitmap(bitmap);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case -1:
                Utility.gotoLogin();
                break;
            default:
                Utility.toastMessage("上传图像失败");
                break;
        }
    }

}