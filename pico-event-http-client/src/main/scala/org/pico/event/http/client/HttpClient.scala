package org.pico.event.http.client

import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.impl.nio.client.{CloseableHttpAsyncClient, HttpAsyncClients}
import org.apache.{http => apache}
import org.pico.disposal.SimpleDisposer
import org.pico.disposal.std.autoCloseable._
import org.pico.event.SinkSource
import org.pico.event.http.client.internal.Async
import org.pico.event.http.client.model.{HttpRequest, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

case class HttpClient(impl: CloseableHttpAsyncClient) extends SimpleDisposer {
  this.disposes(impl)

  def sinkSource(implicit ec: ExecutionContext): SinkSource[HttpRequest, Future[HttpResponse]] = {
    SinkSource[HttpRequest, Future[HttpResponse]] { request =>
      execute(request.toApache).map(HttpResponse.from)
    }
  }

  def execute(request: HttpUriRequest): Future[apache.HttpResponse] = {
    Async.handle[apache.HttpResponse] { callback =>
      impl.execute(request, callback)
    }
  }
}

object HttpClient {
  def apply(): HttpClient = HttpClient(HttpAsyncClients.createDefault())
}
