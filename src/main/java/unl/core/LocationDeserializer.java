package unl.core;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class LocationDeserializer implements JsonDeserializer<Location> {

    @Override
    public Location deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonObject locationJsonObject = jsonObject.getAsJsonObject("location");
        JsonObject boundsJsonObject = locationJsonObject.getAsJsonObject("bounds");

        Point point = new Point(
                locationJsonObject.get("lat").getAsDouble(),
                locationJsonObject.get("lon").getAsDouble()
        );
        Elevation elevation = new Elevation(
                locationJsonObject.get("elevation").getAsInt(),
                locationJsonObject.get("elevationType").getAsString()
        );

        double n = boundsJsonObject.getAsJsonObject("ne").get("lat").getAsDouble();
        double e = boundsJsonObject.getAsJsonObject("ne").get("lon").getAsDouble();
        double s = boundsJsonObject.getAsJsonObject("sw").get("lat").getAsDouble();
        double w = boundsJsonObject.getAsJsonObject("sw").get("lon").getAsDouble();

        Bounds bounds = new Bounds(n, e, s, w);

        return new Location(
                point,
                elevation,
                bounds,
                locationJsonObject.get("geohash").getAsString(),
                locationJsonObject.get("words").getAsString()
        );
    }
}
