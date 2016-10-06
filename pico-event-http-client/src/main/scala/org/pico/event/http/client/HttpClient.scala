package org.pico.event.http.client

import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.impl.nio.client.{CloseableHttpAsyncClient, HttpAsyncClients}
import org.pico.disposal.SimpleDisposer
import org.pico.disposal.std.autoCloseable._
import org.pico.event.http.client.internal.Async

import scala.concurrent.Future

case class HttpClient(impl: CloseableHttpAsyncClient) extends SimpleDisposer {
  this.disposes(impl)

  def execute(request: HttpUriRequest): Future[HttpResponse] = {
    Async.handle[HttpResponse] { callback =>
      impl.execute(request, callback)
    }
  }
}

object HttpClient {
  def apply(): HttpClient = HttpClient(HttpAsyncClients.createDefault())
}


