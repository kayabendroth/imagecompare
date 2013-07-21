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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.github.kayabendroth.imagecompare.InvalidArgumentException;


/**
 * Tests for methods provided by {@link SimpleImageComparisonProcessor}.
 *
 * @author kay.abendroth@raxion.net (Kay Abendroth)
 * @since 1.0.0
 * @see SimpleImageComparisonProcessor
 */
@RunWith(JUnit4.class)
public class InvalidArgumentExceptionTest {


    @Test
    public final void checkConstructor() {

        // Message is null.
        final InvalidArgumentException nullMessage = new InvalidArgumentException(null);
        assertTrue("Exception has been created and is not null", nullMessage != null);
        assertTrue("Exception message is null.", nullMessage.getMessage() == null);

        // Message is not null.
        final String exceptionMessage = "Invalid argument.";
        final InvalidArgumentException withMessage = new InvalidArgumentException(exceptionMessage);
        assertTrue("Exception has been created and is not null", nullMessage != null);
        assertEquals("Exception message is has been stored in exception.",
                exceptionMessage,
                withMessage.getMessage());
    }
}
