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

import static org.imgscalr.Scalr.Method;
import static org.imgscalr.Scalr.Mode;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.imgscalr.Scalr;


/**
 * This is a simple image comparison processor. It only computes differences in the RGB values,
 * without trying to match key parts of the two images first.
 * <p>
 * Based on the article "How do I compare two images to see if they are equal?" by Rafael Santos.
 *
 * @author kay.abendroth@raxion.net (Kay Abendroth), Rafael Santos
 * @since 1.0.0
 * @see http://www.lac.inpe.br/JIPCookbook/6050-howto-compareimages.jsp
 * @see http://stackoverflow.com/questions/8644960/java-library-to-compare-image-similarity
 */
public class SimpleImageComparisonProcessor implements ImageComparisonProcessor {


    /**
     * The lower boundary in per cent for two images to be declared as equal. Two images are
     * considered to be equal, if at least {{@literal DEFINITION_OF_EQUAL}} per cent of the two
     * images are identical.
     */
    private static final double DEFINITION_OF_EQUAL = 99.94;

    /**
     * This is the main value setting the overall quality of the comparison as it determines the
     * number of reference pixels per image. The smaller the distance, the higher the quality.
     */
    private static final short DISTANCE_BETWEEN_REFERENCE_PIXELS = 28;

    /**
     * This is our target sample size. It will be used to initialize {@link sampleSize}.
     */
    private static final short TARGET_SAMPLE_SIZE = 12;

    /**
     * The size of the sampling area used in the averageAround() method.
     */
    private static int sampleSize = TARGET_SAMPLE_SIZE;

    /**
     * The scaling method to be used for scaling the test image.
     *
     * @see org.imgscalr.Scalr.Method.BALANCED
     */
    private static final Method SCALING_METHOD = Method.BALANCED;

    /**
     * The size of a pixel vector is three.
     */
    private static final int PIXEL_VECTOR_SIZE = 3;

    /**
     * One hundred.
     */
    private static final int ONE_HUNDRED = 100;

    /**
     * The maximum value for RGB is 255.
     */
    private static final int MAX_RGB_VALUE = 255;

    /**
     * To calculate the maximum possible distance between two images, we need a multiplier, which
     * is based on the vector size and the maximum RGB value.
     */
    private static final double MAX_DISTANCE_MULTIPLIER =
            (Math.sqrt((MAX_RGB_VALUE - 0) * (MAX_RGB_VALUE - 0)
                     + (MAX_RGB_VALUE - 0) * (MAX_RGB_VALUE - 0)
                     + (MAX_RGB_VALUE - 0) * (MAX_RGB_VALUE - 0)));


    @Override
    public final boolean compare(final BufferedImage testImage,
            final BufferedImage referenceImage) {

        /**
         * Set all the values we need for the processing.
         */

        /**
         * The width of the reference image. This value also determines the to-be-scaled-to-width
         * of the test image.
         */
        final int referenceWidth = referenceImage.getWidth();
        System.err.println("Reference width is: " + referenceWidth);
        System.err.println("Reference heigth is: " + referenceImage.getHeight());

        /**
         * This number determines the total amount of reference pixels, which is
         * numberOfReferencePixels * numberOfReferencePixels. The higher this value, the higher
         * the overall quality of the comparison. It's determined only by
         * DISTANCE_BETWEEN_REFERENCE_PIXELS.
         *
         * @see DISTANCE_BETWEEN_REFERENCE_PIXELS
         */
        final int refRegionsInOneDimension = referenceWidth / DISTANCE_BETWEEN_REFERENCE_PIXELS;
        System.err.println("No. of reference regions in one dimension is: "
                + refRegionsInOneDimension);

        /**
         * The maximum possible distance between reference image and test image.
         */
        final double maxDistance =
                calculateMaxDistance(refRegionsInOneDimension * refRegionsInOneDimension);
        System.err.println("Max. distance is: " + maxDistance);

        /**
         * The reference image "signature" (numberOfReferencePixels * numberOfReferencePixels
         * representative pixels, each in R,G,B). We use instances of Color to make things simpler.
         *
         * Calculate the signature vector for the reference.
         */
        final Color[][] refSignature = calcSignature(referenceImage, refRegionsInOneDimension);

        /**
         * Re-scale the test image to match the width of our reference image. The library
         * {@literal imgscalr} will be used with following options:
         * <ul>
         *   <li>the scaling method is determined by the class variable {@link SCALING_METHOD} and
         *   </li>
         *   <li>we re-scale in {@literal FIT_TO_WIDTH} mode always</li>
         * </ul>
         *
         * @see http://www.thebuzzmedia.com/software/imgscalr-java-image-scaling-library
         * @see SCALING_METHOD
         * @see org.imgscalr.Scalr.Mode.FIT_TO_WIDTH
         */
        final BufferedImage testImageRescaled = Scalr.resize(testImage, SCALING_METHOD,
                Mode.FIT_TO_WIDTH, referenceWidth, 1);

        /**
         * "This operation leaves the original src image unmodified. If the caller is done with the
         * src image after getting the result of this operation, remember to call Image.flush() on
         * the src to free up native resources and make it easier for the GC to collect the unused
         * image."
         *
         * @see org.imgscalr.Scalr.resize()
         */
        testImage.flush();

        // Calculate the signature vector for the test image.
        final Color[][] testSignature = calcSignature(testImageRescaled, refRegionsInOneDimension);

        // Calculate the distance to the other image.
        double distanceToReference = -1;
        try {
            distanceToReference =
                    calcDistance(testSignature, refSignature, refRegionsInOneDimension);
        } catch (final InvalidArgumentException iae) {
            System.err.println(iae.getMessage());
        }

        // How much of the test image is identical to the reference image?
        final double percentageOfEquality =
                ONE_HUNDRED - ((distanceToReference / maxDistance) * ONE_HUNDRED);
        System.err.println("Calculated percentage of equality is: " + percentageOfEquality);

        /**
         * The two images are considered to be equal, if the test image is identical to the
         * reference image for at least the amount of {{@link DEFINITION_OF_EQUAL}}.
         *
         * @see DEFINITION_OF_EQUAL
         */
        return (percentageOfEquality >= DEFINITION_OF_EQUAL);
    }

    /**
     * This method calculates and returns signature vectors for the input image.
     *
     * @param image The image to calculate the signature for.
     * @param refRegionsInOneDimension The number of reference areas to be used.
     * @return A two-dimensional {@link Color} array of the size {@link numberOfReferencePixels} *
     * {@link numberOfReferencePixels}.
     */
    protected static final Color[][] calcSignature(
            final BufferedImage image,
            final int refRegionsInOneDimension) {

        // Get memory for the signature.
        final Color[][] sig = new Color[refRegionsInOneDimension][refRegionsInOneDimension];

        /**
         * For each of the XXX signature values average the pixels around it. Note that the
         * coordinate of the central pixel is in proportions.
         */
        final float[] prop = new float[refRegionsInOneDimension];

        /**
         * No we need to fill this array.
         * <p>
         * This is an example of the original implementation, which had hard-coded values for a
         * fixed region-per-axis-amount of five: {1f / 10f, 3f / 10f, 5f / 10f, 7f / 10f, 9f / 10f}.
         * <p>
         * But we want to be more dynamic. We calculate a {@literal propValueDistance} first.
         */
        final float propValueDistance =
                (ONE_HUNDRED * 1f / (refRegionsInOneDimension + 1)) / ONE_HUNDRED * 1f;
        System.err.println("propValueDistance is: " + propValueDistance);
        for (int i = 0; i < refRegionsInOneDimension; i++) {
            prop[i] = (i + 1) * propValueDistance;
        }

        /**
         * The following calculation is very important, as we may get IndexOutOfBounce errors
         * otherwise. Depending on the @link NUMBER_OF_REFERENCE_PIXELS and the
         * {@link DISTANCE_BETWEEN_REFERENCE_PIXELS} the {@link SAMPLE_SIZE} may be too high.
         * <p>
         * In order to prevent that, we then need to adjust the sample size.
         */
        final int imageWidth = image.getWidth();
        final int imageHeight = image.getHeight();
        // Upper boundaries for X- and Y-axis.
        while (prop[refRegionsInOneDimension - 1] * imageWidth + sampleSize
                >= imageWidth) {
            sampleSize = sampleSize - 1;
        }
        while (prop[refRegionsInOneDimension - 1] * imageHeight + sampleSize >= imageHeight) {
            sampleSize = sampleSize - 1;
        }
        // Lower boundaries for X- and Y-axis.
        while (prop[0] * imageWidth - sampleSize <= 0) {
            sampleSize = sampleSize - 1;
        }
        while (prop[0] * imageHeight - sampleSize <= 0) {
            sampleSize = sampleSize - 1;
        }
        System.err.println("(New) SAMPLE_SIZE is: " + sampleSize);

        /**
         * Then we calculate the average RGB value for every region.
         */
        for (int x = 0; x < refRegionsInOneDimension; x++) {
            for (int y = 0; y < refRegionsInOneDimension; y++) {
                sig[x][y] = averageAround(image, prop[x], prop[y]);
            }
        }

        return sig;
    }

    /**
     * This method averages the pixel values around a central point and return the average as an
     * instance of Color. The point coordinates are proportional to the image.
     *
     * @param image The image to operate on.
     * @param px The x-coordinate of the center of the area to inspect.
     * @param py The y-coordinate of the center of the area to inspect.
     * @return An averaged color value for the area of interest.
     */
    protected static Color averageAround(
            final BufferedImage image,
            final double px,
            final double py) {

        // Get memory for raw pixel, pixel and for the accumulator.
        int rawPixel = -1;
        final double[] accum = new double[] {0, 0, 0};

        int numPixels = 0;
        final int imageWidth = image.getWidth();
        final int imageHeight = image.getHeight();
        // Sample the pixels.
        // Traverse along the x-axis of the image.
        for (double x = px * imageWidth - sampleSize; x < px * imageWidth + sampleSize; x++)
        {
            // Travers along the y-axis of the image.
            for (double y = py * imageHeight - sampleSize; y < py * imageHeight + sampleSize; y++) {
                // Get the raw pixel value for the current pixel first.
                rawPixel = image.getRGB((int) x, (int) y);
                // Convert this to a double array.
                final double[] pixel = getRgbArrayFromPixel(rawPixel);
                accum[0] += pixel[0];
                accum[1] += pixel[1];
                accum[2] += pixel[2];
                numPixels++;
            }
        }

        // Average the accumulated values.
        accum[0] /= numPixels;
        accum[1] /= numPixels;
        accum[2] /= numPixels;

        return new Color((int) accum[0], (int) accum[1], (int) accum[2]);
    }

    /**
     * This method calculates the distance between the signatures of an image and the reference one.
     * The signatures for the image passed as the parameter are calculated inside the method.
     *
     * @param source The source signature array.
     * @param target The target signature array.
     * @param refRegionsInOneDimension The number of reference regions in one dimension.
     * @return The calculated distance to the other image.
     * @throws InvalidArgumentException If the source or target array length doesn't match
     * refRegionsInOneDimension or if refRegionsInOneDimension is smaller than zero.
     */
    protected static final double calcDistance(
            final Color[][] source,
            final Color[][] target,
            final int refRegionsInOneDimension) throws InvalidArgumentException {

        // We can return zero immediately, if the number of reference regions is zero.
        if (refRegionsInOneDimension == 0) { return 0; }
        // Length of source and target array has to match number of regions.
        if (source.length != refRegionsInOneDimension) {
            throw new InvalidArgumentException(
                    "Source array length doesn't match number of regions.");
        }
        if (target.length != refRegionsInOneDimension) {
            throw new InvalidArgumentException(
                    "Target array length doesn't match number of regions.");
        }
        // Number of reference regions must be higher or equal than zero.
        if (refRegionsInOneDimension < 0) {
            throw new InvalidArgumentException(
                    "Number of reference regions must be zero or higher.");
        }


        /**
         * There are several ways to calculate distances between two vectors, we will calculate the
         * sum of the distances between the RGB values of pixels in the same positions.
         */
        double dist = 0;
        for (int x = 0; x < refRegionsInOneDimension; x++) {
            for (int y = 0; y < refRegionsInOneDimension; y++) {
                final int r1 = source[x][y].getRed();
                final int g1 = target[x][y].getGreen();
                final int b1 = source[x][y].getBlue();
                final int r2 = target[x][y].getRed();
                final int g2 = source[x][y].getGreen();
                final int b2 = target[x][y].getBlue();
                final double tempDist = Math.sqrt((r1 - r2) * (r1 - r2) + (g1 - g2)
                        * (g1 - g2) + (b1 - b2) * (b1 - b2));
                dist += tempDist;
            }
        }

        return dist;
    }

    /**
     * Calculate the maximum distance between two images depending on the number of regions used.
     *
     * @param totalNumberOfRefRegions The number of regions used to compute the differences between
     * two
     * images.
     * @return The maximum distance possible as a double value or -1, if the number of regions is
     * below zero.
     */
    protected static final double calculateMaxDistance(final int totalNumberOfRefRegions) {

        if (totalNumberOfRefRegions < 0) { return -1; }

        return totalNumberOfRefRegions * MAX_DISTANCE_MULTIPLIER;
    }

    /**
     * Image pixels are arrays of integers [32 bits/4Bytes]. Consider a 32 pixel as
     * 11111111-00110011-00111110-00011110.
     * <p>
     * First Byte From Left [11111111]= Alpha
     * Second Byte From Left[00110011]= Red
     * Third Byte From Left [00111110]= Green
     * Fourth Byte From Left[00011110]= Blue
     *
     * The following method will do a proper bit shift and logical AND operation to extract the
     * correct values of different color/alpha components.
     *
     * @param pixel The raw pixel value from your image.
     * @return A double array containing the RGB values.
     * @see http://sanjaal.com/java/tag/java-image-get-pixel-information/
     */
    protected static final double[] getRgbArrayFromPixel(final int pixel) {

        final double[] rgbArray = new double[PIXEL_VECTOR_SIZE];

        final int redPosition = 16;
        final int greenPosition = 8;
        final int bitmask = 0x000000FF;

        final int red = (pixel >> redPosition) & bitmask;
        final int green = (pixel >> greenPosition) & bitmask;
        final int blue = (pixel) & bitmask;

        rgbArray[0] = red;
        rgbArray[1] = green;
        rgbArray[2] = blue;

        return rgbArray;
    }
}
