package jetbrains.datalore.visualization.base.svgMapper.jfx

import javafx.scene.Parent
import javafx.scene.layout.Pane
import jetbrains.datalore.mapper.core.MappingContext
import jetbrains.datalore.mapper.core.Synchronizers
import jetbrains.datalore.visualization.base.svg.SvgSvgElement

class SvgSvgElementMapper(
    source: SvgSvgElement,
    peer: SvgJfxPeer
) : SvgElementMapper<SvgSvgElement, Parent>(source, createTargetContainer(), peer) {

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        super.registerSynchronizers(conf)

        val targetList = Utils.elementChildren(target)
        conf.add(
            Synchronizers.forObservableRole(
                this,
                source.children(),
                targetList,
                SvgNodeMapperFactory(peer)
            )
        )
    }

    override fun onAttach(ctx: MappingContext) {
        super.onAttach(ctx)

        if (!source.isAttached()) {
            throw IllegalStateException("Element must be attached")
        }
//        val peer = SvgAwtPeer()
        source.container().setPeer(peer)
    }

    override fun onDetach() {
        if (source.isAttached()) {
            source.container().setPeer(null)
        }
        super.onDetach()
    }

    companion object {
        private fun createTargetContainer(): Parent {
            val pane = Pane()
//            val pane = StackPane()
//            pane.alignmentProperty().set(Pos.TOP_LEFT)
                    
            // this makes lines sharp
            pane.scaleX = 1 / ScaleFactor.value
            pane.scaleY = 1 / ScaleFactor.value

            pane.centerShapeProperty().set(false)

//            pane.style = "-fx-border-color: red; -fx-border-width: 0 5; -fx-background-color: #2f4f4f"
//            pane.style = "-fx-border-width: 0"
//            pane.snapToPixelProperty().set(true)
//            pane.style = "-fx-padding: 0"

            return pane
        }
    }
}