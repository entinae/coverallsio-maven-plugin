package org.eluder.coveralls.maven.plugin.httpclient;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;

class DefaultCharsetContentTypeHeader implements Header {
  private final Header target;

  DefaultCharsetContentTypeHeader(final Header target) {
    this.target = target;
  }

  @Override
  public String getName() {
    return target.getName();
  }

  @Override
  public String getValue() {
    return target.getValue();
  }

  @Override
  public HeaderElement[] getElements() throws ParseException {
    return new HeaderElement[] {new DefaultCharsetHeaderElement(target.getElements()[0])};
  }
}