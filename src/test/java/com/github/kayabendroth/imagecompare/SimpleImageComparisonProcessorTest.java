/*
 * This file is part of the Java library imagecompare.
 *
 * Copyright © 2013, Kay Abendroth or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * imagecompare is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * imagecompare is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with imagecompare. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.kayabendroth.imagecompare;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.github.kayabendroth.imagecompare.SimpleImageComparisonProcessor;


/**
 * Tests for methods provided by {@link SimpleImageComparisonProcessor}.
 *
 * @author kay.abendroth@raxion.net (Kay Abendroth)
 * @since 1.0.0
 * @see SimpleImageComparisonProcessor
 */
@RunWith(JUnit4.class)
public class SimpleImageComparisonProcessorTest {


    @Test
    public final void checkCalcDistance() {

        final Color[][] lengthZero = new Color[0][0];
        final Color[][] lengthOne = new Color[1][1];
        final Color[][] lengthFive = new Color[5][5];
        final int fiveRefRegions = 5;

        // Zero will be returned immediately, if refRegionsInOneDimension is zero.
        double actual = -1;
        try {
            actual = SimpleImageComparisonProcessor.calcDistance(lengthZero, lengthZero, 0);
        } catch (final InvalidArgumentException iae) {
            assertTrue(iae.getMessage(), false);
        }
        assertEquals("Zero will be returned immediately, if refRegionsInOneDimension is zero.",
                0.0,
                actual,
                0);

        // Exception will be thrown, if length of source and target array don't match number of regions.
        actual = -1;
        InvalidArgumentException actualException = null;
        String expectedExceptionMessage = "Source array length doesn't match number of regions.";
        try {
            actual = SimpleImageComparisonProcessor.calcDistance(lengthZero, lengthFive, fiveRefRegions);
        } catch (final InvalidArgumentException iae) {
            actualException = iae;
        }
        assertTrue("Exception is not null.", actualException != null);
        assertEquals("Exception will be thrown, if length of source and target array don't match number of regions.",
                expectedExceptionMessage,
                actualException.getMessage());

        actual = -1;
        actualException = null;
        expectedExceptionMessage = "Target array length doesn't match number of regions.";
        try {
            actual = SimpleImageComparisonProcessor.calcDistance(lengthFive, lengthOne, fiveRefRegions);
        } catch (final InvalidArgumentException iae) {
            actualException = iae;
        }
        assertTrue("Exception is not null.", actualException != null);
        assertEquals("Exception will be thrown, if length of source and target array don't match number of regions.",
                expectedExceptionMessage,
                actualException.getMessage());

        // Number of reference regions is not allowed to be below zero.
        actual = -1;
        actualException = null;
        expectedExceptionMessage = "Number of reference regions must be zero or higher.";
        try {
            actual = SimpleImageComparisonProcessor.calcDistance(lengthFive, lengthFive, -111);
        } catch (final InvalidArgumentException iae) {
            actualException = iae;
        }
        assertTrue("Exception is not null.", actualException != null);
        assertEquals("Exception will be thrown, if length of source and target array don't match number of regions.",
                expectedExceptionMessage,
                actualException.getMessage());
    }

    @Test
    public final void checkCalculateMaxDistance() {

        // Number of regions is zero. The maximum distance possible is zero as well.
        assertEquals("Number of regions is zero. The maximum distance possible is zero as well.",
                0,
                SimpleImageComparisonProcessor.calculateMaxDistance(0),
                0);

        // Number of regions is below zero. The maximum distance returned is -1.
        assertEquals("Number of regions is below zero. The maximum distance returned is -1.",
                -1,
                SimpleImageComparisonProcessor.calculateMaxDistance(-111),
                0);

        // Number of regions is 25. The maximum distance returned is 11.041,8238983.
        assertEquals("Number of regions is below zero. The maximum distance returned is " +
                "11.041,8238983.",
                11041.8238983,
                SimpleImageComparisonProcessor.calculateMaxDistance(25),
                1);
    }

    @Test
    public final void checkGetRgbArrayFromPixel() {

        // First: The array returned has a length of three.
        assertEquals("First: The array returned has a length of three.",
                3,
                SimpleImageComparisonProcessor.getRgbArrayFromPixel(Color.WHITE.getRGB()).length);

        // Test with color white.
        assertArrayEquals("Test with color white.",
                new double[] {255, 255, 255},
                SimpleImageComparisonProcessor.getRgbArrayFromPixel(Color.WHITE.getRGB()),
                0);

        // Test with color black.
        assertArrayEquals("Test with color black.",
                new double[] {0, 0, 0},
                SimpleImageComparisonProcessor.getRgbArrayFromPixel(Color.BLACK.getRGB()),
                0);

        // Test with color red.
        assertArrayEquals("Test with color red.",
                new double[] {255, 0, 0},
                SimpleImageComparisonProcessor.getRgbArrayFromPixel(Color.RED.getRGB()),
                0);
    }
}
