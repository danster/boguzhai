package com.boguzhai.activity.me.info;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;

import org.json.JSONObject;

public class AccountInfoEditActivity extends BaseActivity {

    private ImageView image;
    private String image_url="";

    private Dialog setHeadDialog;
    private View mDialogView;


    private static final String[] list_addr_1={"不限","北京","上海","江苏","浙江","其他"};
    private static final String[] list_addr_2={"不限","南京","镇江","无锡","苏州","其他"};
    private static final String[] list_addr_3={"不限","玄武","鼓楼","江宁","雨花","其他"};

    private StringBuffer addr_1=new StringBuffer();
    private StringBuffer addr_2=new StringBuffer();
    private StringBuffer addr_3=new StringBuffer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setScrollView(R.layout.me_myinfo_edit);
        title.setText("编辑个人信息");

        init();
	}

	private void init(){
        image = (ImageView)findViewById(R.id.image);

        fillAccountInfo();

        utility.setSpinner(this, R.id.addr_1, list_addr_1, addr_1);
        utility.setSpinner(this, R.id.addr_2, list_addr_2, addr_2);
        utility.setSpinner(this, R.id.addr_3, list_addr_3, addr_3);

		int[] ids = { R.id.my_email, R.id.my_mobile, R.id.ok, R.id.name_clear, R.id.nickname_clear,
                      R.id.my_photo, R.id.telephone_clear, R.id.fax_clear, R.id.qq_clear, R.id.address_clear};
		this.listen(ids);
	}

    private void fillAccountInfo(){
        image.setImageBitmap(Variable.account.image);
        ((TextView)findViewById(R.id.name)).setText(Variable.account.name);
        ((EditText)findViewById(R.id.nickname)).setText(Variable.account.nickname);
        ((EditText)findViewById(R.id.address)).setText(Variable.account.address);
        ((EditText)findViewById(R.id.telephone)).setText(Variable.account.telephone);
        ((EditText)findViewById(R.id.fax)).setText(Variable.account.fax);
        ((EditText)findViewById(R.id.qq)).setText(Variable.account.qq);

        ((TextView)findViewById(R.id.email)).setText(Variable.account.email);
        ((TextView)findViewById(R.id.mobile)).setText(Variable.account.mobile);

        addr_1.replace(0,addr_1.length(),Variable.account.address_1);
        addr_2.replace(0,addr_2.length(),Variable.account.address_2);
        addr_3.replace(0,addr_3.length(),Variable.account.address_3);
    }

	@Override
	public void onClick(View view) {
		super.onClick(view);

		switch (view.getId()) {

		case R.id.ok:
            String nickname = ((EditText)findViewById(R.id.nickname)).getText().toString();
            String address = ((EditText)findViewById(R.id.address)).getText().toString();
            String telephone = ((EditText)findViewById(R.id.telephone)).getText().toString();
            String fax = ((EditText)findViewById(R.id.fax)).getText().toString();
            String qq = ((EditText)findViewById(R.id.qq)).getText().toString();

            HttpClient conn = new HttpClient();
            conn.setParam("sessionid", Variable.account.sessionid);
            conn.setParam("nickname", nickname);
            conn.setParam("address_1", addr_1.toString());
            conn.setParam("address_2", addr_2.toString());
            conn.setParam("address_3", addr_3.toString());
            conn.setParam("address", address);
            conn.setParam("telephone", telephone);
            conn.setParam("fax", fax);
            conn.setParam("qq", qq);
            conn.setParam("photo", image_url);
            conn.setUrl(Constant.url+"pClientInfoAction!setAccountInfo.htm");
            new Thread(new HttpPostRunnable(conn, new SubmitHandler())).start();

            break;
        case R.id.my_photo:
            showDialog();
            break;
        case R.id.my_email: startActivity(new Intent(this, AccountBindEmailActivity.class));  break;
        case R.id.my_mobile: startActivity(new Intent(this, AccountBindMobileActivity.class));break;
        case R.id.nickname_clear: ((EditText)findViewById(R.id.nickname)).setText(""); break;
        case R.id.telephone_clear:((EditText)findViewById(R.id.telephone)).setText("");break;
        case R.id.fax_clear:      ((EditText)findViewById(R.id.fax)).setText("");      break;
        case R.id.qq_clear:       ((EditText)findViewById(R.id.qq)).setText("");       break;
        case R.id.address_clear:  ((EditText)findViewById(R.id.address)).setText("");  break;

        default: break;
		};
	}

    public void showDialog() {
        setHeadDialog = new AlertDialog.Builder(this).create();
        setHeadDialog.show();
        // getApplicationContext(), this 效果一样，点击空白处无反应
        //
        mDialogView = View.inflate(getApplicationContext(), R.layout.dialog_take_photo, null);
        setHeadDialog.getWindow().setContentView(mDialogView);

        WindowManager.LayoutParams lp = setHeadDialog.getWindow().getAttributes();
        DisplayMetrics display = this.getResources().getDisplayMetrics();
        lp.width = (int) (display.widthPixels); // 设置宽度
        setHeadDialog.getWindow().setAttributes(lp);
        bindDialogEvent();

    }

    private void bindDialogEvent() {
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
                    startActivityForResult(intent, 1);
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
                    intent.setType("imageUrl/jpeg");
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        startActivityForResult(intent, 3); // SELECT_PIC_KITKAT
                    } else {
                        startActivityForResult(intent, 2); // SELECT_PIC_NORMAL
                    }
                } catch (ActivityNotFoundException e) {

                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setHeadDialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("TakePhoto", "request="+requestCode+", resultCode="+resultCode);

        if (resultCode != RESULT_OK || data == null) { setHeadDialog.dismiss(); return;}

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
                Cursor cursor = getContentResolver().query(mImageCaptureUri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();

            }else if(requestCode == 3){ // Android 4.4与4.4以上版本，获取图片的真实路径
                picturePath = getPath(this, data.getData());
                Log.i("TakePhoto", "path: "+picturePath);
            }else {
                setHeadDialog.dismiss(); return;
            }

            Bitmap bitmap = null;

            try {
                bitmap = BitmapFactory.decodeFile(picturePath);
                if (!bitmap.equals(null))
                    image.setImageBitmap(bitmap);
                else
                {
                    Toast.makeText(this, "图片加载失败", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Log.i("TakePhoto", "从拍照中获取图片");
            Bundle extras = data.getExtras();
            if (extras != null) {
                //这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
                Bitmap bitmap = extras.getParcelable("data");
                if (bitmap != null) {
                    image.setImageBitmap(bitmap);
                }
            }
        }

        setHeadDialog.dismiss();
    }

    private void setImage(Uri mImageCaptureUri) {

        // 不管是拍照还是选择图片每张图片都有在数据中存储也存储有对应旋转角度orientation值
        // 所以我们在取出图片是把角度值取出以便能正确的显示图片,没有旋转时的效果观看

        ContentResolver cr = this.getContentResolver();
        Cursor cursor = cr.query(mImageCaptureUri, null, null, null, null);// 根据Uri从数据库中找
        if (cursor != null) {
            cursor.moveToFirst();// 把游标移动到首位，因为这里的Uri是包含ID的所以是唯一的不需要循环找指向第一个就是了
            String filePath = cursor.getString(cursor.getColumnIndex("_data"));// 获取图片路
            String orientation = cursor.getString(cursor
                    .getColumnIndex("orientation"));// 获取旋转的角度
            cursor.close();
            if (filePath != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);//根据Path读取资源图片
                int angle = 0;
                if (orientation != null && !"".equals(orientation)) {
                    angle = Integer.parseInt(orientation);
                }
                if (angle != 0) {
                    // 下面的方法主要作用是把图片转一个角度，也可以放大缩小等
                    Matrix m = new Matrix();
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    m.setRotate(angle); // 旋转angle度
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);// 从新生成图片

                }
                image.setImageBitmap(bitmap);
            }
        }
    }

    public class SubmitHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            switch(code){
                case 0:
                    baseActivity.alertMessage("修改个人信息成功");
                    break;
                default:
                    baseActivity.alertMessage("修改个人信息失败");
                    break;
            }
        }
    }


    /**
     * 4.4系统以上图库选择图片 返回路径处理
     *
     * @param context
     * @param uri
     * @return
     */
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
     *
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {


        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };


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


