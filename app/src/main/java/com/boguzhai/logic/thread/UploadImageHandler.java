package com.boguzhai.logic.thread;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.boguzhai.logic.utils.Utility;

import org.json.JSONObject;

public class UploadImageHandler extends HttpJsonHandler{
    private ImageView iv = null;
    private Bitmap bitmap = null;

    public UploadImageHandler(ImageView iv, Bitmap bitmap){
        super();
        this.iv = iv;
        this.bitmap = bitmap;
    }

    @Override
    public void handlerData(int code, JSONObject data) {
        super.handlerData(code , data);
        switch(code){
            case 0:
                Utility.toastMessage("上传图像成功");
                iv.setImageBitmap(bitmap);
                break;
            default:
                Utility.toastMessage("上传图像失败");
                break;
        }
    }

}