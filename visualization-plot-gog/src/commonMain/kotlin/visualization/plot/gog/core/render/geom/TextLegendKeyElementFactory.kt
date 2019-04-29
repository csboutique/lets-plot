package jetbrains.datalore.visualization.plot.gog.core.render.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.plot.gog.core.render.AestheticsUtil
import jetbrains.datalore.visualization.plot.gog.core.render.DataPointAesthetics
import jetbrains.datalore.visualization.plot.gog.core.render.LegendKeyElementFactory
import jetbrains.datalore.visualization.plot.gog.core.render.geom.util.GeomHelper
import jetbrains.datalore.visualization.plot.gog.core.render.svg.TextLabel

internal class TextLegendKeyElementFactory : LegendKeyElementFactory {

    override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
        val label = TextLabel("a")
        GeomHelper.decorate(label, p)
        label.setHorizontalAnchor(TextLabel.HorizontalAnchor.MIDDLE)
        label.setVerticalAnchor(TextLabel.VerticalAnchor.CENTER)
        label.moveTo(size.x / 2, size.y / 2)
        val g = SvgGElement()
        g.children().add(label.rootGroup)
        return g
    }

    override fun minimumKeySize(p: DataPointAesthetics): DoubleVector {
        val strokeWidth = AestheticsUtil.strokeWidth(p)
        return DoubleVector(4.0, strokeWidth + 4)
    }
}
