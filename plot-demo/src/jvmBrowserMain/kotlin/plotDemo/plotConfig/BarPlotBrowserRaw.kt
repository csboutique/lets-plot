/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.plotConfig

import jetbrains.datalore.plotDemo.model.plotConfig.BarPlot

fun main() {
    with(BarPlot()) {
        @Suppress("UNCHECKED_CAST")
        (PlotConfigBrowserDemoUtil.show(
            "Bar plot",
            plotSpecList() as List<MutableMap<String, Any>>,
            demoComponentSize,
            applyBackendTransform = false
        ))
    }
}
