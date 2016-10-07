package org.pico.event.http.client.model

sealed trait HttpResponse

case class HttpOk() extends HttpResponse
