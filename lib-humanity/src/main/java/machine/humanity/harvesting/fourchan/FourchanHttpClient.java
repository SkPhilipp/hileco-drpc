package machine.humanity.harvesting.fourchan;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class FourchanHttpClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String BASE_URL = "http://api.4chan.org/";
    /**
     * A response handler which returns the content body as a string when the status is 200 to 299.
     */
    private static final ResponseHandler<String> STREAM_RESPONSE_HANDLER = response -> {
        int status = response.getStatusLine().getStatusCode();
        if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
    };

    public JsonNode get(String path) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpget = new HttpGet(BASE_URL + path);
            String content = httpclient.execute(httpget, STREAM_RESPONSE_HANDLER);
            return MAPPER.readTree(content);
        }
    }

}
