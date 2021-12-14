package application;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpHelper {
	// 1.创建OkhttpClient对象
	private static OkHttpClient client = new OkHttpClient();

	public static String accessToken;// 访问令牌

	// 创建媒体类型
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	/**
	 * 发送okhttp get请求
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static String get(String url) throws IOException {
		// 2.构建请求对象Request
		Request.Builder builder = new Request.Builder();
		builder.url(url);
		if(accessToken!=null) {
			builder.addHeader("AccessToken", accessToken);
		}
		Request request = builder.build();
		// 3.发送请求（同步）
		Response response = client.newCall(request).execute();
		// 返回响应包体部分
		return response.body().string();
	}

	/**
	 * 发送okhttp post请求
	 * 
	 * @param url
	 * @param requestBodyJson
	 * @return
	 * @throws IOException
	 */
	public static String post(String url, String requestBodyJson) throws IOException {
		RequestBody body = RequestBody.create(JSON, requestBodyJson);
		// 2.构建请求对象Request
		Request.Builder builder = new Request.Builder();
		builder.url(url);
		if(accessToken!=null) {
			builder.addHeader("AccessToken", accessToken);
		}
		builder.post(body);
		Request request = builder.build();
		// 3.发送请求（同步）
		Response response = client.newCall(request).execute();
		// 返回响应包体部分
		return response.body().string();
	}
}
