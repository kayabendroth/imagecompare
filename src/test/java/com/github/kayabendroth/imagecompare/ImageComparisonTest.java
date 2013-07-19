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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import com.github.kayabendroth.imagecompare.ImageComparisonModule;
import com.github.kayabendroth.imagecompare.ImageComparisonService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ImageComparisonService}.
 *
 * @author kay.abendroth@raxion.net (Kay Abendroth)
 * @since 1.0.0
 * @see ImageComparisonService
 */
@RunWith(JUnit4.class)
public class ImageComparisonTest {


    private static final String googleReference = "/www.google.com.2013.07.15.png";
    private static final String googleIdentical = "/www.google.com.2013.07.15_same.png";
    private static final String bing = "/www.bing.com.2013.07.18.png";
    private static final String buttonsReference = "/buttons-color.png";
    private static final String buttonsGrey = "/buttons-grey.png";
    private static final String manWallWithoutPlate = "/man_and_wall_without_plate.jpg";
    private static final String manWallWithPlate = "/man_and_wall_with_plate.jpg";


    @Test
    public final void checkImageComparisonWithIdenticalImages() {

        BufferedImage googleReferenceImage;
        BufferedImage googleIdenticalImage;
        googleIdenticalImage = googleReferenceImage = null;

        try {
            googleReferenceImage = ImageIO.read(ImageComparisonTest.class.getResourceAsStream(googleReference));
            googleIdenticalImage = ImageIO.read(ImageComparisonTest.class.getResourceAsStream(googleIdentical));
        } catch (final IOException ioe) {
            assertTrue(ioe.getMessage(), false);
        } catch (final IllegalArgumentException iae) {
            assertTrue(iae.getMessage(), false);
        }

        // Both images are not null.
        assertTrue("Google reference image is not null", googleReferenceImage != null);
        assertTrue("Google test image is not null", googleIdenticalImage != null);

        final Injector injector = Guice.createInjector(new ImageComparisonModule());
        final ImageComparisonService imageComparisonService = injector.getInstance(ImageComparisonService.class);

        // Test successful comparison: Pictures are 100 per cent identical.
        final boolean result = imageComparisonService.compare(googleIdenticalImage, googleReferenceImage);
        assertTrue("Test successful comparison: Pictures are 100 per cent identical.", result);
    }

    @Test
    public final void checkImageComparisonBingIsNotGoogle() {

        BufferedImage googleReferenceImage;
        BufferedImage bingReferenceImage;
        googleReferenceImage = bingReferenceImage = null;

        try {
            googleReferenceImage = ImageIO.read(ImageComparisonTest.class.getResourceAsStream(googleReference));
            bingReferenceImage = ImageIO.read(ImageComparisonTest.class.getResourceAsStream(bing));
        } catch (final IOException ioe) {
            assertTrue(ioe.getMessage(), false);
        } catch (final IllegalArgumentException iae) {
            assertTrue(iae.getMessage(), false);
        }

        // Both images are not null.
        assertTrue("Reference image is not null", googleReferenceImage != null);
        assertTrue("Test image is not null", bingReferenceImage != null);

        final Injector injector = Guice.createInjector(new ImageComparisonModule());
        final ImageComparisonService imageComparisonService = injector.getInstance(ImageComparisonService.class);

        // Test unsuccessful comparison: Pictures are not identical and differences are not within our accepted boundaries.
        final boolean result = imageComparisonService.compare(bingReferenceImage, googleReferenceImage);
        assertFalse("Test unsuccessful comparison: Bing is not Google.", result);
    }

    @Test
    public final void checkImageComparisonWithDifferentImages() {

        BufferedImage buttonsReferenceImage;
        BufferedImage buttonsGreyImage;
        buttonsReferenceImage = buttonsGreyImage = null;

        try {
            buttonsReferenceImage = ImageIO.read(ImageComparisonTest.class.getResourceAsStream(buttonsReference));
            buttonsGreyImage = ImageIO.read(ImageComparisonTest.class.getResourceAsStream(buttonsGrey));
        } catch (final IOException ioe) {
            assertTrue(ioe.getMessage(), false);
        } catch (final IllegalArgumentException iae) {
            assertTrue(iae.getMessage(), false);
        }

        // Both images are not null.
        assertTrue("Reference image is not null", buttonsReferenceImage != null);
        assertTrue("Test image is not null", buttonsGreyImage != null);

        final Injector injector = Guice.createInjector(new ImageComparisonModule());
        final ImageComparisonService imageComparisonService = injector.getInstance(ImageComparisonService.class);

        // Test unsuccessful comparison: Pictures are not identical and differences are not within our accepted boundaries.
        final boolean result = imageComparisonService.compare(buttonsGreyImage, buttonsReferenceImage);
        assertFalse("Test unsuccessful comparison: Buttons with color and all buttons grey is a difference which is not within our accepted boundaries.", result);
    }

    /**
     * This test uses two real-world webcam images which are slightly different (one image shows a
     * wall with plate and the other one doesn't have a plate on the wall).
     *
     * @see http://mindmeat.blogspot.de/2008/07/java-image-comparison.html
     */
    @Test
    public final void checkImageComparisonWithSlightlyDifferentRealWorldImages() {

        BufferedImage referenceImage;
        BufferedImage testImage;
        referenceImage = testImage = null;

        try {
            referenceImage = ImageIO.read(ImageComparisonTest.class.getResourceAsStream(manWallWithPlate));
            testImage = ImageIO.read(ImageComparisonTest.class.getResourceAsStream(manWallWithoutPlate));
        } catch (final IOException ioe) {
            assertTrue(ioe.getMessage(), false);
        } catch (final IllegalArgumentException iae) {
            assertTrue(iae.getMessage(), false);
        }

        // Both images are not null.
        assertTrue("Reference image is not null", referenceImage != null);
        assertTrue("Test image is not null", testImage != null);

        final Injector injector = Guice.createInjector(new ImageComparisonModule());
        final ImageComparisonService imageComparisonService = injector.getInstance(ImageComparisonService.class);

        // Test unsuccessful comparison: Pictures are not identical and differences are not within our accepted boundaries.
        final boolean result = imageComparisonService.compare(testImage, referenceImage);
        assertFalse("Test unsuccessful comparison: Picture from man in front of a wall with plate and picture from man in front of a wall w/o plate is not within our accepted boundaries.", result);
    }
}
