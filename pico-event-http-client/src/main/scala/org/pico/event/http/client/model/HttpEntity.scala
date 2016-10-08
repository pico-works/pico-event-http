package org.pico.event.http.client.model

import org.apache.http.client.entity.{UrlEncodedFormEntity => ApacheUrlEncodedFormEntity}
import org.apache.http.entity.{ContentType, StringEntity}
import org.apache.http.message.BasicNameValuePair
import org.apache.http.{HttpEntity => ApacheHttpEntity}

import scala.collection.JavaConverters._

sealed trait HttpEntity {
  def toApache: ApacheHttpEntity
}

case class ApplicationJsonEntity(json: String = "") extends HttpEntity {
  override def toApache: ApacheHttpEntity = {
    new StringEntity(json, ContentType.APPLICATION_JSON)
  }
}

case class UrlEncodedFormEntity(
    parameters: List[(String, String)] = List.empty,
    charset: Option[String] = None) extends HttpEntity {
  override def toApache: ApacheHttpEntity = {
    new ApacheUrlEncodedFormEntity(
      parameters.map { case (k, v) => new BasicNameValuePair(k, v) }.asJava,
      charset.orNull)
  }
}
