package org.eluder.coveralls.maven.plugin.httpclient;

import org.apache.maven.settings.Proxy;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CoverallsIoClient extends CoverallsClient {
  public CoverallsIoClient(final String coverallsUrl, final Proxy proxy) {
    super(coverallsUrl, new CoverallsIoHttpClient(new HttpClientFactory(coverallsUrl).proxy(proxy).create()), new ObjectMapper());
  }
}
