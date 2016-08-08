package ca.ontario.ecorr.util;

import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility methods for writing tests that deal with JSON.
 *  
 * @author brouwerto
 *
 */
public class TestUtil {
	/**
	 * A media type of JSON in UTF8.
	 */
	public static final MediaType APPLICATION_JSON_UTF8 =
			new MediaType(MediaType.APPLICATION_JSON.getType(),
							MediaType.APPLICATION_JSON.getSubtype(),
							Charset.forName("utf8"));

	/**
	 * A media type of JSON.
	 */
	public static final MediaType APPLICATION_JSON =
			new MediaType(MediaType.APPLICATION_JSON.getType(),
							MediaType.APPLICATION_JSON.getSubtype());
	
	/**
	 * Convert an object to JSON.
	 * 
	 * @param object The object to convert
	 * @return byte array with JSON representation
	 * @throws IOException
	 */
    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }
 
    /**
     * Create a string of "a"s with specified length.
     * 
     * @param length The length of the string to return.
     * @return The string
     */
    public static String createStringWithLength(int length) {
        StringBuilder builder = new StringBuilder();
 
        for (int index = 0; index < length; index++) {
            builder.append("a");
        }
 
        return builder.toString();
    }
}
