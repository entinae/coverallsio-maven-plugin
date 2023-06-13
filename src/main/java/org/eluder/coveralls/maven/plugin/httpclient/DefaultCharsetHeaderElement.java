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

import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;

class DefaultCharsetHeaderElement implements HeaderElement {
  private final HeaderElement target;
  private final NameValuePair[] parameters;

  DefaultCharsetHeaderElement(final HeaderElement target) {
    this.target = target;
    final NameValuePair[] parameters = target.getParameters();
    for (final NameValuePair parameter: parameters) { // [A]
      if (parameter.getName().equalsIgnoreCase("charset")) {
        this.parameters = parameters;
        return;
      }
    }

    final int length = parameters.length;
    this.parameters = new NameValuePair[length + 1];
    System.arraycopy(parameters, 0, this.parameters, 0, length);
    this.parameters[length] = new NameValuePair() {
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
    for (final NameValuePair parameter : parameters) // [A]
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