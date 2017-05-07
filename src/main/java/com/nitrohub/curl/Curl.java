package com.nitrohub.curl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Curl {

	public static void main(String... args) {
		try {
			System.setProperty("javax.net.debug", "ssl");
			ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
			Command command = (Command) context.getBean("command");
			PoolingHttpClientConnectionManager clientConnectionManager = new PoolingHttpClientConnectionManager(getRegistry(command.getTlsProtocols()));
			clientConnectionManager.setMaxTotal(100);
			clientConnectionManager.setDefaultMaxPerRoute(20);
			String authHeader = null;
			if(command.getAuthUse().equalsIgnoreCase("Yes")) {
				String auth = command.getAuthUser() + ":" + command.getAuthPass();
				byte[] encodedAuth = Base64.encodeBase64(
						auth.getBytes(Charset.forName("ISO-8859-1")));
				authHeader = "Basic " + new String(encodedAuth);
			}
			HttpClient client = null;
			if(command.getProxyUse().equalsIgnoreCase("Yes")) {
				HttpHost proxy = new HttpHost(command.getProxyHost(), command.getProxyPort(), HttpHost.DEFAULT_SCHEME_NAME);
				Credentials credentials = new UsernamePasswordCredentials(command.getProxyUser(),command.getProxyPass());
				AuthScope authScope = new AuthScope(command.getProxyHost(), command.getProxyPort());
				CredentialsProvider credsProvider = new BasicCredentialsProvider();
				credsProvider.setCredentials(authScope, credentials);
				DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
				client = HttpClients.custom().setConnectionManager(clientConnectionManager).setRoutePlanner(routePlanner).setDefaultCredentialsProvider(credsProvider).build();
			} else
				client = HttpClients.custom().setConnectionManager(clientConnectionManager).build();
			HttpGet httpGet = null;
			HttpPost httpPost = null;
			if(command.getCallMethod() == null) command.setCallMethod("GET");
			JsonParser  parser = null;
			if(command.getCallHeader() != null) {
				JsonFactory factory = new JsonFactory();
				parser  = factory.createJsonParser(command.getCallHeader());
			}
			if(command.getCallMethod().equalsIgnoreCase("GET")) {
				httpGet = new HttpGet(command.getCallUrl());
				if(authHeader != null)
					httpGet.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
				while(parser != null && !parser.isClosed()) {
					JsonToken token = parser.nextToken();
					if(token == null) break;
					if(JsonToken.FIELD_NAME.equals(token)) {
						String headerName = parser.getCurrentName();
						token = parser.nextToken();
						String headerValue = parser.getText();
						System.out.println("header name : " + headerName + " header value : " + headerValue);
						httpGet.setHeader(headerName, headerValue);
					}
				}
			} else if(command.getCallMethod().equalsIgnoreCase("POST")) {
				httpPost = new HttpPost(command.getCallUrl());
				if(authHeader != null)
					httpPost.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
				if(command.getCallEntity() != null) {
					StringEntity entity = new StringEntity(command.getCallEntity());
					httpPost.setEntity(entity);
				}
				while(parser != null && !parser.isClosed()) {
					JsonToken token = parser.nextToken();
					if(token == null) break;
					if(JsonToken.FIELD_NAME.equals(token)) {
						String headerName = parser.getCurrentName();
						token = parser.nextToken();
						String headerValue = parser.getText();
						System.out.println("header name : " + headerName + " header value : " + headerValue);
						httpPost.setHeader(headerName, headerValue);
					}
				}
			}
			CloseableHttpResponse response = (CloseableHttpResponse) client.execute(httpGet != null ? httpGet : (httpPost != null ? httpPost : null));
			System.out.println("Response phrase : " + response.getStatusLine().getReasonPhrase());
			System.out.println("Response code : " + response.getStatusLine().getStatusCode());
			for(Header header : response.getAllHeaders()) {
				System.out.println(header.getName() + " : " + header.getValue());
			}
			InputStream stream = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line = null;
			do {
				line = reader.readLine();
				if(line != null)
					System.out.println(line);
			} while(line != null);
		} catch (KeyManagementException | NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
	}

	private static Registry<ConnectionSocketFactory> getRegistry(String tlsProtocols) throws KeyManagementException, NoSuchAlgorithmException {
		SSLContext sslContext = SSLContexts.custom().build();
		String[] tlsProtocolArray = tlsProtocols != null ? tlsProtocols.split(",") : new String[]{"TLSv1.2"};
		SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext,
				tlsProtocolArray, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
		return RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory())
				.register("https", sslConnectionSocketFactory)
				.build();
	}
}
