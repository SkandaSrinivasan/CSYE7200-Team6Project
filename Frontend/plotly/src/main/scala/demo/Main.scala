package demo

import slinky.core.facade.Hooks._
import org.scalajs.dom.document
import org.scalajs.dom
import slinky.core.FunctionalComponent
import slinky.web.ReactDOM
import slinky.web.html.{br, div}
import typings.plotlyJs.anon.{PartialPlotDataAutobinx, PartialPlotMarkerAutocolorscale}
import typings.plotlyJs.mod.{Data, PlotType}
import typings.plotlyJs.plotlyJsStrings
import typings.reactPlotlyJs.anon.PartialLayout
import typings.reactPlotlyJs.components.ReactPlotlyDotjs

import scala.scalajs.js

object Main {
  var Component: FunctionalComponent[Unit] = FunctionalComponent[Unit] {
    case () =>
      val (oneSize, setOneSize) = useState(-1)
      val (zeroSize, setZeroSize) = useState(0)
      println(oneSize, zeroSize)

      var data = js.Array[Data](
        PartialPlotDataAutobinx()
          .setType(PlotType.bar)
          .setXVarargs("positive", "negative")
          .setYVarargs(oneSize, zeroSize)
      )

      val data1 = js.Array[Data](
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
      useEffect(
        () => {
          val xhr = new dom.XMLHttpRequest()
          xhr.open("GET", "http://localhost:9000/tweets")
          xhr.onload = { (e: dom.Event) =>
            if (xhr.status == 200) {
              val r = js.JSON.parse(xhr.responseText)
              val requestData = js.JSON.stringify(r)
              val patZero = "0.0".r
              val patOnes = "1.0".r
              val zeroUnfiltered =
                patZero.findAllMatchIn(requestData).map(_.toString.trim).toArray
              val oneUnfiltered =
                patOnes.findAllMatchIn(requestData).map(_.toString.trim).toArray
              val zero = zeroUnfiltered.filter(_ != "000")
              val one = oneUnfiltered.filter(_ != "100")
              setOneSize(one.length)
              setZeroSize(zero.length)
            }
          }
          xhr.send()
        },
        Seq()
      )

          ReactPlotlyDotjs(
            data = data,
            layout = PartialLayout()
              .setWidth(500)
              .setHeight(500)
              .setTitle("Sentiment sdas"))

//          ReactPlotlyDotjs(
//            data = data,
//            layout = PartialLayout()
//              .setWidth(1000)
//              .setHeight(500)
//              .setTitle("Sentiment Data")
//
//      )


  }

  def main(argv: Array[String]): Unit = {
    ReactDOM.render(Component(()), document.getElementById("container"))

  }
}
