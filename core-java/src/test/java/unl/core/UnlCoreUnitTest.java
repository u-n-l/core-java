package unl.core;

import org.junit.Assert;
import org.junit.Test;

import static unl.core.UnlCore.DEFAULT_ELEVATION;

public class UnlCoreUnitTest {
    @Test
    public void encodeTest() {
        /* get UnlCore instance */
        UnlCore unlCore = UnlCore.getInstance();

        /* encodes Jutland */
        Assert.assertEquals(unlCore.encode(57.648, 10.41, 6), "u4pruy");
        /* encodes Jutland floor 5 */
        Assert.assertEquals(unlCore.encode(57.648, 10.41, 6, new Elevation(5)), "u4pruy@5");
        /* encodes Jutland floor -2 */
        Assert.assertEquals(unlCore.encode(57.648, 10.41, 6, new Elevation(-2)), "u4pruy@-2");
        /* encodes Jutland heightincm 87 */
        Assert.assertEquals(unlCore.encode(57.648, 10.41, 6, new Elevation(87, "heightincm")), "u4pruy#87");
        /* encodes Jutland with default precision 9 */
        Assert.assertEquals(unlCore.encode(57.64, 10.41), "u4pruvh36");
        /* encodes Curitiba */
        Assert.assertEquals(unlCore.encode(-25.38262, -49.26561, 8), "6gkzwgjz");
        /* matches locationId */
        Assert.assertEquals(unlCore.encode(37.25, 123.75, 12), "wy85bj0hbp21");
    }

    @Test
    public void decodeTest() {
        /* get UnlCore instance */
        UnlCore unlCore = UnlCore.getInstance();

        /* decodes Jutland */
        Assert.assertEquals(unlCore.decode("u4pruy"),
                new PointWithElevation(new Point(57.648, 10.41), DEFAULT_ELEVATION, new BoundsWithElevation(new Bounds(
                        new Point(57.645263671875, 10.404052734375),
                        new Point(57.6507568359375, 10.4150390625)
                ), DEFAULT_ELEVATION)));

        /* decodes Justland floor 3 */
        Assert.assertEquals(unlCore.decode("u4pruy@3"),
                new PointWithElevation(new Point(57.648, 10.41),
                        new Elevation(3, "floor"),
                        new BoundsWithElevation(new Bounds(
                                new Point(57.645263671875, 10.404052734375),
                                new Point(57.6507568359375, 10.4150390625)
                        ), DEFAULT_ELEVATION)));

        /* decodes Justland floor 0 */
        Assert.assertEquals(unlCore.decode("u4pruy@0"),
                new PointWithElevation(new Point(57.648, 10.41),
                        DEFAULT_ELEVATION,
                        new BoundsWithElevation(new Bounds(
                                new Point(57.645263671875, 10.404052734375),
                                new Point(57.6507568359375, 10.4150390625)
                        ), DEFAULT_ELEVATION)));

        /* decodes Justland floor -2 */
        Assert.assertEquals(unlCore.decode("u4pruy@-2"),
                new PointWithElevation(new Point(57.648, 10.41),
                        new Elevation(-2, "floor"),
                        new BoundsWithElevation(new Bounds(
                                new Point(57.645263671875, 10.404052734375),
                                new Point(57.6507568359375, 10.4150390625)
                        ), DEFAULT_ELEVATION)));

        /* decodes Jutland heightincm 87 */
        Assert.assertEquals(unlCore.decode("u4pruy#87"),
                new PointWithElevation(new Point(57.648, 10.41),
                        new Elevation(87, "heightincm"),
                        new BoundsWithElevation(new Bounds(
                                new Point(57.645263671875, 10.404052734375),
                                new Point(57.6507568359375, 10.4150390625)
                        ), DEFAULT_ELEVATION)));

        /* decodes Jutland heightincm 0 */
        Assert.assertEquals(unlCore.decode("u4pruy#0"),
                new PointWithElevation(new Point(57.648, 10.41),
                        new Elevation(0, "heightincm"),
                        new BoundsWithElevation(new Bounds(
                                new Point(57.645263671875, 10.404052734375),
                                new Point(57.6507568359375, 10.4150390625)
                        ), DEFAULT_ELEVATION)));

        /* decodes Curitiba */
        Assert.assertEquals(unlCore.decode("6gkzwgjz"),
                new PointWithElevation(new Point(-25.38262, -49.26561),
                        DEFAULT_ELEVATION,
                        new BoundsWithElevation(new Bounds(
                                new Point(-25.382709503173828, -49.265785217285156),
                                new Point(-25.382537841796875, -49.26544189453125)
                        ), DEFAULT_ELEVATION)));

        /* decodes Curitiba floor 5 */
        Assert.assertEquals(unlCore.decode("6gkzwgjz@5"),
                new PointWithElevation(new Point(-25.38262, -49.26561),
                        new Elevation(5, "floor"),
                        new BoundsWithElevation(new Bounds(
                                new Point(-25.382709503173828, -49.265785217285156),
                                new Point(-25.382537841796875, -49.26544189453125)
                        ), DEFAULT_ELEVATION)));

        /* decodes Curitiba heightincm 90 */
        Assert.assertEquals(unlCore.decode("6gkzwgjz#90"),
                new PointWithElevation(new Point(-25.38262, -49.26561),
                        new Elevation(90, "heightincm"),
                        new BoundsWithElevation(new Bounds(
                                new Point(-25.382709503173828, -49.265785217285156),
                                new Point(-25.382537841796875, -49.26544189453125)
                        ), DEFAULT_ELEVATION)));
    }


    @Test
    public void appendElevationTest() {
        /* get UnlCore instance */
        UnlCore unlCore = UnlCore.getInstance();

        /* appends elevation Curitiba 5th floor */
        Assert.assertEquals(unlCore.appendElevation("6gkzwgjz", new Elevation(5)),
                "6gkzwgjz@5"
        );

        /* appends elevation Curitiba above 87cm */
        Assert.assertEquals(unlCore.appendElevation("6gkzwgjz", new Elevation(87, "heightincm")),
                "6gkzwgjz#87"
        );
    }

    @Test
    public void excludeElevationTest() {
        /* get UnlCore instance */
        UnlCore unlCore = UnlCore.getInstance();

        /* excludes elevation Curitiba 5th floor */
        Assert.assertEquals(unlCore.excludeElevation("6gkzwgjz@5"),
                new LocationIdWithElevation(
                        "6gkzwgjz",
                        new Elevation(5, "floor")
                )
        );

        /* excludes elevation Curitiba above 87cm */
        Assert.assertEquals(unlCore.excludeElevation("6gkzwgjz#87"),
                new LocationIdWithElevation(
                        "6gkzwgjz",
                        new Elevation(87, "heightincm")
                )
        );
    }

    @Test
    public void adjacentTest() {
        /* get UnlCore instance */
        UnlCore unlCore = UnlCore.getInstance();

        /* adjacent north */
        Assert.assertEquals(unlCore.adjacent("ezzz@5", "n"), "gbpb@5");
    }

    @Test
    public void neighbourTest() {
        /* get UnlCore instance */
        UnlCore unlCore = UnlCore.getInstance();

        /* fetches neighbours */
        Assert.assertEquals(unlCore.neighbour("ezzz"),
                new Neighbour("gbpb", "u000", "spbp", "spbn", "ezzy", "ezzw", "ezzx", "gbp8"));

        /* fetches neighbours 5th floor */
        Assert.assertEquals(unlCore.neighbour("ezzz@5"),
                new Neighbour("gbpb@5", "u000@5", "spbp@5", "spbn@5", "ezzy@5", "ezzw@5", "ezzx@5", "gbp8@5"));

        /* fetches neighbours -2 floor */
        Assert.assertEquals(unlCore.neighbour("ezzz@-2"),
                new Neighbour("gbpb@-2", "u000@-2", "spbp@-2", "spbn@-2", "ezzy@-2", "ezzw@-2", "ezzx@-2", "gbp8@-2"));

        /* fetches neighbours above 87cm */
        Assert.assertEquals(unlCore.neighbour("ezzz#87"),
                new Neighbour("gbpb#87", "u000#87", "spbp#87", "spbn#87", "ezzy#87", "ezzw#87", "ezzx#87", "gbp8#87"));

        /* fetches neighbours below 5cm */
        Assert.assertEquals(unlCore.neighbour("ezzz#-5"),
                new Neighbour("gbpb#-5", "u000#-5", "spbp#-5", "spbn#-5", "ezzy#-5", "ezzw#-5", "ezzx#-5", "gbp8#-5"));
    }

    @Test
    public void gridLinesTest() {
        /* get UnlCore instance */
        UnlCore unlCore = UnlCore.getInstance();

        /* retrieves grid lines with precision 9 */
        Assert.assertEquals(unlCore.gridLines(
                new Bounds(
                        new Point(46.77210936378606, 23.595436614661565),
                        new Point(46.77227194246396, 23.59560827603795)
                ), 9
        ).size(), 7);

        /* retrieves grid lines with no precision specified (default 9) */
        Assert.assertEquals(unlCore.gridLines(
                new Bounds(
                        new Point(46.77210936378606, 23.595436614661565),
                        new Point(46.77227194246396, 23.59560827603795)
                )
        ).size(), 7);

        /* retrieves grid lines with precision 12 */
        Assert.assertEquals(unlCore.gridLines(
                new Bounds(
                        new Point(46.77210936378606, 23.595436614661565),
                        new Point(46.77227194246396, 23.59560827603795)
                ), 12
        ).size(), 1481);
    }
}
