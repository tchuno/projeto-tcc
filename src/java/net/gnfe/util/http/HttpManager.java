package net.gnfe.util.http;

import net.gnfe.util.http.HttpTimeoutExecutor.HttpTimeoutException;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class HttpManager {

	private DefaultHttpClient httpClient;
	private Map<String, String> headers = new LinkedHashMap<String, String>();
	private Long timeout;
	private boolean isSsl;
	private boolean throwByStatus = true;

	public HttpManager() {}


	public Response get(String url, Map<String, String> params) {

		if(params != null) {

			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			for (String name : params.keySet()) {
				String value = params.get(name);
				formparams.add(new BasicNameValuePair(name, value));
			}

			String queryString = URLEncodedUtils.format(formparams, "utf-8");
			url += "?" + queryString;
		}

		HttpGet httpGet = new HttpGet(url);

		addHeaders(httpGet);

		HttpResponse response = executar(httpGet);

		if(response == null) {
			throw new RuntimeException("response null");
		}

		return new Response(httpGet, response);
	}

	private void addHeaders(HttpRequestBase httpRequest) {

		//Exemplos úteis:
		//httpPost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
		//httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:17.0) Gecko/17.0 Firefox/17.0");
		//httpPost.addHeader("Host", "denatran.serpro.gov.br");
		//httpPost.addHeader("Origin", "http://denatran.serpro.gov.br");

		if (headers != null && !headers.isEmpty()) {

			for (String name : headers.keySet()) {
				String value = headers.get(name);
				httpRequest.addHeader(name, value);
			}
		}
	}

	public Response postJson(String url, String json) {

		HttpPost httpPost = new HttpPost(url);

		addHeaders(httpPost);

		StringEntity entity = new StringEntity(json, "UTF-8");
		entity.setContentType("application/json");
		httpPost.setEntity(entity);

		HttpResponse response = executar(httpPost, true);

		if(response == null) {
			throw new RuntimeException("response null");
		}

		return new Response(httpPost, response);
	}

	public Response post(String url, AbstractHttpEntity entity) {

		HttpPost httpPost = new HttpPost(url);

		addHeaders(httpPost);

		httpPost.setEntity(entity);

		HttpResponse response = executar(httpPost, true);

		if(response == null) {
			throw new RuntimeException("response null");
		}

		return new Response(httpPost, response);
	}

	public Response post(String url, Map<String, String> params) {

		HttpPost httpPost = new HttpPost(url);

		addHeaders(httpPost);

		setParams(params, httpPost);

		HttpResponse response = executar(httpPost, true);

		if(response == null) {
			throw new RuntimeException("response null");
		}

		return new Response(httpPost, response);
	}

	private void setParams(Map<String, String> params, HttpPost httpPost) {

		List<NameValuePair> formparams = new ArrayList<NameValuePair>();

		if(params != null) {
			for (String name : params.keySet()) {
				String value = params.get(name);
				formparams.add(new BasicNameValuePair(name, value));
			}
		}

		try {

			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
			httpPost.setEntity(entity);
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}

	public void setIsSsl(boolean isSsl) {
		this.isSsl = isSsl;
	}

	public void setThrowByStatus(boolean throwByStatus) {
		this.throwByStatus = throwByStatus;
	}

	private HttpResponse executar(HttpRequestBase httpRequest) {
		return executar(httpRequest, false);
	}

	private HttpResponse executar(HttpRequestBase httpRequest, boolean login) {

		if(httpClient == null) {
			throw new IllegalStateException("É nescessário chamar start() primeiro");
		}

		HttpResponse response = null;

		try {
			if(timeout != null) {

				HttpTimeoutExecutor executor = new HttpTimeoutExecutor(httpClient);
				executor.execute(httpRequest, timeout);

				response = executor.getResponse();
			}
			else {

				response = httpClient.execute(httpRequest);
			}
		}
		catch (SocketException e) {

			//String message = e.getMessage();
			//HttpHost proxy = proxyManager != null ? proxyManager.getProxy() : null;
			//if(proxyManager != null && message.contains(proxyManager.getHost())) {
			//	throw new RuntimeException("Não foi possível se comunicar com o servidor de proxy: " + message);
			//}
			//else {
			throw new RuntimeException(e);
			//}
		}
		catch (HttpTimeoutException e) {
			throw new RuntimeException("Tempo de espera excedido: " + e.getMessage(), e);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();

		if(throwByStatus) {
			if(statusCode == 403 || statusCode == 401 || (statusCode == 302 && !login)) {

				System.out.println("HttpManager.executar() Request: statusCode: " + statusCode);
				CookieStore cookieStore = httpClient.getCookieStore();
				List<Cookie> cookies = cookieStore.getCookies();
				for (Cookie cookie : cookies) {
					System.out.println("cookie: " + cookie.getName() + " value: " + cookie.getValue());
				}

				System.out.println("HttpManager.executar() Response: statusCode: " + statusCode);
				Header[] allHeaders = response.getAllHeaders();
				for (Header header : allHeaders) {
					System.out.println("header: " + header.getName() + " value: " + header.getValue());
				}

				release(httpRequest, response);

				throw new RuntimeException("Falha ao acessar: Não foi possível logar ao servidor, verifique se usuário e senha estão corretos. " + statusLine);
			}

			if(statusCode != 200 && !login) {

				release(httpRequest, response);

				throw new RuntimeException("Falha ao acessar: Falha na conexão ao servidor. Status Code: " + statusLine.toString());
			}
		}

		return response;
	}

	public File releaseToFile(HttpRequestBase httpRequest, HttpResponse response) {

		if(response == null) {
			return null;
		}

		// Get hold of the response entity
		HttpEntity entity = response.getEntity();

		// If the response does not enclose an entity, there is no need
		// to bother about connection release
		if (entity != null) {

			try {

				File file = File.createTempFile("httpToFile-", ".download");
				FileOutputStream fos = new FileOutputStream(file);
				entity.writeTo(fos);
				return file;
			}
			catch (IOException e) {

				// In case of an IOException the connection will be released
				// back to the connection manager automatically
				throw new RuntimeException("Falha ao ler dados: ", e);
			}
			catch (RuntimeException e) {

				// In case of an unexpected exception you may want to abort
				// the HTTP request in order to shut down the underlying
				// connection and release it back to the connection manager.
				httpRequest.abort();
				throw new RuntimeException("Falha ao ler dados: ", e);
			}
		}

		return null;
	}

	public String release(HttpRequestBase httpRequest, HttpResponse response) {

		if(response == null) {
			return null;
		}

		// Get hold of the response entity
		HttpEntity entity = response.getEntity();

		// If the response does not enclose an entity, there is no need
		// to bother about connection release
		if (entity != null) {

			try {

				String html = EntityUtils.toString(entity, "UTF-8");
				return html;
			}
			catch (IOException e) {

				// In case of an IOException the connection will be released
				// back to the connection manager automatically
				throw new RuntimeException("Falha ao ler dados: ", e);
			}
			catch (RuntimeException e) {

				// In case of an unexpected exception you may want to abort
				// the HTTP request in order to shut down the underlying
				// connection and release it back to the connection manager.
				httpRequest.abort();
				throw new RuntimeException("Falha ao ler dados: ", e);
			}
		}

		return null;
	}

	public void start() {

		try {
			httpClient = new DefaultHttpClient();

			if(isSsl) {

				SSLContext ctx = SSLContext.getInstance("TLS");
				X509TrustManager tm = new X509TrustManager() {
					public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {}

					public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {}

					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
				};
				ctx.init(null, new TrustManager[]{tm}, null);
				SSLSocketFactory ssf = new SSLSocketFactory(ctx);
				ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				ClientConnectionManager ccm = httpClient.getConnectionManager();
				SchemeRegistry sr = ccm.getSchemeRegistry();
				sr.register(new Scheme("https", ssf, 443));
			}
		}
		catch (KeyManagementException e) {
			throw new RuntimeException(e);
		}
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public class Response {

		private HttpRequestBase httpRequest;
		private HttpResponse httpResponse;

		public Response(HttpRequestBase httpRequest, HttpResponse httpResponse) {
			this.httpRequest = httpRequest;
			this.httpResponse = httpResponse;
		}

		public HttpResponse getHttpResponse() {
			return httpResponse;
		}

		public String release() {
			return HttpManager.this.release(httpRequest, httpResponse);
		}

		public File releaseToFile() {
			return HttpManager.this.releaseToFile(httpRequest, httpResponse);
		}

		public Header getHeader(String name) {

			Header[] headers = httpResponse.getHeaders(name);
			if(headers == null || headers.length == 0) {
				return null;
			}

			return headers[0];
		}
	}
}
