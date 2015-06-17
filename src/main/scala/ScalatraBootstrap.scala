import com.clackjones.connectivitymap.rest.ExampleServlet
import org.scalatra.LifeCycle
import javax.servlet.ServletContext


class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {

    // mount servlets like this:
    context mount (new ExampleServlet, "/*")
  }
}
