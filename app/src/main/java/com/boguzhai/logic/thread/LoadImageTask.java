package com.boguzhai.logic.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.boguzhai.activity.items.ImageLoader;
import com.boguzhai.logic.utils.ServiceApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * 异步下载图片的任务。
 *
 * @author guolin
 */
public class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

    private ImageLoader imageLoader = ImageLoader.getInstance(); // 对图片进行管理的工具类
    private String mImageUrl = ""; // 图片的URL地址
    private ImageView mImageView = null; // 可重复使用的ImageView
    private int ratio=1; // 下载图片的长宽设为原图的ratio分之一

    public LoadImageTask() {}

    /**
     * 将可重复使用的ImageView传入
     *
     * @param imageView
     */
    public LoadImageTask(ImageView imageView) {
        mImageView = imageView;
    }

    /**
     * 将可重复使用的ImageView和下载压缩率传入,
     *
     * @param imageView
     * @param imageRatio
     */
    public LoadImageTask(ImageView imageView, int imageRatio) {
        mImageView = imageView;
        ratio = imageRatio;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        mImageUrl = params[0];

        if(mImageUrl.equals("")){
            return null;
        }

        // 如果手机没有安装SD卡，只下载图片不缓存进SD卡
        if(!ServiceApi.hasSDCard()){
            try {
                BitmapFactory.Options options=new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                options.inSampleSize = ratio;
                InputStream in = new URL(mImageUrl).openStream();
                return BitmapFactory.decodeStream(in,null,options);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        Bitmap imageBitmap = imageLoader.getBitmapFromMemoryCache(mImageUrl);
        if (imageBitmap == null) {
            Log.i("TAG", "Load image: "+mImageUrl);
            imageBitmap = loadImage(mImageUrl);
        }
        return imageBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            if (mImageView != null) {
                mImageView.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * 根据传入的URL，对图片进行加载。如果这张图片已经存在于SD卡中，则直接从SD卡里读取，否则就从网络上下载。
     *
     * @param imageUrl 图片的URL地址
     * @return 加载到内存的图片。
     */
    private Bitmap loadImage(String imageUrl) {
        File imageFile = new File(getImagePath(imageUrl));
        if (!imageFile.exists()) {
            Log.i("TAG", "file not exist: "+imageFile.getPath());
            downloadImage(imageUrl);
        }
        if (imageUrl != null) {
            Bitmap bitmap = decodeBitmapFromResource(imageFile.getPath(), 1);
            if (bitmap != null) {
                imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
                return bitmap;
            }
        }
        return null;
    }

    /**
     * 将图片下载到SD卡缓存起来。
     *
     * @param imageUrl 图片的URL地址。
     */
    private void downloadImage(String imageUrl) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d("TAG", "monted sdcard");
        } else {
            Log.d("TAG", "has no sdcard");
        }

        try {
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = ratio;
            InputStream in = new URL(mImageUrl).openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
            if (bitmap != null) {
                // 将一张图片存储到LruCache中
                imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
            }

            // 将图片放到SD卡缓存起来
            File imageFile = new File(getImagePath(imageUrl));
            FileOutputStream fOut = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap decodeBitmapFromResource(String pathName, int mRatio){
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = mRatio;
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    /**
     * 获取图片的本地存储路径。
     *
     * @param imageUrl 图片的URL地址。
     * @return 图片的本地存储路径。
     */
    public static String getImagePath(String imageUrl) {
        int lastSlashIndex = imageUrl.lastIndexOf("/");
        String imageName = imageUrl.substring(lastSlashIndex + 1);
        String imageDir = Environment.getExternalStorageDirectory().getPath() + "/Shbgz/Images/";
        File file = new File(imageDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        String imagePath = imageDir + imageName;
        return imagePath;
    }
}