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
  var oneSize = -1
  var zeroSize = -1
  var Component = FunctionalComponent[Unit] {
    case () =>
      var data = js.Array[Data](
        PartialPlotDataAutobinx()
          .setType(PlotType.bar)
          .setXVarargs(0, 1)
          .setYVarargs(zeroSize, oneSize)
      )

      ReactPlotlyDotjs(data = data, layout = PartialLayout().setWidth(500).setHeight(500).setTitle("Sentiment Data")).debug(true)
  }

  def processData(requestData: String): Unit = {
    val patZero = "0.0".r
    val patOnes = "1.0".r
    val zeroUnfiltered = patZero.findAllMatchIn(requestData).map(_.toString.trim).toArray
    val oneUnfiltered = patOnes.findAllMatchIn(requestData).map(_.toString.trim).toArray
    val zero = zeroUnfiltered.filter(_ != "000")
    val one = oneUnfiltered.filter(_ != "100")
    oneSize = one.size
    zeroSize = zero.size
  }

  def getData(): Unit = {
   val xhr = new dom.XMLHttpRequest()
      xhr.open("GET", "http://localhost:9000/tweets")
      xhr.onload = { (e: dom.Event) =>
        if (xhr.status == 200) {
          val r = js.JSON.parse(xhr.responseText)
          val requestData = js.JSON.stringify(r)
          processData(requestData)
          println("Zerosize" + zeroSize)
          println("Onesize" + oneSize)
        }
      }
      xhr.send()
  }

  def main(argv: Array[String]): Unit =
    getData()
    ReactDOM.render(Component(()), document.body)
}
