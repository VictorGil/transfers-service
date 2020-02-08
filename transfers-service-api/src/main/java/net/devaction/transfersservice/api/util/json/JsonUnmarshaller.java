package net.devaction.transfersservice.api.util.json;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class JsonUnmarshaller<T> {
    private static final Logger log = LoggerFactory.getLogger(JsonUnmarshaller.class);

    private final ObjectReader objectReader;

    public JsonUnmarshaller(Class<T> clazz) {
        objectReader = new ObjectMapper().readerFor(clazz);
    }

    public T unmarshall(String jsonString) throws IOException {
        T object = null;
        try {
            object = objectReader.readValue(jsonString);
        } catch (IOException ex) {
            String errorMessage = "Error when trying to unmarshall the following JSON string:\n"
                    + jsonString;
            log.error(errorMessage, ex);
            throw new IOException(errorMessage, ex);
        }

        log.trace("Unmarshalled/deserialized object from JSON String:\n{}", object);
        return object;
    }
}
