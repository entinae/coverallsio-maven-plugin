/* Copyright (c) 2019 Seva Safris
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