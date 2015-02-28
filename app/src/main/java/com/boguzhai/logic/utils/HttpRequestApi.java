package com.boguzhai.logic.utils;

/**
 * Name: HttpConnect Function
 * Version: 1.0
 * Author: 黄茂峰
 * Created Time: 2014-9-2
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class HttpRequestApi {
	private static final String TAG = "HttpRequestDao";

    protected String url = "";
    protected String requsetType = "GET";
    protected int connectionTimeout = 7500; //网络链接的超时时间，毫秒
    protected int socketTimeout = 15000;    //读取远程socket数据的超时时间，毫秒
    protected String charset = HTTP.UTF_8;  //当前链接的字符编码
    
    protected HttpRequestBase httpRequest = null; //HTTP 请求管理器
    protected HttpParams httpParameters = null;   //HTTP 请求的配置参数
    protected HttpResponse httpResponse = null;   //HTTP 响应管理器
    protected HttpEntity responseEntity=null;     //HTTP 响应的内容
    protected HttpClient httpClient = null;       //HTTP 客户端连接管理器
    
    protected int statusCode = -1; //HTTP 响应的状态码
    
    protected ArrayList<NameValuePair> params = null; // POST/GET string 类型的参数
    protected ArrayList<NameValuePair> headerParams = null; // HTTP Request Header 参数列表
    protected MultipartEntityBuilder multipartEntityBuilder = null; //HTTP POST方式发送多段数据管理器
	
	/***** Constructors *****/
	
	public HttpRequestApi(){}
    
    public void addHeader(String name, String value){
    	if(headerParams == null){
    		headerParams = new ArrayList<NameValuePair>();
    	}
    	headerParams.add(new BasicNameValuePair(name, value));
    }
    
    public void addParam(String key, String value){
    	if(params == null){
    		params = new ArrayList<NameValuePair>();
    	}
    	params.add(new BasicNameValuePair(key, value));
    }
    
    public void get(String url){ 
        this.requsetType = "GET";
		if(params != null){
			url = url +"?"+ URLEncodedUtils.format(params, "utf-8"); 
		}
        this.httpRequest = new HttpGet(url);
        this.httpClientExecute(); // 执行客户端请求
    }

    public void get(){ this.get(this.url);}

    public void post(String url) { 
        this.requsetType = "POST";
        this.httpRequest = new HttpPost(url);
		if(params != null){
			MultipartEntityBuilder builder = this.getMultipartEntityBuilder();
			for (NameValuePair param : params){
				builder.addTextBody(param.getName(), param.getValue(),
                        ContentType.create("text/plain", MIME.UTF8_CHARSET));
			}
		}

        HttpEntity httpEntity = this.multipartEntityBuilder.build();
        this.getHttpPost().setEntity(httpEntity);
        
        this.httpClientExecute(); // 执行客户端请求
        Log.i(TAG,"end http post ");
    }

    public void post(){ this.post(this.url);}
    
    /********** 网络链接, 生成 响应状态码 和 响应内容(状态码为SC_OK的话) ************/
    protected void httpClientExecute() {
        if (this.httpClient != null && this.httpClient.getConnectionManager() != null) {
            this.httpClient.getConnectionManager().shutdown(); //如果没有关闭客户端HTTPClient,需关闭启动垃圾回收机制
        }

        this.httpParameters = new BasicHttpParams(); // 新建并配置 HTTP 请求参数
        this.httpParameters.setParameter("charest", this.charset); // 默认的数据编码
        this.httpParameters.setParameter(HTTP.CONTENT_TYPE, "multipart/form-data"); // 默认的POST数据提交方式
        
    	// 添加 HTTP Header 参数列表
		if(headerParams != null){
			for (NameValuePair headerParam : headerParams){
				this.httpParameters.setParameter(headerParam.getName(), headerParam.getValue());
			}
		}
		
		this.httpClient = new DefaultHttpClient(this.httpParameters); //开启一个客户端 HTTP 请求
        HttpConnectionParams.setConnectionTimeout(this.httpParameters, this.connectionTimeout);
        HttpConnectionParams.setSoTimeout(this.httpParameters, this.socketTimeout);

        try {
        	Log.i(TAG,"start http connecting");
			this.httpResponse = this.httpClient.execute(this.httpRequest); // 发送 HTTP 请求并获取服务端响应
        } catch(ConnectTimeoutException e){
            Log.i(TAG,"time out");
            this.responseEntity = null;
            return;
        } catch (IOException e) {
			Log.i(TAG,"error at http connecting !!!");
            this.responseEntity = null;
            return;
		} 
        
        Log.i(TAG,"end http connecting");
        this.statusCode = this.httpResponse.getStatusLine().getStatusCode(); // 获取 HTTP 响应的状态码
        
        if (this.statusCode == HttpStatus.SC_OK) {
        	Log.i(TAG,"Connect successed");
        	this.responseEntity = httpResponse.getEntity();
        } else {
        	Log.i(TAG,"Connect error, status code is :"+this.statusCode);
        	this.responseEntity = null;
        }
    }

    /******* 对下载后的数据的简单操作 ********************************************/
    public byte[] responseToByteArray(){
		try {
			return EntityUtils.toByteArray(responseEntity);
		} catch (IOException e) {
			Log.e(TAG, "EntityUtils cannot convert HttpEntity to ByteArray");
			e.printStackTrace();
		}
        return null;
	}
	
	public String responseToString( String charset){
		try {
			// responseEntity 是HTTP请求的响应体
			String result = EntityUtils.toString(responseEntity, charset);
			result = StringEscapeUtils.unescapeHtml(result);
			Log.i(TAG, "Response string : "+result);
			return result;
		} catch (IOException e) {
			Log.e(TAG, "EntityUtils cannot convert HttpEntity to String");
			e.printStackTrace();
		}
        return null;
	}

    public String responseToString(){
        return responseToString(HTTP.UTF_8);
    }
	
	public Bitmap responseToBitmap(){
		byte[] data = responseToByteArray();
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}
	 
	public void responseToFile(File file){
		FileOutputStream fileOS;
		try {
			fileOS = new FileOutputStream(file);
            responseEntity.writeTo(fileOS);
            responseEntity.consumeContent();
            fileOS.flush();
            fileOS.close();    
		} catch (IOException e) {
			Log.e(TAG, "Cannot write HttpEntity to file");
            e.printStackTrace();
        }  
	}
    
	/************* setters, getters, checkers ********************************/
    public boolean isGet(){ return this.requsetType == "GET"; }

    public boolean isPost(){ return this.requsetType == "POST"; }

    public String getUrl() {   return url;}

    public void setUrl(String url) {    this.url = url;}
    
    public HttpRequestApi setRequsetType(String requsetType){ this.requsetType = requsetType; return this;}

    public HttpRequestApi setConnectionTimeout(int timeout){ this.connectionTimeout = timeout; return this; }

    public HttpRequestApi setSocketTimeout(int timeout){ this.socketTimeout = timeout; return this; }

    public HttpRequestApi setCharset(String charset){ this.charset = charset; return this; }

    public HttpRequestApi setMultipartEntityBuilder(MultipartEntityBuilder m){
        this.multipartEntityBuilder = m;
        return this;
    }

    public String getRequestType(){ return this.requsetType;}
    
    public HttpResponse getHttpResponse(){ return this.httpResponse;}

    public HttpClient getHttpClient(){ return this.httpClient;}
    
    public HttpGet getHttpGet(){ return (HttpGet) this.httpRequest;}

    public HttpPost getHttpPost(){ return (HttpPost) this.httpRequest;}

    public int getStatusCode(){ return this.statusCode;}
    
    public HttpEntity getResponseEntity(){ return this.responseEntity;}

    public MultipartEntityBuilder getMultipartEntityBuilder()    {
        if (this.multipartEntityBuilder == null) {
            this.multipartEntityBuilder = MultipartEntityBuilder.create();
            // 设置为浏览器兼容模式
            multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            // 设置请求的编码格式
            multipartEntityBuilder.setCharset(Charset.forName(this.charset));
        }
        return this.multipartEntityBuilder;
    }

}
