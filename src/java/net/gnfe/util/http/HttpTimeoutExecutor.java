package net.gnfe.util.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HttpTimeoutExecutor implements Runnable {

	private final HttpClient httpclient;
	private HttpUriRequest httpRequest;
	private Exception ioException;
	private HttpResponse response;
	private boolean esperando;

	public HttpTimeoutExecutor(HttpClient httpclient) {
		this.httpclient = httpclient;
	}

	public synchronized void execute(HttpUriRequest httpRequest, long timeout) throws Exception {

		this.httpRequest = httpRequest;
		response = null;

		esperando = true;

		Thread thread = new Thread(this);
		thread.start();

		try {
			thread.join(timeout);
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		esperando = false;

		if(ioException != null) {
			throw ioException;
		}

		if(response == null) {

			//shutdown libera a conexão travada
			httpclient.getConnectionManager().shutdown();

			throw new HttpTimeoutException(timeout);
		}
	}

	@Override
	public void run() {

		try {
			//caso o sistema tenha dado OutOfMemory a coneçao vai travar aqui
			response = httpclient.execute(httpRequest);
		}
		catch (Exception e) {
			e.printStackTrace();
			ioException = e;
		}

		if(!esperando) {

			if(response != null) {
				release();
			}

			httpclient.getConnectionManager().shutdown();
		}
	}

	private void release() {

		HttpEntity entity = response.getEntity();

		if(entity != null) {

			InputStream contentIS = null;
			try {
				contentIS = entity.getContent();

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				for(int b = 0; (b = contentIS.read()) != -1;) {
					baos.write(b);
				}

				System.out.println("Requisição HTTP dita como morta se recuperou e retornou o seguinte conteudo:");
				System.out.println(new String(baos.toByteArray(), "UTF-8"));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			catch (RuntimeException e) {
				e.printStackTrace();
				httpRequest.abort();
			}
			finally {
				if(contentIS != null) {
					try {
						contentIS.close();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public HttpResponse getResponse() {
		return response;
	}

	public static class HttpTimeoutException extends Exception {

		private final long timeout;

		public HttpTimeoutException(long timeout) {
			this.timeout = timeout;
		}

		public long getTimeout() {
			return timeout;
		}
	}
}