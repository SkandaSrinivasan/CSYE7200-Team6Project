import java.io._
import org.apache.commons._
import org.apache.http._
import org.apache.http.client._
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import java.util.ArrayList
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import com.google.gson.Gson

  object TryPost extends App {
    //  def routes[F[_] : Monad]: HttpRoutes[F] = {
    //    val dsl = Http4sDsl[F]
    //    import dsl._
    //    HttpRoutes.of[F] {
    //      case POST -> Root / "stream" => {
    //
    //      }
    //    }
    //
    //  }
    case class Person(firstName: String, lastName: String, age: Int)
    // create our object as a json string
    val spock = new Person("Leonard", "Nimoy", 82)
    val spockAsJson = new Gson().toJson(spock)

    // add name value pairs to a post object
    val post = new HttpPost("http://localhost:8080/posttest")
    val nameValuePairs = new ArrayList[NameValuePair]()
    nameValuePairs.add(new BasicNameValuePair("JSON", spockAsJson))
    post.setEntity(new UrlEncodedFormEntity(nameValuePairs))

    // send the post request
    val client = new DefaultHttpClient
    val response = client.execute(post)
    println("--- HEADERS ---")
    response.getAllHeaders.foreach(arg => println(arg))
  }
