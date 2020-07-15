package com.soywiz.korma.geom.shape

import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.*
import kotlin.test.*

class Shape2dTest {
    @Test
    fun test() {
        assertEquals(
            "Rectangle(x=0, y=0, width=100, height=100)",
            VectorPath { rect(0, 0, 100, 100) }.toShape2d(closed = true).toString()
        )

        assertEquals(
            "Complex(items=[Rectangle(x=0, y=0, width=100, height=100), Rectangle(x=300, y=0, width=100, height=100)])",
            VectorPath {
                rect(0, 0, 100, 100)
                rect(300, 0, 100, 100)
            }.toShape2d(closed = true).toString()
        )

        assertEquals(
            "Polygon(points=[(0, 0), (100, 0), (100, 100)])",
            VectorPath {
                moveTo(0, 0)
                lineTo(100, 0)
                lineTo(100, 100)
                close()
            }.toShape2d(closed = true).toString()
        )
    }

    @Test
    fun test_ToRectangleOrNull() {
        val a = Point(1.0, 1.0)
        val b = Point(1.0, 2.0)
        val c = Point(2.0, 2.0)
        val d = Point(2.0, 1.0)

        assertNotNull(PointArrayList(a, b, c, d).toRectangleOrNull())
        assertNotNull(PointArrayList(d, a, b, c).toRectangleOrNull())
        assertNotNull(PointArrayList(c, d, a, b).toRectangleOrNull())
        assertNotNull(PointArrayList(b, c, d, a).toRectangleOrNull())
        assertNotNull(PointArrayList(b, a, c, d).toRectangleOrNull())
        assertNotNull(PointArrayList(a, c, b, d).toRectangleOrNull())
        assertNotNull(PointArrayList(a, b, d, c).toRectangleOrNull())

        assertNull(PointArrayList(a).toRectangleOrNull())
        assertNull(PointArrayList(a, b).toRectangleOrNull())
        assertNull(PointArrayList(a, b, c).toRectangleOrNull())
        assertNull(PointArrayList(a, b, c, d, a).toRectangleOrNull())

        assertNull(PointArrayList(a, a, b, c).toRectangleOrNull())
        assertNull(PointArrayList(a, b, a, c).toRectangleOrNull())
        assertNull(PointArrayList(a, b, c, a).toRectangleOrNull())
        assertNull(PointArrayList(a, b, b, c).toRectangleOrNull())
        assertNull(PointArrayList(a, a, a, a).toRectangleOrNull())

        assertNull(PointArrayList(Point(0.0, 1.0), Point(1.0, 2.0), Point(2.0, 2.0), Point(2.0, 1.0)).toRectangleOrNull())
        assertNull(PointArrayList(Point(1.0, 1.0), Point(0.0, 2.0), Point(2.0, 2.0), Point(2.0, 1.0)).toRectangleOrNull())
        assertNull(PointArrayList(Point(1.0, 1.0), Point(1.0, 2.0), Point(0.0, 2.0), Point(2.0, 1.0)).toRectangleOrNull())
        assertNull(PointArrayList(Point(1.0, 1.0), Point(1.0, 2.0), Point(2.0, 2.0), Point(0.0, 1.0)).toRectangleOrNull())
        assertNull(PointArrayList(Point(1.0, 0.0), Point(1.0, 2.0), Point(2.0, 2.0), Point(2.0, 1.0)).toRectangleOrNull())
        assertNull(PointArrayList(Point(1.0, 1.0), Point(1.0, 0.0), Point(2.0, 2.0), Point(2.0, 1.0)).toRectangleOrNull())
        assertNull(PointArrayList(Point(1.0, 1.0), Point(1.0, 2.0), Point(2.0, 0.0), Point(2.0, 1.0)).toRectangleOrNull())
        assertNull(PointArrayList(Point(1.0, 1.0), Point(1.0, 2.0), Point(2.0, 2.0), Point(2.0, 0.0)).toRectangleOrNull())
    }
}
