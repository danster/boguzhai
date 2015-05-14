package com.boguzhai.logic.utils;

/**
 * Name: HttpConnect Function
 * Version: 1.0
 * Author: 黄茂峰
 * Created Time: 2014-9-2
 */

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.net.ssl.SSLHandshakeException;

public class HttpClient {
    private static final String CHARSET = HTTP.UTF_8;
	private static final String TAG = "HttpClient";
    private static String regex = "^(http://|https://)?((?:[A-Za-z0-9]+-[A-Za-z0-9]+|[A-Za-z0-9]+)\\.)+([A-Za-z]+)[/\\?\\:]?.*$" ;
    private static Pattern pattern = Pattern.compile(regex);


    public org.apache.http.client.HttpClient httpClient = null;  //HTTP 客户端连接管理器
    public String url = "";
    public String requestType = "GET";
    public int connectionPoolTimeout = 5000; //从ConnectionManager管理的连接池中取出连接的超时时间，毫秒
    public int connectionTimeout = 7500; //通过网络与服务器建立连接的超时时间(请求超时)，毫秒
    public int socketTimeout = 10000; //Socket读数据的超时时间，即从服务器获取响应数据需要等待的时间，毫秒

    public HttpRequestBase httpRequest = null; //HTTP 请求管理器
    public HttpParams httpParameters = null;   //HTTP 请求的配置参数
    public HttpResponse httpResponse = null;   //HTTP 响应管理器
    public HttpEntity responseEntity = null;     //HTTP 响应的内容

    public int statusCode = -1; //HTTP 响应的状态码

    public ArrayList<NameValuePair> params = null; // HTTP Request Content String类型 参数列表
    public ArrayList<NameValuePair> headerParams = null; // HTTP Request Header 参数列表
    public MultipartEntityBuilder multipartEntityBuilder = null; // HTTP POST Request Content form-data类型 参数管理器

    private HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler();

	/***** Constructors *****/
	public HttpClient(){}

    /****** 为网络链接添加参数: Headler, Content, Url-Key ******/
    public void setHeader(String name, String value){
    	if(headerParams == null){
    		headerParams = new ArrayList<NameValuePair>();
    	}
    	headerParams.add(new BasicNameValuePair(name, value));
    }

    public void setParam(String key, String value){
    	if(params == null){
    		params = new ArrayList<NameValuePair>();
    	}
    	params.add(new BasicNameValuePair(key, value));
    }

    public void setParamFile(String key, File file){
        MultipartEntityBuilder builder = this.getMultipartEntityBuilder();
        builder.addBinaryBody(key, file);
        // builder.addBinaryBody(key, file, ContentType.create("image/jpeg"), null);
    }

    // 将图片压缩成Base64编码字符串，之后再addTextBody
    public void setParamBitmap(String key, Bitmap bitmap){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
        byte[] data = bos.toByteArray();
        String bitmap_str= Base64.encodeToString(data, 0);
        this.setParam(key,bitmap_str);

        // 检查 bitmap_str
        File f = FileApi.createFile("test.txt");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        try {
            bw.write(bitmap_str);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /****** HTTP Get and Post ******/
    public void get(){ this.get(this.url); }

    public void get(String url){ 
        this.requestType = "GET";
		if(params != null){
			url = url +"?"+ URLEncodedUtils.format(params, CHARSET);
		}

        if(!checkUrl()){
            httpResponse = null;
            statusCode = -1;
            return;
        }

        Log.i(TAG,"http get: "+url);
        this.httpRequest = new HttpGet(url);
        this.httpClientExecute(); // 执行客户端请求
    }

    public void post(){ this.post(this.url); }

    public void post(String url) { 
        this.requestType = "POST";

        // 这里只能添加文本参数,上传文件等多媒体请另外添加
        String para="";
		if(params != null){
            MultipartEntityBuilder builder = this.getMultipartEntityBuilder();
			for (NameValuePair param : params){
                para += param.getName()+"="+param.getValue()+"&";
				builder.addTextBody(param.getName(), param.getValue(), ContentType.create("text/plain", MIME.UTF8_CHARSET));
			}
		}
        if(!checkUrl()){
            Log.i(TAG,"http request: url is illegal !");
            httpResponse = null;
            statusCode = -1;
            return;
        }
        if(!para.equals("")){
            Log.i(TAG,"http post: "+url+"?"+para.substring(0,para.length()-1));
        }else {
            Log.i(TAG,"http post: "+url);
        }

        this.httpRequest = new HttpPost(url);
        HttpEntity httpEntity = this.getMultipartEntityBuilder().build();
        ((HttpPost)this.httpRequest).setEntity(httpEntity); //设置POST Content

        this.httpClientExecute(); // 执行客户端请求
    }

    // 判断url是否合法
    private boolean checkUrl(){
        if(url.equals("")){ return false;}
        if(!( url.startsWith("http://") || url.startsWith("https://") )){ url = "http://" + url;}
        if(url.equals("") || url.contains(" ")){ return false;}
        // if (!pattern.matcher(url).matches()){ return false;} 匹配表达式不正确
        return true;
    }

    /********** 执行网络链接请求, 生成 响应状态码 和 响应内容(状态码为SC_OK的话) ************/
    protected void httpClientExecute() {
        if (this.httpClient != null && this.httpClient.getConnectionManager() != null) {
            this.httpClient.getConnectionManager().shutdown(); //如果没有关闭客户端HTTPClient,需关闭启动垃圾回收机制
        }

        // 添加 HTTP Header 参数列表
        if(headerParams != null){
            for (NameValuePair headerParam : headerParams){
                this.httpRequest.addHeader(headerParam.getName(), headerParam.getValue());
            }
        }

        httpParameters = new BasicHttpParams(); // 新建并配置 HTTP 请求参数
        ConnManagerParams.setTimeout(httpParameters, connectionPoolTimeout); // 从连接池中取连接的超时时间
        HttpConnectionParams.setConnectionTimeout(httpParameters, connectionTimeout); // 客户端请求超时
        HttpConnectionParams.setSoTimeout(httpParameters, socketTimeout); // 服务器响应超时

        // 设置我们的HttpClient支持HTTP和HTTPS两种模式
        SchemeRegistry schReg =new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        // 使用线程安全的连接管理来创建HttpClient
        ClientConnectionManager cm =new ThreadSafeClientConnManager(httpParameters, schReg);

        // 创建一个客户端HTTP请求
        // 如果不使用连接管理器：this.httpClient =new DefaultHttpClient(httpParameters);
        this.httpClient =new DefaultHttpClient(cm, httpParameters);
        // ((AbstractHttpClient)this.httpClient ).setHttpRequestRetryHandler(requestRetryHandler);

        try {
        	Log.i(TAG,"http connect: start ... ");
			this.httpResponse = this.httpClient.execute(this.httpRequest); // 发送HTTP请求并获取服务端响应
        } catch (IOException e) {
            Log.i(TAG,"http connect: IO Exception when executing request !");
            e.printStackTrace();
            this.responseEntity = null;
            return;
		}

        Log.i(TAG,"http connect: end.");
        //this.httpResponse.getFirstHeader("sessionid");
        this.statusCode = this.httpResponse.getStatusLine().getStatusCode(); // 获取 HTTP 响应的状态码
        
        if (this.statusCode == HttpStatus.SC_OK) {
        	Log.i(TAG,"http result: succeed !");
        	this.responseEntity = httpResponse.getEntity();
        } else {
        	Log.i(TAG,"http result: error, status code is :"+this.statusCode);
        	this.responseEntity = null;
        }
    }

    /******* 对响应内容的简单操作 *********/
    public byte[] responseToByteArray(){
        if(responseEntity == null){  return  null;}

		try {
			return EntityUtils.toByteArray(responseEntity);
		} catch (IOException e) {
			Log.e(TAG, "EntityUtils cannot convert HttpEntity to ByteArray");
			e.printStackTrace();
		}
        return null;
	}
	
	public String responseToString(String charset){
        if(responseEntity==null){
            return null;
        }

		try {
			// responseEntity 是HTTP请求的响应体
			String result = EntityUtils.toString(responseEntity, charset);
			result = StringEscapeUtils.unescapeHtml(result);
			return result;
		} catch (IOException e) {
			Log.e(TAG, "EntityUtils cannot convert HttpEntity to String");
			e.printStackTrace();
		}
        return null;
	}

    public String responseToString(){
        return responseToString(CHARSET);
    }

	public void responseToFile(File file){
        if(responseEntity==null){
            return;
        }

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
    public boolean isGet(){ return this.requestType == "GET"; }

    public boolean isPost(){ return this.requestType == "POST"; }

    public String getUrl() { return url;}

    public void setUrl(String url) {  this.url = url;}
    
    public HttpClient setRequestType(String requestType){ this.requestType = requestType; return this;}

    public HttpClient setMultipartEntityBuilder(MultipartEntityBuilder m){
        this.multipartEntityBuilder = m;
        return this;
    }

    public String getRequestType(){ return this.requestType;}

    public int getStatusCode(){ return this.statusCode;}
    
    public HttpEntity getResponseEntity(){ return this.responseEntity;}

    public MultipartEntityBuilder getMultipartEntityBuilder()    {
        if (this.multipartEntityBuilder == null) {
            this.multipartEntityBuilder = MultipartEntityBuilder.create();
            // 设置为浏览器兼容模式
            multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            // 设置请求的编码格式
            multipartEntityBuilder.setCharset(Charset.forName(CHARSET));
        }
        return this.multipartEntityBuilder;
    }

    /**************** 设置重连机制和异常自动恢复处理 ******************/
    class HttpRequestRetryHandler extends DefaultHttpRequestRetryHandler {
        public HttpRequestRetryHandler() {
            super();
        }

        public HttpRequestRetryHandler(int retryCount, boolean requestSentRetryEnabled) {
            super(retryCount, requestSentRetryEnabled);
        }

        @Override
        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
            // 设置恢复策略，在Http请求发生异常时候将自动重试3次
            if (executionCount >= 3) {
                // Do not retry if over max retry count
                return false;
            }

            if (exception instanceof ConnectionPoolTimeoutException) {
                Log.i(TAG, "Connection Pool Timeout ! " + executionCount);
                return true;
            }

            if (exception instanceof NoHttpResponseException) {
                // Retry if the server dropped connection on us
                return true;
            }
            if (exception instanceof SSLHandshakeException) {
                // Do not retry on SSL handshake exception
                return false;
            }

            // InterruptedIOException 有两个子类异常，ConnectTimeoutException, SocketTimeoutException
            if (exception instanceof ConnectTimeoutException) {
                Log.i(TAG, "Connection Timeout ! " + executionCount);
                return true;
            }
            if (exception instanceof SocketTimeoutException) {
                Log.i(TAG, "Socket Timeout ! " + executionCount);
                return true;
            }
            if (exception instanceof InterruptedIOException) {
                Log.i(TAG, "Interrupted IO Timeout ! " + executionCount);
                return true;
            }

            HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
            boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
            if (!idempotent) {
                // Retry if the request is considered idempotent(幂等)
                return true;
            }
            return false;
        }
    }
}
