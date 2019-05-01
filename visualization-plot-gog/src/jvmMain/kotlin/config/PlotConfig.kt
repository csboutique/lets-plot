package jetbrains.datalore.visualization.plot.gog.config

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.base.gcommon.base.Strings
import jetbrains.datalore.visualization.plot.gog.config.Option.Meta
import jetbrains.datalore.visualization.plot.gog.config.Option.Meta.Kind
import jetbrains.datalore.visualization.plot.gog.config.Option.Plot.COORD
import jetbrains.datalore.visualization.plot.gog.config.Option.Plot.DATA
import jetbrains.datalore.visualization.plot.gog.config.Option.Plot.FACET
import jetbrains.datalore.visualization.plot.gog.config.Option.Plot.LAYERS
import jetbrains.datalore.visualization.plot.gog.config.Option.Plot.MAPPING
import jetbrains.datalore.visualization.plot.gog.config.Option.Plot.SCALES
import jetbrains.datalore.visualization.plot.gog.config.Option.Plot.TITLE
import jetbrains.datalore.visualization.plot.gog.config.Option.Plot.TITLE_TEXT
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrameUtil
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.plot.assemble.PlotFacets
import jetbrains.datalore.visualization.plot.gog.plot.assemble.TypedScaleProviderMap
import jetbrains.datalore.base.observable.collections.Collections.unmodifiableList
import java.util.Collections.singletonMap

abstract class PlotConfig protected constructor(opts: Map<String, Any>) : OptionsAccessor(opts, DEF_OPTIONS) {


    val layerConfigs: List<LayerConfig>
    val facets: PlotFacets
    val scaleProvidersMap: TypedScaleProviderMap

    protected var sharedData: DataFrame? = null
        private set

    internal val title: String
        get() = getMap(TITLE)[TITLE_TEXT] as String

    protected open val isClientSide: Boolean
        get() = false

    init {

        sharedData = ConfigUtil.createDataFrame(get(DATA))
        checkState(sharedData != null)

        val scaleConfigs = createScaleConfigs()
        scaleProvidersMap = PlotConfigUtil.createScaleProviders(scaleConfigs).unmodifiableCopy()
        layerConfigs = unmodifiableList(createLayerConfigs(sharedData, scaleProvidersMap))

        if (has(FACET)) {
            val facetOptions = getMap(FACET)
            val facetConfig = FacetConfig(facetOptions)
            val dataByLayer = ArrayList<DataFrame>()
            for (layerConfig in layerConfigs) {
                dataByLayer.add(layerConfig.combinedData)
            }
            facets = facetConfig.createFacets(dataByLayer)
        } else {
            facets = PlotFacets.undefined()
        }
    }

    internal fun createScaleConfigs(): List<ScaleConfig<Any>> {
        // merge options by 'aes'
        val mergedOpts = HashMap<Aes<*>, MutableMap<Any, Any>>()
        val scaleOptionsList = getList(SCALES)
        for (opts in scaleOptionsList) {
            val optsMap = opts as Map<*, *>
            val aes = ScaleConfig.aesOrFail(optsMap)
            if (!mergedOpts.containsKey(aes)) {
                mergedOpts[aes] = HashMap()
            }

            mergedOpts[aes]!!.putAll(optsMap as Map<Any, Any>)
        }

        val result = ArrayList<ScaleConfig<Any>>()
        for (scaleOptions in mergedOpts.values) {
            result.add(ScaleConfig(scaleOptions))
        }
        return result
    }

    private fun createLayerConfigs(sharedData: DataFrame?, scaleProviderByAes: TypedScaleProviderMap): List<LayerConfig> {

        val layerConfigs = ArrayList<LayerConfig>()
        val layerOptionsList = getList(LAYERS)
        for (layerOptions in layerOptionsList) {
            checkArgument(layerOptions is Map<*, *>, "Layer options: expected Map but was " + layerOptions!!::class.simpleName)
            val layerConfig = createLayerConfig(layerOptions as Map<*, *>,
                    sharedData,
                    getMap(MAPPING),
                    scaleProviderByAes)
            layerConfigs.add(layerConfig)
        }
        return layerConfigs
    }

    protected abstract fun createLayerConfig(layerOptions: Map<*, *>,
                                             sharedData: DataFrame?,
                                             plotMapping: Map<*, *>,
                                             scaleProviderByAes: TypedScaleProviderMap): LayerConfig


    protected fun replaceSharedData(plotData: DataFrame?) {
        checkState(!isClientSide)   // This class is immutable on client-side
        checkArgument(plotData != null)
        sharedData = plotData
        update(DATA, DataFrameUtil.toMap(plotData!!))
    }

    companion object {
        private val ERROR_MESSAGE = "__error_message"
        private val DEF_OPTIONS = mapOf(
                COORD to CoordProto.CARTESIAN
        )
        internal val PLOT_COMPUTATION_MESSAGES = "computation_messages"

        fun failure(e: Exception): Map<String, Any> {
            val message = e.message
            return singletonMap<String, Any>(PlotConfig.ERROR_MESSAGE,
                    if (Strings.isNullOrEmpty(message))
                        e::class.simpleName
                    else
                        "Error: $message")
        }

        fun assertPlotSpecOrErrorMessage(opts: Map<String, Any>) {
            val identified = isFailure(opts) ||
                    isPlotSpec(opts) ||
                    isGGBunchSpec(opts)

            if (!identified) {
                throw RuntimeException("Expected plot or error message spec: [" + opts.keys + "]")
            }
        }

        fun assertPlotSpec(opts: Map<String, Any>) {
            val identified = isPlotSpec(opts) || isGGBunchSpec(opts)

            if (!identified) {
                throw RuntimeException("Expected plot spec: [" + opts.keys + "]")
            }
        }

        fun isFailure(opts: Map<String, Any>): Boolean {
            return opts.containsKey(ERROR_MESSAGE)
        }

        fun getErrorMessage(opts: Map<String, Any>): String {
            return opts[ERROR_MESSAGE].toString()
        }

        fun isPlotSpec(opts: Map<*, *>): Boolean {
            return Kind.PLOT == specKind(opts)
        }

        fun isGGBunchSpec(opts: Map<*, *>): Boolean {
            return Kind.GG_BUNCH == specKind(opts)
        }

        fun specKind(opts: Map<*, *>): Any? {
            return opts.get(Meta.KIND)
        }
    }
}