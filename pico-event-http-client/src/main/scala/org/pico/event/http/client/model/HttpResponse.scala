package org.pico.event.http.client.model


import org.apache.{http => apache}

sealed trait HttpResponse

case class HttpOk(impl: apache.HttpResponse) extends HttpResponse

object HttpResponse {
  def from(that: apache.HttpResponse): HttpResponse = {
    println(s"--> response class: ${that.getClass}")

    HttpOk(that)
  }
}