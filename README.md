# UNL Core Java

This library can be used to convert a UNL locationId to/from a latitude/longitude point. It also contains helper functions like retrieving the bounds of a UNL cell or the UNL grid lines for a given boundingbox (these can be used to draw a UNL cell or a UNL grid).

## Installation

You must use a personal access token with the appropriate scopes to publish and install packages in GitHub Packages. You can generate a token in your [account settings page](https://github.com/settings/tokens). 

### Maven project

To add the package as a dependecy to a Maven project the following steps need to be done: 

1. Authenticate to GithubPackages 
Add the dependency to your pom.xml file:

You can authenticate to GitHub Packages with Apache Maven by editing your settings.xml file to include your personal access token. Replace USERNAME with your GitHub username and TOKEN with your personall access token:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <activeProfiles>
    <activeProfile>github</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>github</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
          <releases><enabled>true</enabled></releases>
          <snapshots><enabled>true</enabled></snapshots>
        </repository>
        <repository>
          <id>github</id>
          <name>GitHub u-n-l Apache Maven Packages</name>
          <url>https://maven.pkg.github.com/u-n-l/core-java</url>
        </repository>
      </repositories>
    </profile>
  </profiles>

  <servers>
    <server>
      <id>github</id>
      <username>USERNAME</username>
      <password>TOKEN</password>
    </server>
  </servers>
</settings>

```

2. Add the following dependecy tag to the dependencies element of your project pom.xml file:

```xml
<dependency>
  <groupId>unl</groupId>
  <artifactId>core</artifactId>
  <version>0.0.2</version>
</dependency>
```

3. Install the package by running the following command from the same level with your pom.xml file:
```bash
mvn install
```

### Gradle project

To add the package as a dependency to your gradle project you must:
1. Authenticate to GitHubPackages. Replace USERNAME with your GitHub username and TOKEN with your personal access token. 

```
repositories {
      maven {
          name = "GitHubPackages"
          url = uri("https://maven.pkg.github.com/OWNER/REPOSITORY")
          credentials {
              username = project.findProperty("gpr.user") ?: System.getenv("USERNAME")
              password = project.findProperty("gpr.key") ?: System.getenv("TOKEN")
          }
      }
  }
```
2. Add the unl core package dependency to your build.gradle file:
```
implementation 'unl:core:0.0.2'
```

3. Add the maven plugin to your build.gradle file:

```
plugins {
    id 'maven'
}
```
4. Install the package:

```bash
$ gradle install
```

For more information, see the official guide: [Configuring Gradle for use with GitHub Packages](https://docs.github.com/en/packages/guides/configuring-gradle-for-use-with-github-packages).

## Unit Tests

To run the unit test classes, run the following command in the root directory of the project:

```bash
mvn test
```

## Classes

### Point 

```java
public class Point {
  private double lat;
  private double lon;

  public Point(double lat, double lon) {
    this.lat = lat;
    this.lon = lon;
  }
}
```

### Bounds 

```java
public class Bounds {@NotNull
  private Point sw;@NotNull
  private Point ne;

  public Bounds(@NotNull Point sw, @NotNull Point ne) {
    this.sw = sw;
    this.ne = ne;
  }
}
```

### Elevation

```java
public class Elevation {
  private int elevation;@NotNull
  private String elevationType;

  public Elevation(int elevation, @NotNull String elevationType) {
    this.elevation = elevation;
    this.elevationType = elevationType;
  }
}
```

### BoundsWithElevation 

```java
public class BoundsWithElevation {@NotNull
  private Bounds bounds;@NotNull
  private Elevation elevation;

  public BoundsWithElevation(@NotNull Bounds bounds, @NotNull Elevation elevation) {
    this.bounds = bounds;
    this.elevation = elevation;
  }
}
```

### PointWithElevation

```java
public class PointWithElevation {@NotNull
  public Point coordinates;@NotNull
  public Elevation elevation;@NotNull
  public BoundsWithElevation bounds;

  public PointWithElevation(@NotNull Point coordinates, @NotNull Elevation elevation, @NotNull BoundsWithElevation bounds) {
    this.coordinates = coordinates;
    this.elevation = elevation;
    this.bounds = bounds;
  }

  public PointWithElevation(@NotNull Point coordinates, @NotNull BoundsWithElevation bounds) {
    this(coordinates, UnlCore.DEFAULT_ELEVATION, bounds);
  }
}
```

### LocationIdWithElevation 

```java
public class LocationIdWithElevation {@NotNull
  private String locationId;
  private Elevation elevation;

  public LocationIdWithElevation(@NotNull String locationId, @NotNull Elevation elevation) {
    this.locationId = locationId;
    this.elevation = elevation;
  }

  public LocationIdWithElevation(@NotNull String locationId) {
    this(locationId, UnlCore.DEFAULT_ELEVATION);
  }
}
```

### Neighbours 

```java
public class Neighbours {
  @NotNull
  private String n;
  @NotNull
  private String ne;
  @NotNull
  private String e;
  @NotNull
  private String se;
  @NotNull
  private String s;
  @NotNull
  private String sw;
  @NotNull
  private String w;
  @NotNull
  private String nw;

  public Neighbours(@NotNull String n, @NotNull String ne, @NotNull String e, @NotNull String se, @NotNull String s, @NotNull String sw, @NotNull String w, @NotNull String nw) {
    this.n = n;
    this.ne = ne;
    this.e = e;
    this.se = se;
    this.s = s;
    this.sw = sw;
    this.w = w;
    this.nw = nw;
  }
}
```

### Location 

```java
public class Location {
@NotNull
  private Point point;@NotNull
  private Elevation elevation;@NotNull
  private Bounds bounds;@NotNull
  private String geohash;@NotNull
  private String words;

  public Location(@NotNull Point point, @NotNull Elevation elevation, @NotNull Bounds bounds, @NotNull String geohash, @NotNull String words) {
    this.point = point;
    this.elevation = elevation;
    this.bounds = bounds;
    this.geohash = geohash;
    this.words = words;
  }
}
```

## UnlCore methods

You can import the UnlCore class into your file, to call any of the methods describe below:

```java
import unl.core.UnlCore;
```
### encode

Encodes latitude/longitude coordinates to locationId. There are multiple signatures for the encode method.

The encoding is done to the given precision. The last parameter is used to specify the elevation information: number and type (floor | "heightincm").
```java
public static String encode(double lat, double lon, int precision, Elevation elevation)
```


Example:
```java
UnlCore.encode(57.648, 10.41, 6, new Elevation(87, "heightincm"));
```
Returns:
```java
"u4pruy#87"
```

The precision and/or elevation can be skipped from the parameters. In this case, the default values will be used:

```java
public final static int DEFAULT_PRECISION = 9;
public final static Elevation DEFAULT_ELEVATION = new Elevation(0, "floor");
```


```java
public static String encode(double lat, double lon, int precision);
```

Example:
```java
UnlCore.encode(57.648, 10.41, 6);
```
Returns:
```java
"u4pruy"
```

```java
public static String encode(double lat, double lon, @NotNull Elevation elevation)
```

Example:
```java
UnlCore.encode(52.37686, 4.90065, new Elevation(-2));
```
Returns:
```java
"u173zwbt3@-2"
```

```java
public static String encode(double lat, double lon)
```
Example:
```java
UnlCore.encode(52.37686, 4.90065);
```

Returns:
```java
"u173zwbt3"
```

### Decode

```java
public static PointWithElevation decode(@NotNull String locationId);
```
Decodes a locationId to latitude/longitude (location is approximate centre of locationId cell, to reasonable precision).

Example: 

```java
UnlCore.decode("u173zwbt3");
```

Returns a PointWithElevation object. Below is the json representation of the returned object:

```java
{
   "coordinates":{
      "lat":52.376869,
      "lon":4.900653
   },
   "elevation":{
      "elevation":0,
      "elevationType":"floor"
   },
   "bounds":{
      "bounds":{
         "sw":{
            "lat":52.37684726715088,
            "lon":4.900631904602051
         },
         "ne":{
            "lat":52.37689018249512,
            "lon":4.900674819946289
         }
      },
      "elevation":{
         "elevation":0,
         "elevationType":"floor"
      }
   }
}
```

### Bounds
Returns SW/NE latitude/longitude bounds of specified locationId cell, along with the elevation information.

```java
public static BoundsWithElevation bounds(@NotNull String locationId);
```

Example:
```java
UnlCore.bounds("u173zwbt3");
```

Returns a BoundsWithElevation object. Below is the json representation of the returned object:

```java
{
   "bounds":{
      "sw":{
         "lat":52.37684726715088,
         "lon":4.900631904602051
      },
      "ne":{
         "lat":52.37689018249512,
         "lon":4.900674819946289
      }
   },
   "elevation":{
      "elevation":0,
      "elevationType":"floor"
   }
}
``` 

## gridLines
Returns the vertical and horizontal lines that can be used to draw a UNL grid in the specified
SW/NE latitude/longitude bounds and precision. Each line is represented by an array of two
coordinates in the format: [[startLon, startLat], [endLon, endLat]].

```java
public static List<double[][]> gridLines(@NotNull Bounds bounds, int precision);
```

If the precision parameter is not passed, the default precision will be used: 9.

Example:

```java
Point sw = new Point(46.77210936378606, 23.595436614661565);
Point ne = new Point(46.77227194246396, 23.59560827603795);
Bounds bounds = new Bounds(sw, ne);

UnlCore.gridLines(bounds, 12);
```

Will return an ArrayList of length 1481, containing the array of lines:
```
[[startLon, startLat], [endLon, endLat]]
   ...
```

## adjacent 
Determines adjacent cell in given direction: "N" | "S" | "E" | "W".

```java
public static String adjacent(@NotNull String locationId, @NotNull String direction)
```

Example:

```java
UnlCore.adjacent("ezzz@5", "N");
```

Returns a string:

```java
"gbpb@5"
````

### neighbours

Returns all 8 adjacent cells to specified locationId.

```java
UnlCore.neighbours("ezzz");
```

Returns a Neighbour object, containing the 8 adjacent cells to specified locationId. Below is the JSON representation of the object:

```java
{
   "n":"gbpb",
   "ne":"u000",
   "e":"spbp",
   "se":"spbn",
   "s":"ezzy",
   "sw":"ezzw",
   "w":"ezzx",
   "nw":"gbp8"
}
```

### excludeElevation

Returns an instance of LocationIdWithElevation, containing the locationId and elevation properties. It is mainly used by internal functions.

```java
public static LocationIdWithElevation excludeElevation(@NotNull String locationIdWithElevation);
```

Example:

```java
UnlCore.excludeElevation("6gkzwgjz@5");
```

Returns a LocationIdWithElevation object. The JSON representation:

```java
{
   "locationId":"6gkzwgjz",
   "elevation":{
      "elevation":5,
      "elevationType":"floor"
   }
}
```

### appendElevation 

Adds elevation chars and elevation to a locationId. It is mainly used by internal functions.

```java
public static String appendElevation(@NotNull String locationIdWithoutElevation, @NotNull Elevation elevation);
```

Example:

```java
Elevation elevation = new Elevation(5, "floor"):
UnlCore.appendElevation("6gkzwgjz", elevation);
```

Returns a string: 
```java
"6gkzwgjz@5"
```

### toWords
 Returns the location object, which encapsulates the coordinates, elevation, bounds, geohash and words,
 corresponding to the location string (id or lat-lon coordinates). It requires the api key used to access
 the location APIs.

```java
public static Location toWords(@NotNull String location, @NotNull String apiKey) throws UnlCoreException
```
### words

Returns the location object, which encapsulates the coordinates, elevation, bounds, geohash and words,
corresponding to the words string. It requires the api key used to access
the location APIs.

```java
public static Location words(@NotNull String words, @Nullable String apiKey) throws UnlCoreException
```
In case of _words_ and _toWords_ methods, a UnlCoreException will be thrown if the request to the locationAPI is not sucessful. 

## Contributing
Pull requests are welcome.

Please make sure to update tests as appropriate.

## License
Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
