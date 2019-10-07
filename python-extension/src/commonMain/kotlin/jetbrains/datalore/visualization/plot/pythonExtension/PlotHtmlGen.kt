package jetbrains.datalore.visualization.plot.pythonExtension

import jetbrains.datalore.base.jsObject.mapToJsObjectInitializer
import jetbrains.datalore.visualization.plot.server.config.PlotConfigServerSide

internal object PlotHtmlGen {
    fun applyToRawSpecs(plotSpec: MutableMap<String, Any>): String {
        // server-side transforms: statistics, sampling, etc.
        @Suppress("NAME_SHADOWING")
        val plotSpec = PlotConfigServerSide.processTransform(plotSpec)
        return applyToProcessedSpecs(plotSpec)
    }

    private fun applyToProcessedSpecs(plotSpec: MutableMap<String, Any>): String {
        val alphabet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val randomString: String = List(6) { alphabet.random() }.joinToString("")
        val plotOutputId = "plot_output_$randomString"
        val plotSpecJs = mapToJsObjectInitializer(plotSpec)
        return """
            <div id="$plotOutputId"></div>
            <script type="text/javascript">
                var plotSpec=$plotSpecJs

                var plotContainer = document.getElementById("$plotOutputId");
                DatalorePlot.jetbrains.datalore.visualization.plot.MonolithicJs.buildPlotFromProcessedSpecs(plotSpec, 440.0, 340.0, plotContainer);
            </script>
        """
    }
}