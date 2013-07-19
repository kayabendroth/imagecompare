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
 * Interface for the Image Comparison Service.
 *
 * @author kay.abendroth@raxion.net (Kay Abendroth)
 * @since 1.0.0
 */
public interface ImageComparisonService {


    /**
     * Compare two images.
     *
     * @param testImage The image you want to test.
     * @param referenceImage The reference image for the test.
     * @return {@literal true}, if the test image is 95 per cent identical to the reference image.
     */
    boolean compare(BufferedImage testImage, BufferedImage referenceImage);
}
