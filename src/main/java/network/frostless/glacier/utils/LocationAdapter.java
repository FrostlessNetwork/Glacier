package network.frostless.glacier.utils;
import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;

/**
 * A gson adapter for {@link org.bukkit.Location}.
 *
 * @author Sasuke & Heroslender
 */
public class LocationAdapter implements JsonDeserializer<Location>, JsonSerializer<Location> {

    public static final LocationAdapter INSTANCE = new LocationAdapter();

    @Override
    public Location deserialize(JsonElement jsonString, Type type,
                                JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        if (!jsonString.isJsonObject()) {
            throw new JsonParseException("Invalid location");
        }

        final JsonObject obj = (JsonObject) jsonString;
        final JsonElement world = obj.get("world");
        final JsonElement x = obj.get("x");
        final JsonElement y = obj.get("y");
        final JsonElement z = obj.get("z");
        final JsonElement yaw = obj.get("yaw");
        final JsonElement pitch = obj.get("pitch");

        if (!x.isJsonPrimitive() || !((JsonPrimitive) x).isNumber()) {
            throw new JsonParseException("x nao retorna um numero");
        }

        if (!y.isJsonPrimitive() || !((JsonPrimitive) y).isNumber()) {
            throw new JsonParseException("y nao retorna um numero");
        }

        if (!z.isJsonPrimitive() || !((JsonPrimitive) z).isNumber()) {
            throw new JsonParseException("z nao retorna um numero");
        }

        if (yaw != null && (!yaw.isJsonPrimitive() || !((JsonPrimitive) yaw).isNumber())) {
            throw new JsonParseException("x nao retorna um numero");
        }

        if (pitch != null && (!pitch.isJsonPrimitive() || !((JsonPrimitive) pitch).isNumber())) {
            throw new JsonParseException("x nao retorna um numero");
        }

        World worldInstance = Bukkit.getWorld(world != null ? world.getAsString() : "none");

        return new Location(worldInstance, x.getAsDouble(), y.getAsDouble(), z.getAsDouble(),
                yaw != null ? yaw.getAsFloat() : 0.0F,
                pitch != null ?pitch.getAsFloat() : 0.0F);

    }

    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext jsonSerializationContext) {

        final JsonObject obj = new JsonObject();
        obj.addProperty("world", location.getWorld() != null ? location.getWorld().getName() : null);
        obj.addProperty("x", location.getX());
        obj.addProperty("y", location.getY());
        obj.addProperty("z", location.getZ());
        obj.addProperty("yaw", location.getYaw());
        obj.addProperty("pitch", location.getPitch());
        return obj;

    }

}