package com.clackjones.connectivitymap.rest

import org.scalatra.ScalatraServlet
import org.scalatra.scalate.ScalateSupport

class ExampleServlet extends ScalatraServlet with ScalateSupport {

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }

  get("/hello-scalate") {
    <html>
      <body>
        <h1>Hello, scalate!</h1>
      </body>
    </html>
  }

}
