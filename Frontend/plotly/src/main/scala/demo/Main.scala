package demo

import org.scalajs.dom.document
import org.scalajs.dom
import slinky.core.FunctionalComponent
import slinky.web.ReactDOM
import typings.plotlyJs.anon.{PartialPlotDataAutobinx, PartialPlotMarkerAutocolorscale}
import typings.plotlyJs.mod.{Data, PlotType}
import typings.plotlyJs.plotlyJsStrings
import typings.reactPlotlyJs.anon.PartialLayout
import typings.reactPlotlyJs.components.ReactPlotlyDotjs

import scala.scalajs.js

object Main {
  var jsonData = "Hello World"
  val Component = FunctionalComponent[Unit] {
    case () =>
      println(jsonData)
      val data = js.Array[Data](
        PartialPlotDataAutobinx()
          .setXVarargs(1, 2, 3)
          .setYVarargs(2, 6, 3)
          .setType(PlotType.scatter)
          .setMode(plotlyJsStrings.linesPlussignmarkers)
          .setMarker(
            PartialPlotMarkerAutocolorscale()
              .setColor("red")
          ),
        PartialPlotDataAutobinx()
          .setType(PlotType.bar)
          .setXVarargs(1, 2, 3)
          .setYVarargs(2, 5, 3)
      )

      ReactPlotlyDotjs(data = data, layout = PartialLayout().setWidth(500).setHeight(500).setTitle("A Fancy Plot")).debug(true)
  }

  def getData(): Unit = {
   val xhr = new dom.XMLHttpRequest()
      xhr.open("GET", "http://localhost:9000/tweets")
      xhr.onload = { (e: dom.Event) =>
        if (xhr.status == 200) {
          val r = js.JSON.parse(xhr.responseText)
          jsonData = js.JSON.stringify(r)
        }
      }
      xhr.send()
  }

  def main(argv: Array[String]): Unit =
    getData()
    ReactDOM.render(Component(()), document.body)
}
