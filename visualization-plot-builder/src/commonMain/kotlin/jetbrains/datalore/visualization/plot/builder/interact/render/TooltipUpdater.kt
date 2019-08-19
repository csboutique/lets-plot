package jetbrains.datalore.visualization.plot.builder.interact.render

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.plot.builder.tooltip.TooltipBox

internal class TooltipUpdater(private val tooltipLayer: SvgGElement) {

    private val viewModels = HashSet<TooltipViewModel>()
    private val views = HashMap<TooltipViewModel, TooltipBox>()

    fun updateTooltips(tooltipEntries: Collection<TooltipViewModel>) {
        viewModels.clear()
        viewModels.addAll(tooltipEntries)

        views.values
            .forEach { view -> tooltipLayer.children().remove(view.rootGroup) }
            .also { views.clear() }

        viewModels.forEach { vm ->
            views[vm] = TooltipBox().apply {
                tooltipLayer.children().add(rootGroup) // have to be in DOM to calculate bbox on next line
                with(vm) {
                    setContent(fill, text, fontSize)
                    setPosition(tooltipCoord, stemCoord, orientation(this))
                }
            }
        }
    }

    private fun orientation(entry: TooltipViewModel): TooltipBox.Orientation =
        when {
            entry.orientation === TooltipOrientation.HORIZONTAL -> TooltipBox.Orientation.HORIZONTAL
            else -> TooltipBox.Orientation.VERTICAL
        }

    companion object {
        val IGNORED_COLOR = Color.BLACK
    }
}
