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

import java.awt.image.BufferedImage;


/**
 * Interface for the Image Comparison Processor.
 *
 * @author kay.abendroth@raxion.net (Kay Abendroth)
 * @since 1.0.0
 */
public interface ImageComparisonProcessor {


    /**
     * The lower boundary in per cent for two images to be declared as equal. Two images are
     * considered to be equal, if at least {{@literal DEFINITION_OF_EQUAL}} per cent of the two
     * images are identical.
     */
    double DEFAULT_DEFINITION_OF_EQUAL = 99.94;


    /**
     * Compare two images.
     *
     * @param testImage The image you want to test.
     * @param referenceImage The reference image for the test.
     * @return {@literal true}, if the test image is {@link DEFAULT_DEFINITION_OF_EQUAL} per cent
     * identical to the reference image.
     * @see DEFAULT_DEFINITION_OF_EQUAL
     */
    boolean compare(BufferedImage testImage, BufferedImage referenceImage);

    /**
     * Compare two images with configurable minimum percentage of equality.
     *
     * @param testImage The image you want to test.
     * @param referenceImage The reference image for the test.
     * @param minEqualPercentage The per cent value definining whether or not the two images are to
     * be considered equal.
     * @return {@literal true}, if the test image is at least {@literal minEqualPercentage} per cent
     * identical to the reference image.
     * @throws InvalidArgumentException If {@literal minEqualPercentage} is lower than zero or
     * higher than one hundred.
     */
    boolean compare(
            BufferedImage testImage,
            BufferedImage referenceImage,
            double minEqualPercentage) throws InvalidArgumentException;
}
