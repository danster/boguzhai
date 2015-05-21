package com.boguzhai.logic.utils;

/**
 * byte[] org.apache.commons.io.FileUtils.readFileToByteArray(File file)
 * byte[] org.apache.commons.io.IOUtils.toByteArray(InputStream input)
 * @author dan
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.boguzhai.activity.base.Variable;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileApi {
	private static final String TAG = "FileApi";
	private static final File SDCARD = Environment.getExternalStorageDirectory();
	private static final File DIRECTORY_PICTURES = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES);

	public static File convertBitmapToFile(Bitmap bitmap){
        //create a file to write bitmap data
        File f = new File(Variable.app_context.getCacheDir(), "bitmap_"+ Math.random()+".png");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    private static void checkStorageStatus(){
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    Log.d(TAG, "sdcard state: mounted and writable");
		}
		else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    Log.d(TAG, "sdcard state: mounted and readonly");
		}
		else {
		    Log.d(TAG, "sdcard state: " + state);
		}
	}
	
	public static File createFile( String path, boolean writable, boolean executable){
		File file = createFile(path);
		file.setWritable(writable);
		if(! file.setExecutable(executable)){
			Log.e(TAG, "cannot set the file executable to "+executable);
		}
		return file;
	}
	
	public static File createFile(String path){
		checkStorageStatus();
		File file = new File(SDCARD, path);
		file.getParentFile().mkdirs();
		Log.i(TAG, "file location: "+file.getAbsolutePath());
		try {
			if(! file.exists()){
				Log.i(TAG, "file did not exist, create new file now");
				file.createNewFile();
			}
			
			file.setReadOnly(); //Equivalent to file.setReadable(true) & file.setWritable(false)
			return file;
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.e(TAG, "cannot create the file");
		return null;
	}

	public static boolean delete(String path){
		checkStorageStatus();
		File file = new File(SDCARD, path);
		return file.delete();
	}
	
	// writer reference : http://developer.android.com/training/basics/data-storage/files.html
	public static void write(File file, byte[] content, boolean append){
		checkStorageStatus();
		file.setWritable(true);
		try {
			FileOutputStream fileOS = new FileOutputStream(file, append);
			fileOS.write(content); //Equivalent to write(buffer, 0, buffer.length).
            fileOS.flush();
            fileOS.close();   
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
            e.printStackTrace();
        } 	
	}
	
	public static void write(String path, byte[] content, boolean append){
		File file = createFile(path);
		write(file, content, append);
	
	}
	
	public static void write(File file, byte[] content){
		write(file, content, false);
	}
	
	public static void write(String path, byte[] content){
		write(path, content, false);
	}
	
	public static byte[] readToByteArray(File file){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] b = new byte[1024]; // read 1024 bytes a time

		try {
			Log.i(TAG, "start reading file");
			InputStream is = new BufferedInputStream(new FileInputStream(file));
			int bytesRead;
			while ((bytesRead = is.read(b)) != -1) {
			   bos.write(b, 0, bytesRead);
			   Log.i(TAG, "Have readed "+bos.size()+" bytes from file "); 
			}
			Log.i(TAG, "end reading file ");
			is.close();
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		byte[] data = bos.toByteArray();
		return data;
		
	}
	
	public static byte[] readToByteArray(String path){		
		File file = createFile(path);
		return readToByteArray(file);
	}
	
	public static Bitmap readToBitmap(File file){
		byte[] data = readToByteArray(file);
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}
	
	public static Bitmap readToBitmap(String path){
		byte[] data = readToByteArray(path);
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}
	
	/** functions to do list */
	
	public static byte[] read(File file, int offset, int length){ return null;}
	public static byte[] read(String path, int offset, int length){ return null;}
	public static void write(File file, byte[] content, int offset, int length){}
	public static void write(String path, byte[] content, int offset, int length){}


}
