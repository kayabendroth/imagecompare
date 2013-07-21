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

import com.google.inject.Inject;


/**
 * The main object for image comparison.
 *
 * @author kay.abendroth@raxion.net (Kay Abendroth)
 * @since 1.0.0
 */
public class ImageComparison implements ImageComparisonService {

    /**
     * The image comparison processor to be used for the actual comparison work.
     */
    private final ImageComparisonProcessor proc;


    /**
     * This is the constructor.
     *
     * @param processor The image comparison processor to use for this instance of ImageComparison.
     */
    @Inject
    public ImageComparison(final ImageComparisonProcessor processor) {

        this.proc = processor;
    }


    @Override
    public final boolean compare(final BufferedImage testImage,
            final BufferedImage referenceImage) {

        final boolean result = proc.compare(testImage, referenceImage);
        return result;
    }

    @Override
    public final boolean compare(
            final BufferedImage testImage,
            final BufferedImage referenceImage,
            final double minEqualPercentage) throws InvalidArgumentException {

        final boolean result = proc.compare(testImage, referenceImage, minEqualPercentage);
        return result;
    }
}
