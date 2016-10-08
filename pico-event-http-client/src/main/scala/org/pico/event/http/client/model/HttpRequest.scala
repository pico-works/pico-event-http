package org.pico.event.http.client.model

import org.apache.http.client.methods.{HttpGet => ApacheHttpGet, HttpPost => ApacheHttpPost, HttpUriRequest => ApacheHttpUriRequest}

sealed trait HttpRequest {
  def toApache: ApacheHttpUriRequest
}

case class HttpGet(url: String) extends HttpRequest {
  override def toApache: ApacheHttpUriRequest = new ApacheHttpGet(url)
}

case class HttpPost(url: String, entity: HttpEntity) extends HttpRequest {
  override def toApache: ApacheHttpUriRequest = {
    val that = new ApacheHttpPost(url)

    that.setEntity(entity.toApache)

    that
  }
}
