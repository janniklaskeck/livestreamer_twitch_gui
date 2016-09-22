/**
 * MIT License
 *
 * Copyright (c) 2016 Jan-Niklas Keck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package app.lsgui.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public final class JsonUtils {

    private JsonUtils() {
    }

    public static int getIntSafe(final JsonElement element, final int defaultValue) {
        if (element != null && !element.isJsonNull()) {
            return element.getAsInt();
        }
        return defaultValue;
    }

    public static boolean getBooleanSafe(final JsonElement element, final boolean defaultValue) {
        if (element != null && !element.isJsonNull()) {
            return element.getAsBoolean();
        }
        return defaultValue;
    }

    public static String getStringSafe(final JsonElement element, final String defaultValue) {
        if (element != null && !element.isJsonNull()) {
            return element.getAsString();
        }
        return defaultValue;
    }

    public static String getStringIfNotNull(final String name, final JsonObject obj) {
        if (obj.has(name) && !obj.get(name).isJsonNull()) {
            return obj.get(name).getAsString();
        }
        return "";
    }

    public static boolean getBooleanIfNotNull(final String name, final JsonObject obj) {
        if (obj.has(name) && !obj.get(name).isJsonNull()) {
            return obj.get(name).getAsBoolean();
        }
        return false;
    }

    public static int getIntegerIfNotNull(final String name, final JsonObject obj) {
        if (obj.has(name) && !obj.get(name).isJsonNull()) {
            return obj.get(name).getAsInt();
        }
        return 0;
    }

    public static JsonArray getJsonArraySafe(final String name, final JsonObject obj) {
        if (obj.has(name) && !obj.get(name).isJsonNull() && obj.get(name).isJsonArray()) {
            return obj.get(name).getAsJsonArray();
        }
        return new JsonArray();
    }
}
