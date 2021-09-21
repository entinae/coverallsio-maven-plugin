package org.eluder.coveralls.maven.plugin.httpclient;

import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;

class DefaultCharsetHeaderElement implements HeaderElement {
  private final HeaderElement target;
  private final NameValuePair[] parameters;

  DefaultCharsetHeaderElement(final HeaderElement target) {
    this.target = target;
    final NameValuePair[] parameters = target.getParameters();
    for (final NameValuePair parameter: parameters) {
      if (parameter.getName().equalsIgnoreCase("charset")) {
        this.parameters = parameters;
        return;
      }
    }

    this.parameters = new NameValuePair[parameters.length + 1];
    System.arraycopy(parameters, 0, this.parameters, 0, parameters.length);
    this.parameters[parameters.length] = new NameValuePair() {
      @Override
      public String getName() {
        return "charset";
      }

      @Override
      public String getValue() {
        return "ISO-8859-1";
      }
    };
  }

  @Override
  public String getName() {
    return this.target.getName();
  }

  @Override
  public String getValue() {
    return this.target.getValue();
  }

  @Override
  public NameValuePair[] getParameters() {
    return parameters;
  }

  @Override
  public NameValuePair getParameterByName(final String name) {
    for (final NameValuePair parameter : parameters)
      if (parameter.getName().equalsIgnoreCase(name))
        return parameter;

    return null;
  }

  @Override
  public int getParameterCount() {
    return parameters.length;
  }

  @Override
  public NameValuePair getParameter(final int index) {
    return parameters[index];
  }
}