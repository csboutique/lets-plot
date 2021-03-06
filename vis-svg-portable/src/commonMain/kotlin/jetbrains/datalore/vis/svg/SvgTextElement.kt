/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svg

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.WritableProperty
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.svg.SvgTextContent.Companion.FILL
import jetbrains.datalore.vis.svg.SvgTextContent.Companion.FILL_OPACITY
import jetbrains.datalore.vis.svg.SvgTextContent.Companion.STROKE
import jetbrains.datalore.vis.svg.SvgTextContent.Companion.STROKE_OPACITY
import jetbrains.datalore.vis.svg.SvgTextContent.Companion.STROKE_WIDTH
import jetbrains.datalore.vis.svg.SvgTextContent.Companion.TEXT_ANCHOR
import jetbrains.datalore.vis.svg.SvgTextContent.Companion.TEXT_DY
import jetbrains.datalore.vis.svg.SvgTransformable.Companion.TRANSFORM

class SvgTextElement() : SvgGraphicsElement(), SvgTransformable,
    SvgTextContent {

    companion object {
        val X: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("x")
        val Y: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("y")
    }

    override val elementName = "text"

    override val computedTextLength: Double
        get() = container().getPeer()!!.getComputedTextLength(this)

    override val bBox: DoubleRectangle
        get() = container().getPeer()!!.getBBox(this)

    constructor(content: String) : this() {

        setTextNode(content)
    }

    constructor(x: Double, y: Double, content: String) : this() {

        setAttribute(X, x)
        setAttribute(Y, y)
        setTextNode(content)
    }

    fun x(): Property<Double?> {
        return getAttribute(X)
    }

    fun y(): Property<Double?> {
        return getAttribute(Y)
    }

    override fun transform(): Property<SvgTransform?> {
        return getAttribute(TRANSFORM)
    }

    fun setTextNode(text: String) {
        children().clear()
        addTextNode(text)
    }

    fun addTextNode(text: String) {
        val textNode = SvgTextNode(text)
        children().add(textNode)
    }

    fun setTSpan(tspan: SvgTSpanElement) {
        children().clear()
        addTSpan(tspan)
    }

    fun setTSpan(text: String) {
        children().clear()
        addTSpan(text)
    }

    fun addTSpan(tspan: SvgTSpanElement) {
        children().add(tspan)
    }

    fun addTSpan(text: String) {
        children().add(SvgTSpanElement(text))
    }

    override fun fill(): Property<SvgColor?> {
        return getAttribute(FILL)
    }

    override fun fillColor(): WritableProperty<Color?> {
        return SvgUtils.colorAttributeTransform(fill(), fillOpacity())
    }

    override fun fillOpacity(): Property<Double?> {
        return getAttribute(FILL_OPACITY)
    }

    override fun stroke(): Property<SvgColor?> {
        return getAttribute(STROKE)
    }

    override fun strokeColor(): WritableProperty<Color?> {
        return SvgUtils.colorAttributeTransform(stroke(), strokeOpacity())
    }

    override fun strokeOpacity(): Property<Double?> {
        return getAttribute(STROKE_OPACITY)
    }

    override fun strokeWidth(): Property<Double?> {
        return getAttribute(STROKE_WIDTH)
    }

    override fun textAnchor(): Property<String?> {
        return getAttribute(TEXT_ANCHOR)
    }

    override fun textDy(): Property<String?> {
        return getAttribute(TEXT_DY)
    }

    override fun pointToTransformedCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer()!!.invertTransform(this, point)
    }

    override fun pointToAbsoluteCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer()!!.applyTransform(this, point)
    }
}