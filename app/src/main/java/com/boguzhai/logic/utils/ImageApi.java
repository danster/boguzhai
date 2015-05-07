package com.boguzhai.logic.utils;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.boguzhai.R;
import com.boguzhai.activity.base.Variable;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;


public class ImageApi {
    private static Dialog getImageDialog = null;


    public static Bitmap Create2DCode(String str) throws WriterException {
        //生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 300, 300);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        //二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(matrix.get(x, y)){
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static void showUpdateImageDialog() {
        getImageDialog = new AlertDialog.Builder(Variable.currentActivity).create();
        getImageDialog.show();
        // getApplicationContext(), this 效果一样，点击空白处无反应
        View mDialogView = View.inflate(Variable.app_context, R.layout.dialog_take_photo, null);
        getImageDialog.getWindow().setContentView(mDialogView);

        WindowManager.LayoutParams lp = getImageDialog.getWindow().getAttributes();
        DisplayMetrics display = Variable.currentActivity.getResources().getDisplayMetrics();
        lp.width = (int) (display.widthPixels); // 设置宽度
        getImageDialog.getWindow().setAttributes(lp);

        Button cameraButton = (Button) mDialogView.findViewById(R.id.iv_userinfo_takepic);
        Button photoButton = (Button) mDialogView.findViewById(R.id.iv_userinfo_choosepic);
        Button cancelButton = (Button) mDialogView.findViewById(R.id.iv_userinfo_cancle);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    //拍照我们用Action为MediaStore.ACTION_IMAGE_CAPTURE，
                    //有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    Variable.currentActivity.startActivityForResult(intent, 1);
                    getImageDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    //选择照片的时候也一样，我们用Action为Intent.ACTION_GET_CONTENT，
                    //有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    //intent.setType("imageUrl/jpeg"); //选择有：图库 文件管理 其它程序
                    intent.setType("image/*"); //选择有：文件管理 其它程序
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Variable.currentActivity.startActivityForResult(intent, 3); // SELECT_PIC_KITKAT
                    } else {
                        Variable.currentActivity.startActivityForResult(intent, 2); // SELECT_PIC_NORMAL
                    }
                    getImageDialog.dismiss();
                } catch (ActivityNotFoundException e) {

                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getImageDialog.dismiss();
            }
        });
    }

    public static Bitmap getBitmap(int requestCode, Intent data){
        //取得返回的Uri,基本上选择照片的时候返回的是以Uri形式，但是在拍照中有得机子呢Uri是空的，所以要特别注意
        Uri mImageCaptureUri = data.getData();

        //返回的Uri不为空时，那么图片信息数据都会在Uri中获得。如果为空，那么我们就进行下面的方式获取
        if (mImageCaptureUri != null) {
            Log.i("TakePhoto", "从相册中选取照片");

            String picturePath="";
            if(requestCode == 2){ // Android 4.4 以下版本
                String uriStr = mImageCaptureUri.toString();
                String path = uriStr.substring(10, uriStr.length());
                if (path.startsWith("com.sec.android.gallery3d")) {
                    Log.e("It's auto backup", mImageCaptureUri.toString());
                }
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = Variable.currentActivity.getContentResolver().query(mImageCaptureUri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();

            } else if(requestCode == 3){ // Android 4.4与4.4以上版本，获取图片的真实路径
                picturePath = getPath(Variable.currentActivity, data.getData());
            } else {
                return null;
            }
            try {
                return FileApi.compressBitmap(BitmapFactory.decodeFile(picturePath));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Log.i("TakePhoto", "从拍照中获取图片");
            Bundle extras = data.getExtras();
            if (extras != null) {
                //这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
                return FileApi.compressBitmap( (Bitmap)extras.getParcelable("data") );
            }
        }

        return null;
    }

    /***********************************************************************************************
     * 4.4系统以上图库选择图片 返回路径处理
     *
     * @param context
     * @param uri
     * @return path of file
     *
     **********************************************************************************************/
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}

