package jetbrains.datalore.visualization.plot.builder.sampling.method

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.visualization.plot.base.DataFrame
import jetbrains.datalore.visualization.plot.builder.sampling.Sampling

internal abstract class SamplingBase(val sampleSize: Int) : Sampling {

    init {
        checkState(this.sampleSize > 0, "Sample size must be greater than zero, but was: " + this.sampleSize)
    }

    open fun isApplicable(population: DataFrame): Boolean {
        return population.rowCount() > sampleSize
    }
}