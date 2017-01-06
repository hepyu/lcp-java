package com.open.lcp.framework.core.configuration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfig {

//	private static final Log logger = LogFactory.getLog(HttpClientConfig.class);
//
//	@Bean
//	public HttpClientConnectionManager httpConnManager() {
//
//		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
//		// Increase max total connection to 800
//		cm.setMaxTotal(1500);
//		// Increase default max connection per route to 100
//		cm.setDefaultMaxPerRoute(500);
//
//		return cm;
//	}
//
//	@Bean
//	public CloseableHttpClient httpClient() {
//
//		HttpClientConnectionManager cm = httpConnManager();
//
//		ConnectionKeepAliveStrategy keepAliveStrat = new DefaultConnectionKeepAliveStrategy() {
//
//			@Override
//			public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
//
//				long keepAlive = super.getKeepAliveDuration(response, context);
//				if (keepAlive == -1) {
//					// Keep connections alive 5 seconds if a keep-alive value
//					// has not be explicitly set by the server
//					keepAlive = 5000;
//				}
//				return keepAlive;
//			}
//
//		};
//
//		return HttpClients.custom().setConnectionManager(cm).setKeepAliveStrategy(keepAliveStrat)
//				.addInterceptorFirst(new HttpResponseInterceptor() {
//					// 支持gzip
//					public void process(final HttpResponse response, final HttpContext context)
//							throws HttpException, IOException {
//						HttpEntity entity = response.getEntity();
//						if (entity != null) {
//							Header ceheader = entity.getContentEncoding();
//							if (ceheader != null) {
//								HeaderElement[] codecs = ceheader.getElements();
//								for (int i = 0; i < codecs.length; i++) {
//									if (codecs[i].getName().equalsIgnoreCase("gzip")) {
//										response.setEntity(new GzipDecompressingEntity(response.getEntity()));
//										return;
//									}
//								}
//							}
//						}
//					}
//
//				}).build();
//
//	}
//
//	@PostConstruct
//	public void postConstruct() {
//
//		final HttpClientConnectionManager cm = httpConnManager();
//		final CloseableHttpClient httpClient = httpClient();
//
//		final IdleConnectionMonitorThread thread = new IdleConnectionMonitorThread(cm);
//		thread.start();
//
//		Runtime.getRuntime().addShutdownHook(new Thread() {
//			@Override
//			public void run() {
//
//				if (thread != null)
//					thread.shutdown();
//
//				if (cm != null)
//					cm.shutdown();
//
//				if (httpClient != null)
//					try {
//						httpClient.close();
//					} catch (IOException e) {
//						logger.warn("ShutdownHook", e);
//					}
//
//			}
//		});
//	}
//
//	public static class IdleConnectionMonitorThread extends Thread {
//
//		private final HttpClientConnectionManager connMgr;
//		private volatile boolean shutdown;
//		private final Object lock = new Object();
//
//		public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
//			super("IdleConnectionMonitorThread");
//			this.connMgr = connMgr;
//		}
//
//		@Override
//		public void run() {
//			try {
//				while (!shutdown) {
//					synchronized (lock) {
//						lock.wait(5000);
//						// Close expired connections
//						connMgr.closeExpiredConnections();
//						// Optionally, close connections
//						// that have been idle longer than 30 sec
//						connMgr.closeIdleConnections(10, TimeUnit.SECONDS);
//
//						// System.out.println("close idle
//						// conn................");
//					}
//				}
//			} catch (InterruptedException ex) {
//				logger.warn("IdleConnectionMonitorThread", ex);
//			}
//		}
//
//		public void shutdown() {
//			shutdown = true;
//			synchronized (lock) {
//				lock.notifyAll();
//			}
//		}
//
//	}
}
