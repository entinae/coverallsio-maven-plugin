/* Copyright (c) 2019 ENTINAE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * You should have received a copy of The MIT License (MIT) along with this
 * program. If not, see <http://opensource.org/licenses/MIT/>.
 */

package org.eluder.coveralls.maven.plugin.httpclient;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.ContentType;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

@SuppressWarnings("deprecation")
class CoverallsIoHttpClient implements HttpClient {
  private final HttpClient target;

  CoverallsIoHttpClient(final HttpClient target) {
    this.target = target;
  }

  @Override
  public HttpParams getParams() {
    return this.target.getParams();
  }

  @Override
  public ClientConnectionManager getConnectionManager() {
    return this.target.getConnectionManager();
  }

  @Override
  public HttpResponse execute(final HttpUriRequest request) throws IOException, ClientProtocolException {
    final HttpResponse response = target.execute(request);
    final HttpEntity entity = response.getEntity();
    final ContentType contentType = ContentType.getOrDefault(entity);
    if (contentType.getCharset() != null)
      return response;

    EntityUtils.updateEntity(response, new DefaultCharsetHttpEntity(entity));
    return response;
  }

  @Override
  public HttpResponse execute(final HttpUriRequest request, final HttpContext context) throws IOException, ClientProtocolException {
    return this.target.execute(request, context);
  }

  @Override
  public HttpResponse execute(final HttpHost target, final HttpRequest request) throws IOException, ClientProtocolException {
    return this.target.execute(target, request);
  }

  @Override
  public HttpResponse execute(final HttpHost target, final HttpRequest request, final HttpContext context) throws IOException, ClientProtocolException {
    return this.target.execute(target, request, context);
  }

  @Override
  public <T>T execute(final HttpUriRequest request, final ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
    return this.target.execute(request, responseHandler);
  }

  @Override
  public <T>T execute(final HttpUriRequest request, final ResponseHandler<? extends T> responseHandler, final HttpContext context) throws IOException, ClientProtocolException {
    return this.target.execute(request, responseHandler, context);
  }

  @Override
  public <T>T execute(final HttpHost target, final HttpRequest request, final ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
    return this.target.execute(target, request, responseHandler);
  }

  @Override
  public <T>T execute(final HttpHost target, final HttpRequest request, final ResponseHandler<? extends T> responseHandler, final HttpContext context) throws IOException, ClientProtocolException {
    return this.target.execute(target, request, responseHandler, context);
  }
}