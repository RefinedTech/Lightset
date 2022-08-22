package dev.refinedtech.lightset;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiConsumer;

@SuppressWarnings({"unchecked", "unused"})
public class Lightset {

    private final HashMap<String, Object> loaded = new HashMap<>();

    private Lightset() {}

    /**
     * Reads from the input stream, line by line and parses the lines into a map.
     *
     * @param stream The input stream to read from.
     *               The stream will not be closed.
     * @return A lightset instance.
     * @throws IOException If an error occurs while reading from the stream.
     */
    public static Lightset load(InputStream stream) throws IOException {
        Lightset lightset = new Lightset();
        lightset.loadFromStream(stream);
        return lightset;
    }

    /**
     * Creates an input stream from the given path and loads it.
     *
     * @param path The path to read from.
     * @return A lightset instance.
     * @throws IOException If an error occurs while reading from the path.
     * @see #load(InputStream)
     */
    public static Lightset load(Path path) throws IOException {
        try(InputStream is = Files.newInputStream(path)) {
            return load(is);
        }
    }

    /**
     * Creates an input stream from the given file and loads it.
     *
     * @param file The file to read from.
     * @return A lightset instance.
     * @throws IOException If an error occurs while reading from the file.
     * @see #load(InputStream)
     * @see #load(Path)
     */
    public static Lightset load(File file) throws IOException {
        return load(file.toPath());
    }

    /**
     * Creates an input stream from the given path and loads it.
     *
     * @param path The path to read from.
     * @return A lightset instance.
     * @throws IOException If an error occurs while reading from the path.
     * @see #load(InputStream)
     * @see #load(Path)
     */
    public static Lightset load(String path) throws IOException {
        return load(Paths.get(path));
    }


    /**
     * Creates an input stream from the given resource and loads it.
     *
     * @param path The path to read from.
     * @return A lightset instance.
     * @throws IOException If an error occurs while reading from the path.
     * @see #load(InputStream)
     */
    public static Lightset resource(String path) throws IOException {
        try(InputStream stream = Lightset.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null)
                throw new FileNotFoundException("Resource not found: " + path);

            return load(stream);
        }
    }

    /**
     * Creates an input stream from the given resource and loads it.
     *
     * @param file The file to read from.
     * @return A lightset instance.
     * @throws IOException If an error occurs while reading from the file.
     * @see #load(InputStream)
     * @see #resource(String)
     */
    public static Lightset resource(File file) throws IOException {
        return resource(file.getPath());
    }

    /**
     * Creates an input stream from the given resource and loads it.
     *
     * @param path The path to read from.
     * @return A lightset instance.
     * @throws IOException If an error occurs while reading from the path.
     * @see #load(InputStream)
     * @see #resource(String)
     */
    public static Lightset resource(Path path) throws IOException {
        return resource(path.toString());
    }

    /**
     * Gets the input stream from the connection and loads it.
     *
     * @param connection The connection to read from.
     * @return A lightset instance.
     * @throws IOException If an error occurs while reading from the connection.
     * @see #load(InputStream)
     */
    public static Lightset connection(URLConnection connection) throws IOException {
        try(InputStream stream = connection.getInputStream()) {
            return load(stream);
        }
    }

    /**
     * Connects to the URL and loads it.
     *
     * @param url The URL to read from.
     * @return A lightset instance.
     * @throws IOException If an error occurs while reading from the URL.
     * @see #load(InputStream)
     * @see #connection(URLConnection)
     */
    public static Lightset url(URL url) throws IOException {
        return connection(url.openConnection());
    }

    /**
     * Connects to the URL and loads it.
     *
     * @param url The URL to read from.
     * @return A lightset instance.
     * @throws IOException If an error occurs while reading from the URL.
     * @see #load(InputStream)
     * @see #url(URL)
     */
    public static Lightset url(String url) throws IOException {
        return url(new URL(url));
    }

    /**
     * Gets the bytes for the input string and wraps them into a ByteArrayInputStream.
     * This is useful for loading from a string.
     *
     * @param sequence The sequence to read from.
     * @return A lightset instance.
     * @throws IOException If an error occurs while reading from the string.
     * @see #load(InputStream)
     */
    public static Lightset string(CharSequence sequence) throws IOException {
        try(InputStream stream = new ByteArrayInputStream(sequence.toString().getBytes(StandardCharsets.UTF_8))) {
            return load(stream);
        }
    }

    /**
     * Iterate over the loaded properties.
     *
     * @param consumer The consumer to call for each property.
     *                 The consumer will be called with the key and value.
     * @return This lightset instance.
     */
    public Lightset each(BiConsumer<String, Object> consumer) {
        loaded.forEach(consumer);
        return this;
    }

    /**
     * Gets the value for the given key.
     *
     * @param key The key to get the value for.
     *            If the key is not found, the default value is returned.
     * @param def The default value to return if the key is not found.
     *            If the default value is null, then an empty Optional is returned.
     * @return The value for the key.
     */
    public Optional<Object> getRaw(String key, Object def) {
        return Optional.ofNullable(this.getNullableRaw(key, def));
    }

    /**
     * Gets the value for the given key.
     *
     * @param key The key to get the value for.
     *            If the key is not found, the default value is returned.
     * @param def The default value to return if the key is not found.
     *            If the default value is null, then null is returned.
     *
     * @return The value for the key.
     */
    public Object getNullableRaw(String key, Object def) {
        return loaded.getOrDefault(key, def);
    }

    /**
     * Gets the value for the given key.
     *
     * @param key The key to get the value for.
     *            If the key is not found, then an empty Optional is returned.
     * @return The value for the key.
     */
    public Optional<Object> getRaw(String key) {
        return Optional.ofNullable(this.getNullableRaw(key));
    }

    /**
     * Gets the value for the given key.
     *
     * @param key The key to get the value for.
     *            If the key is not found, then null is returned.
     * @return The value for the key.
     */
    public Object getNullableRaw(String key) {
        return loaded.get(key);
    }

    /**
     * Gets the value for the given key, converted to the given type.
     *
     * @param key The key to get the value for.
     *            If the key is not found, the default value is returned.
     * @param def The default value to return if the key is not found.
     *            If the default value is null, then an empty Optional is returned.
     * @return The value for the key.
     */
    public <T> Optional<T> get(String key, T def) {
        return Optional.ofNullable(this.getNullable(key,def));
    }

    /**
     * Gets the value for the given key, converted to the given type.
     *
     * @param key The key to get the value for.
     *            If the key is not found, the default value is returned.
     * @param def The default value to return if the key is not found.
     *            If the default value is null, then null is returned.
     * @return The value for the key.
     */
    public <T> T getNullable(String key, T def) {
        return (T) loaded.getOrDefault(key, def);
    }

    /**
     * Gets the value for the given key, converted to the given type.
     *
     * @param key The key to get the value for.
     *            If the key is not found, then an empty Optional is returned.
     * @return The value for the key.
     */
    public <T> Optional<T> get(String key) {
        return Optional.ofNullable(this.getNullable(key));
    }

    /**
     * Gets the value for the given key, converted to the given type.
     *
     * @param key The key to get the value for.
     *            If the key is not found, then null is returned.
     * @return The value for the key.
     */
    public <T> T getNullable(String key) {
        return (T) loaded.get(key);
    }

    private void loadFromStream(InputStream is) throws IOException {
        try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while((line = br.readLine()) != null) {

                // Ignore comments, and empty lines
                if(line.startsWith("#") || line.trim().isEmpty()) continue;

                int index = line.indexOf('=');

                // No equals sign, ignore the line
                if(index == -1)
                    continue;

                loaded.put(line.substring(0, index), tryParse(line.substring(index + 1)));
            }
        }
    }

    private Object tryParse(String value) {
        if(value.equalsIgnoreCase("true")) return true;
        if(value.equalsIgnoreCase("false")) return false;
        Number n = tryParseNumber(value);
        if(n != null) return n;
        return value;
    }

    private Number tryParseNumber(String value) {
        if (value.toLowerCase().endsWith("f")) {
            try {
                return Float.parseFloat(value);
            } catch (NumberFormatException ignored) {

            }
        }

        if (value.toLowerCase().endsWith("d")) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException ignored) {

            }
        }

        try {
            return Byte.parseByte(value);
        } catch(NumberFormatException ignored) {

        }

        try {
            return Short.parseShort(value);
        } catch(NumberFormatException ignored) {

        }

        try {
            return Integer.parseInt(value);
        } catch(NumberFormatException ignored) {

        }

        try {
            return Long.parseLong(value);
        } catch(NumberFormatException ignored) {

        }

        try {
            Number val = Float.parseFloat(value);
            if (!Float.isInfinite(val.floatValue()))
                return val;
        } catch(NumberFormatException ignored) {

        }

        try {
            return Double.parseDouble(value);
        } catch(NumberFormatException ignored) {

        }
        return null;
    }

}
