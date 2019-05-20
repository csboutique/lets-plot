package jetbrains.datalore.visualization.plot.base.data

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.common.geometry.Utils
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions

class RingAssertion internal constructor(ring: List<DoubleVector>) : AbstractAssert<RingAssertion, List<DoubleVector>>(ring, RingAssertion::class.java) {

    val isClosed: RingAssertion
        get() {
            Assertions.assertThat(actual[0]).isEqualTo(actual[actual.size - 1])
            return this
        }

    fun hasSize(expected: Int): RingAssertion {
        Assertions.assertThat(actual).hasSize(expected)
        return this
    }

    fun hasArea(expected: Double): RingAssertion {
        return hasArea(expected, 0.001)
    }

    private fun hasArea(expected: Double, epsilon: Double): RingAssertion {
        Assertions.assertThat(Utils.calculateArea(actual)).isEqualTo(expected, Assertions.offset(epsilon))
        return this
    }

    companion object {

        fun assertThatRing(ring: List<DoubleVector>): RingAssertion {
            return RingAssertion(ring)
        }
    }
}
