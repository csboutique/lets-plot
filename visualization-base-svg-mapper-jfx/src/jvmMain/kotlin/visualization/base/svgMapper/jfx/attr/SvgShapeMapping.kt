package jetbrains.datalore.visualization.base.svgMapper.jfx.attr

import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Shape
import jetbrains.datalore.visualization.base.svg.SvgColors
import jetbrains.datalore.visualization.base.svg.SvgConstants
import jetbrains.datalore.visualization.base.svg.SvgShape

internal abstract class SvgShapeMapping<TargetT : Shape> : SvgAttrMapping<TargetT>() {
    init {
//        target.smoothProperty().set(false)
//        target.strokeType = StrokeType.CENTERED
    }

    override fun setAttribute(target: TargetT, name: String, value: Any?) {
        when (name) {
            SvgShape.FILL.name -> setColor(value, fillGet(target), fillSet(target))
            SvgShape.FILL_OPACITY.name -> setOpacity(asDouble(value), fillGet(target), fillSet(target))
            SvgShape.STROKE.name -> setColor(value, strokeGet(target), strokeSet(target))
            SvgShape.STROKE_OPACITY.name -> setOpacity(asDouble(value), strokeGet(target), strokeSet(target))
            SvgShape.STROKE_WIDTH.name -> target.strokeWidth = asDouble(value)
            SvgConstants.SVG_STROKE_DASHARRAY_ATTRIBUTE -> {
                val strokeDashArray = (value as String).split(",").map { it.toDouble() }
                target.strokeDashArray.addAll(strokeDashArray)
            }
            else -> super.setAttribute(target, name, value)
        }
    }

    companion object {
        private val fillGet = { shape: Shape ->
            // This will reset fill color to black if color is defined via style
            { shape.fill as? Color ?: Color.BLACK }
        }
        private val fillSet = { shape: Shape -> { c: Color -> shape.fill = c } }
        private val strokeGet = { shape: Shape ->
            // This will reset stroke color to black if color is defined via style
            { shape.stroke as? Color ?: Color.BLACK }
        }
        private val strokeSet = { shape: Shape -> { c: Color -> shape.stroke = c } }


        /**
         * value : the color name (string) or SvgColor (jetbrains.datalore.visualization.base.svg)
         */
        private fun setColor(value: Any?, get: () -> Color, set: (Color) -> Unit) {
            if (value == null) return

            val svgColorString = value.toString()
            val newColor =
                if (svgColorString == SvgColors.NONE.toString()) {
                    Color(0.0, 0.0, 0.0, 0.0)
                } else {
                    val new = Paint.valueOf(svgColorString) as Color
                    val curr = get()
                    Color.color(new.red, new.green, new.blue, curr.opacity)
                }
            set(newColor)
        }

        private fun setOpacity(value: Double, get: () -> Color, set: (Color) -> Unit) {
            val c = get()
            set(Color.color(c.red, c.green, c.blue, value))
        }
    }
}