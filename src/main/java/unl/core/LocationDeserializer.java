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
        Point sw = new Point(
                boundsJsonObject.getAsJsonObject("sw").get("lat").getAsDouble(),
                boundsJsonObject.getAsJsonObject("sw").get("lon").getAsDouble()
        );
        Point ne = new Point(
                boundsJsonObject.getAsJsonObject("ne").get("lat").getAsDouble(),
                boundsJsonObject.getAsJsonObject("ne").get("lon").getAsDouble()
        );
        Bounds bounds = new Bounds(sw, ne);

        return new Location(
                point,
                elevation,
                bounds,
                locationJsonObject.get("geohash").getAsString(),
                locationJsonObject.get("words").getAsString()
        );
    }
}
