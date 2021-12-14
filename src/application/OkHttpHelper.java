package application;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpHelper {
	// 1.����OkhttpClient����
	private static OkHttpClient client = new OkHttpClient();

	public static String accessToken;// ��������

	// ����ý������
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	/**
	 * ����okhttp get����
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static String get(String url) throws IOException {
		// 2.�����������Request
		Request.Builder builder = new Request.Builder();
		builder.url(url);
		if(accessToken!=null) {
			builder.addHeader("AccessToken", accessToken);
		}
		Request request = builder.build();
		// 3.��������ͬ����
		Response response = client.newCall(request).execute();
		// ������Ӧ���岿��
		return response.body().string();
	}

	/**
	 * ����okhttp post����
	 * 
	 * @param url
	 * @param requestBodyJson
	 * @return
	 * @throws IOException
	 */
	public static String post(String url, String requestBodyJson) throws IOException {
		RequestBody body = RequestBody.create(JSON, requestBodyJson);
		// 2.�����������Request
		Request.Builder builder = new Request.Builder();
		builder.url(url);
		if(accessToken!=null) {
			builder.addHeader("AccessToken", accessToken);
		}
		builder.post(body);
		Request request = builder.build();
		// 3.��������ͬ����
		Response response = client.newCall(request).execute();
		// ������Ӧ���岿��
		return response.body().string();
	}
}
