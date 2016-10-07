package org.pico.event.http.client.model

import org.apache.http.client.methods.{HttpGet => ApacheHttpGet, HttpUriRequest => ApacheHttpUriRequest}

sealed trait HttpRequest {
  def toApache: ApacheHttpUriRequest
}

case class HttpGet(url: String) extends HttpRequest {
  override def toApache: ApacheHttpUriRequest = new ApacheHttpGet(url)
}
