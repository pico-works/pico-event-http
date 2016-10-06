package org.pico.event.http.client.internal

import org.apache.http.concurrent.FutureCallback
import org.pico.event.http.client.CancelledException

import scala.concurrent.{Future, Promise}

object Async {
  def handle[A](f: FutureCallback[A] => Unit): Future[A] = {
    val p = Promise[A]

    val callback = new FutureCallback[A] {
      override def cancelled(): Unit = p.failure(new CancelledException)
      override def completed(result: A): Unit = p.success(result)
      override def failed(ex: Exception): Unit = p.failure(ex)
    }

    f(callback)

    p.future
  }
}
