package org.eluder.coveralls.maven.plugin.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

@SuppressWarnings("deprecation")
class DefaultCharsetHttpEntity implements HttpEntity {
  private final HttpEntity target;

  DefaultCharsetHttpEntity(final HttpEntity target) {
    this.target = target;
  }

  @Override
  public boolean isRepeatable() {
    return target.isRepeatable();
  }

  @Override
  public boolean isChunked() {
    return target.isChunked();
  }

  @Override
  public long getContentLength() {
    return target.getContentLength();
  }

  @Override
  public Header getContentType() {
    return new DefaultCharsetContentTypeHeader(target.getContentType());
  }

  @Override
  public Header getContentEncoding() {
    return target.getContentEncoding();
  }

  @Override
  public InputStream getContent() throws IOException, UnsupportedOperationException {
    return target.getContent();
  }

  @Override
  public void writeTo(final OutputStream outStream) throws IOException {
    target.writeTo(outStream);
  }

  @Override
  public boolean isStreaming() {
    return target.isStreaming();
  }

  @Override
  public void consumeContent() throws IOException {
    target.consumeContent();
  }
}