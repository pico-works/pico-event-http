package org.pico.event.http.client

import java.nio.CharBuffer
import java.util.concurrent.CountDownLatch

import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.concurrent.FutureCallback
import org.apache.http.nio.IOControl
import org.apache.http.nio.client.methods.{AsyncCharConsumer, HttpAsyncMethods}
import org.apache.http.protocol.HttpContext
import org.pico.disposal.Auto
import org.pico.disposal.std.autoCloseable._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.control.NonFatal

object Main {
  def main(args: Array[String]): Unit = {
    for (httpclient <- Auto(HttpClient())) {
      // Start the client
      httpclient.impl.start()

      // One most likely would want to use a callback for operation result
      val request2 = new HttpGet("http://www.apache.org/")

      try {
        val response2 = Await.result(httpclient.execute(request2), 2.second)

        println(request2.getRequestLine + "->" + response2.getStatusLine)
      } catch {
        case e: CancelledException => println(request2.getRequestLine + " cancelled")
        case NonFatal(e) => println(request2.getRequestLine + "->" + e)
      }

      // In real world one most likely would also want to stream
      // request and response body content
      val latch2 = new CountDownLatch(1)
      val request3 = new HttpGet("http://www.apache.org/")
      val producer3 = HttpAsyncMethods.create(request3)
      val consumer3 = new AsyncCharConsumer[HttpResponse] {
        var response: HttpResponse = null

        override def onResponseReceived(response: HttpResponse): Unit = this.response = response

        override def onCharReceived(buf: CharBuffer, ioctrl: IOControl): Unit = {
          // Do something useful
        }

        override def releaseResources(): Unit = ()

        override def buildResult(context: HttpContext): HttpResponse = this.response
      }

      httpclient.impl.execute(producer3, consumer3, new FutureCallback[HttpResponse]() {
        def completed(response3: HttpResponse): Unit = {
          latch2.countDown()
          println(request2.getRequestLine + "->" + response3.getStatusLine)
        }

        def failed(ex: Exception): Unit = {
          latch2.countDown()
          println(request2.getRequestLine + "->" + ex)
        }

        def cancelled(): Unit = {
          latch2.countDown()
          println(request2.getRequestLine + " cancelled")
        }
      })

      latch2.await()
    }
  }
}
