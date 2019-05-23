package jetbrains.datalore.visualization.plot.builder.interact.loc

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.base.interact.GeomTarget
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetCollector.TooltipParams
import jetbrains.datalore.visualization.plot.base.interact.HitShape
import jetbrains.datalore.visualization.plot.base.interact.HitShape.Kind.*
import jetbrains.datalore.visualization.plot.base.interact.TipLayoutHint

class TargetPrototype(
        internal val hitShape: HitShape,
        internal val indexMapper: (Int) -> Int,
        private val tooltipParams: TooltipParams) {

    internal fun crateGeomTarget(hitCoord: DoubleVector, hitIndex: Int): GeomTarget {
        return GeomTarget(
                hitIndex,
                createTipLayoutHint(hitCoord, hitShape, tooltipParams.getColor()),
                tooltipParams.getTipLayoutHints()
        )
    }

    companion object {
        fun createTipLayoutHint(hitCoord: DoubleVector, hitShape: HitShape, fill: Color): TipLayoutHint {
            return when (hitShape.kind) {
                RECT -> {
                    val radius = hitShape.rect.width / 2
                    TipLayoutHint.horizontalTooltip(hitCoord, radius, fill)
                }

                POINT -> TipLayoutHint.verticalTooltip(hitCoord, hitShape.point.radius, fill)

                PATH -> TipLayoutHint.horizontalTooltip(hitCoord, 0.0, fill)

                POLYGON -> TipLayoutHint.cursorTooltip(hitCoord, fill)
            }
        }
    }
}