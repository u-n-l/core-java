package unl.core;

import org.junit.Assert;
import org.junit.Test;

import static unl.core.UnlCore.DEFAULT_ELEVATION;

public class UnlCoreUnitTest {
    @Test
    public void encodeTest() {
        /* encodes Jutland */
        Assert.assertEquals(UnlCore.encode(57.648, 10.41, 6), "u4pruy");
        /* encodes Jutland floor 5 */
        Assert.assertEquals(UnlCore.encode(57.648, 10.41, 6, new Elevation(5)), "u4pruy@5");
        /* encodes Jutland floor -2 */
        Assert.assertEquals(UnlCore.encode(57.648, 10.41, 6, new Elevation(-2)), "u4pruy@-2");
        /* encodes Jutland heightincm 87 */
        Assert.assertEquals(UnlCore.encode(57.648, 10.41, 6, new Elevation(87, "heightincm")), "u4pruy#87");
        /* encodes Jutland with default precision 9 */
        Assert.assertEquals(UnlCore.encode(57.64, 10.41), "u4pruvh36");
        /* encodes Curitiba */
        Assert.assertEquals(UnlCore.encode(-25.38262, -49.26561, 8), "6gkzwgjz");
        /* matches locationId */
        Assert.assertEquals(UnlCore.encode(37.25, 123.75, 12), "wy85bj0hbp21");
    }

    @Test
    public void decodeTest() {
        /* decodes Jutland */
        Assert.assertEquals(UnlCore.decode("u4pruy"),
                new PointWithElevation(new Point(57.648, 10.41), DEFAULT_ELEVATION, new Bounds(
                        57.6507568359375, 10.4150390625, 57.645263671875, 10.404052734375
                )));

        /* decodes Justland floor 3 */
        Assert.assertEquals(UnlCore.decode("u4pruy@3"),
                new PointWithElevation(new Point(57.648, 10.41),
                        new Elevation(3, "floor"),
                        new Bounds(57.6507568359375, 10.4150390625, 57.645263671875, 10.404052734375
                        )));

        /* decodes Justland floor 0 */
        Assert.assertEquals(UnlCore.decode("u4pruy@0"),
                new PointWithElevation(new Point(57.648, 10.41),
                        DEFAULT_ELEVATION,
                        new Bounds(
                                57.6507568359375, 10.4150390625, 57.645263671875, 10.404052734375
                        )));

        /* decodes Justland floor -2 */
        Assert.assertEquals(UnlCore.decode("u4pruy@-2"),
                new PointWithElevation(new Point(57.648, 10.41),
                        new Elevation(-2, "floor"),
                        new Bounds(
                                57.6507568359375, 10.4150390625, 57.645263671875, 10.404052734375
                        )));

        /* decodes Jutland heightincm 87 */
        Assert.assertEquals(UnlCore.decode("u4pruy#87"),
                new PointWithElevation(new Point(57.648, 10.41),
                        new Elevation(87, "heightincm"),
                        new Bounds(
                                57.6507568359375, 10.4150390625, 57.645263671875, 10.404052734375
                        )));

        /* decodes Jutland heightincm 0 */
        Assert.assertEquals(UnlCore.decode("u4pruy#0"),
                new PointWithElevation(new Point(57.648, 10.41),
                        new Elevation(0, "heightincm"),
                        new Bounds(
                                57.6507568359375, 10.4150390625, 57.645263671875, 10.404052734375
                        )));

        /* decodes Curitiba */
        Assert.assertEquals(UnlCore.decode("6gkzwgjz"),
                new PointWithElevation(new Point(-25.38262, -49.26561),
                        DEFAULT_ELEVATION,
                        new Bounds(
                                -25.382537841796875, -49.26544189453125, -25.382709503173828, -49.265785217285156
                        )));

        /* decodes Curitiba floor 5 */
        Assert.assertEquals(UnlCore.decode("6gkzwgjz@5"),
                new PointWithElevation(new Point(-25.38262, -49.26561),
                        new Elevation(5, "floor"),
                        new Bounds(
                                -25.382537841796875, -49.26544189453125, -25.382709503173828, -49.265785217285156
                        )));

        /* decodes Curitiba heightincm 90 */
        Assert.assertEquals(UnlCore.decode("6gkzwgjz#90"),
                new PointWithElevation(new Point(-25.38262, -49.26561),
                        new Elevation(90, "heightincm"),
                        new Bounds(
                                -25.382537841796875, -49.26544189453125, -25.382709503173828, -49.265785217285156
                        )));
    }


    @Test
    public void appendElevationTest() {
        /* appends elevation Curitiba 5th floor */
        Assert.assertEquals(UnlCore.appendElevation("6gkzwgjz", new Elevation(5)),
                "6gkzwgjz@5"
        );

        /* appends elevation Curitiba above 87cm */
        Assert.assertEquals(UnlCore.appendElevation("6gkzwgjz", new Elevation(87, "heightincm")),
                "6gkzwgjz#87"
        );
    }

    @Test
    public void excludeElevationTest() {
        /* excludes elevation Curitiba 5th floor */
        Assert.assertEquals(UnlCore.excludeElevation("6gkzwgjz@5"),
                new LocationIdWithElevation(
                        "6gkzwgjz",
                        new Elevation(5, "floor")
                )
        );

        /* excludes elevation Curitiba above 87cm */
        Assert.assertEquals(UnlCore.excludeElevation("6gkzwgjz#87"),
                new LocationIdWithElevation(
                        "6gkzwgjz",
                        new Elevation(87, "heightincm")
                )
        );
    }

    @Test
    public void adjacentTest() {
        /* adjacent north */
        Assert.assertEquals(UnlCore.adjacent("ezzz@5", "n"), "gbpb@5");
    }

    @Test
    public void neighbourTest() {
        /* fetches neighbours */
        Assert.assertEquals(UnlCore.neighbours("ezzz"),
                new Neighbours("gbpb", "u000", "spbp", "spbn", "ezzy", "ezzw", "ezzx", "gbp8"));

        /* fetches neighbours 5th floor */
        Assert.assertEquals(UnlCore.neighbours("ezzz@5"),
                new Neighbours("gbpb@5", "u000@5", "spbp@5", "spbn@5", "ezzy@5", "ezzw@5", "ezzx@5", "gbp8@5"));

        /* fetches neighbours -2 floor */
        Assert.assertEquals(UnlCore.neighbours("ezzz@-2"),
                new Neighbours("gbpb@-2", "u000@-2", "spbp@-2", "spbn@-2", "ezzy@-2", "ezzw@-2", "ezzx@-2", "gbp8@-2"));

        /* fetches neighbours above 87cm */
        Assert.assertEquals(UnlCore.neighbours("ezzz#87"),
                new Neighbours("gbpb#87", "u000#87", "spbp#87", "spbn#87", "ezzy#87", "ezzw#87", "ezzx#87", "gbp8#87"));

        /* fetches neighbours below 5cm */
        Assert.assertEquals(UnlCore.neighbours("ezzz#-5"),
                new Neighbours("gbpb#-5", "u000#-5", "spbp#-5", "spbn#-5", "ezzy#-5", "ezzw#-5", "ezzx#-5", "gbp8#-5"));
    }

    @Test
    public void gridLinesTest() {
        /* retrieves grid lines with precision 9 */
        Assert.assertEquals(UnlCore.gridLines(
                new Bounds(
                        46.77227194246396, 23.59560827603795, 46.77210936378606, 23.595436614661565
                ), 9
        ).size(), 7);

        /* retrieves grid lines with no precision specified (default 9) */
        Assert.assertEquals(UnlCore.gridLines(
                new Bounds(
                        46.77227194246396, 23.59560827603795, 46.77210936378606, 23.595436614661565
                )
        ).size(), 7);

        /* retrieves grid lines with precision 12 */
        Assert.assertEquals(UnlCore.gridLines(
                new Bounds(
                        46.77227194246396, 23.59560827603795, 46.77210936378606, 23.595436614661565
                ), 12
        ).size(), 1481);
    }
}
