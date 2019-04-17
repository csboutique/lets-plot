package jetbrains.datalore.visualization.base.canvas.javaFx

import javafx.embed.swing.JFXPanel
import javafx.scene.image.WritableImage
import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.function.Function
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.datalore.visualization.base.canvas.ScaledCanvas
import jetbrains.datalore.visualization.base.canvas.javaFx.JavafxCanvasUtil.asyncTakeSnapshotImage
import javafx.scene.canvas.Canvas as NativeCanvas

internal class JavafxCanvas
private constructor(
        val nativeCanvas: NativeCanvas,
        size: Vector,
        pixelRatio: Double) :
        ScaledCanvas(
                JavafxContext2d(nativeCanvas.graphicsContext2D),
                size,
                pixelRatio) {

    companion object {
        init {
            //initialize Toolkit
            JFXPanel()
        }

        fun create(size: Vector, pixelRatio: Double): JavafxCanvas {
            return JavafxCanvas(NativeCanvas(), size, pixelRatio)
        }
    }

    init {
        nativeCanvas.width = size.x * pixelRatio
        nativeCanvas.height = size.y * pixelRatio
    }

    override fun takeSnapshot(): Async<Canvas.Snapshot> {
        return asyncTakeSnapshotImage(nativeCanvas).map(
                success = object : Function<WritableImage, Canvas.Snapshot> {
                    override fun apply(value: WritableImage): Canvas.Snapshot {
                        return JavafxSnapshot(value)
                    }

                }
        )
    }

    internal class JavafxSnapshot(val image: WritableImage) : Canvas.Snapshot

}