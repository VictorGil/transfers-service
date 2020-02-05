package net.devaction.transfersservice.api.util.json;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class FileReader {
    private static final Logger log = LoggerFactory.getLogger(FileReader.class);

    public String readFileFromClasspath(String filename) throws Exception {
        String content = null;
        try {
            content = Files.readString(Paths.get(this.getClass().getClassLoader().getResource(filename).toURI()), StandardCharsets.UTF_8);
        } catch (IOException | URISyntaxException ex) {
            String errorMessage = "Unable to read file from classpath, filename: " + filename;
            log.error(errorMessage, ex);
            throw new Exception(errorMessage, ex);
        }
        return content;
    }
}
