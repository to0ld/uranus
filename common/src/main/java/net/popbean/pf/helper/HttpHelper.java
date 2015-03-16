package net.popbean.pf.helper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;

/**
 * @author to0ld
 */
public class HttpHelper {

//	public final static String FAKE_HTTPS = "fakes";
//	public final static String SELF_HTTPS = "selfs";

	public final static ContentType CONTENT_TYPE_FORM = ContentType.create(URLEncodedUtils.CONTENT_TYPE, "UTF-8");
	public final static ContentType CONTENT_TYPE_TEXT = ContentType.create("text/plain", "UTF-8");
	public final static ContentType CONTENT_TYPE_JSON = ContentType.create("application/json", "UTF-8");

	private static String encode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return s;
		}
	}

	private static String stringify(Object s) {
		if (s == null) {
			return "";
		} else if (s.getClass().isPrimitive()) {
			return String.valueOf(s);
		} else if (s instanceof String) {
			return (String) s;
		} else {
			return JSON.toJSONString(s);
		}
	}

	public static String getContent(String url, Map<String, Object> param) {
		return getContent(url, param, (Header[]) null);
	}

	public static String getContent(String url, Map<String, Object> param, Header... headers) {
		if (param == null) {
			return getContent(url, (String) null, CONTENT_TYPE_FORM, headers);
		}
		StringBuffer sb = new StringBuffer();
		sb.append("param=");
//		sb.append(encode(encode(stringify(param))));
		sb.append(encode(stringify(param)));
		for (Entry<String, Object> entry : param.entrySet()) {
			String key = encode(entry.getKey());
			Object value = entry.getValue();
			if (value == null) {
				continue;
			}
			if (value.getClass().isArray()) {
				for (Object v : (Object[]) value) {
					if (sb.length() > 0) {
						sb.append("&");
					}
					sb.append(key);
					sb.append("=");
					sb.append(encode(stringify(v)));
				}
			} else {
				if (sb.length() > 0) {
					sb.append("&");
				}
				sb.append(key);
				sb.append("=");
				sb.append(encode(stringify(value)));
			}
		}
		return getContent(url, sb.toString(), CONTENT_TYPE_FORM, headers);
	}

	public static String getContent(String url) {
		return getContent(url, (String) null, null, (Header[]) null);
	}

	public static String getContent(String url, Header... headers) {
		return getContent(url, (String) null, null, headers);
	}

	public static String getContent(String url, String content) {
		return getContent(url, content, CONTENT_TYPE_TEXT, (Header[]) null);
	}

	public static String getContent(String url, String content, Header... headers) {
		return getContent(url, content, CONTENT_TYPE_TEXT, headers);
	}

	public static String getContent(String url, String content, ContentType contentType) {
		return getContent(url, content, contentType, (Header[]) null);
	}

	public static String getContent(String url, String content, ContentType contentType, Header... headers) {
		String body = null;
		
		CloseableHttpClient client = null;
		if (url.startsWith("http")) {
			client = HttpClientBuilder.create().build();
		}
		if(client == null){
			client = HttpClients.custom().setConnectionManager(Self.getConnectionManager()).build();
		}		
		HttpUriRequest request;
		if (content != null) {
			request = new HttpPost(url);
			if (content.length() > 0) {
				((HttpPost) request).setEntity(new StringEntity(content, contentType));
			}
		} else {
			request = new HttpGet(url);
		}
		if (headers != null) {
			for (Header header : headers) {
				request.addHeader(header);
			}
		}
		CloseableHttpResponse response = null;
		try {
			response = client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				body = EntityUtils.toString(response.getEntity(), "UTF-8");
			}else{
				body = "http status:"+statusCode;response.close();
			}
		} catch (IOException e) {
			return ExceptionUtils.getStackTrace(e);
		}finally{
			if(response !=null){
				try {
					response.close();	
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
			if(client !=null){
				try {
					client.close();	
				} catch (Exception e2) {
					//ignore first soso
				}
				
			}
		}
		return body;
	}

	private static class Self {


		private static X509TrustManager getTrustManager() {
			return new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
//					return null;
					return new X509Certificate[]{};
				}
			};
		}

		private static X509HostnameVerifier getHostnameVerifier() {
			return new X509HostnameVerifier() {
				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}

				@Override
				public void verify(String host, SSLSocket ssl) throws IOException {
				}

				@Override
				public void verify(String host, X509Certificate cert) throws SSLException {
				}

				@Override
				public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
				}
			};
		}

		private static SSLContext getSSLContext() {
			SSLContext context = null;
			X509TrustManager trustManager = getTrustManager();
			try {
				context = SSLContext.getInstance("SSL");
				context.init(null, new TrustManager[] { trustManager }, new SecureRandom());
			} catch (NoSuchAlgorithmException e) {
				// do nothing
			} catch (KeyManagementException e) {
				// do nothing
			}
			return context;
		}

		public static HttpClientConnectionManager getConnectionManager() {
			SSLContext sslcontext = getSSLContext();
			X509HostnameVerifier hostnameVerifier = getHostnameVerifier();
			//
			RegistryBuilder<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory> create();
			PlainConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,hostnameVerifier);
			r = r.register("https", sslsf);
			r = r.register("http", plainsf);
			PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(r.build());
			connectionManager.setMaxTotal(100);
			return connectionManager;
			//
		}
	}

}
