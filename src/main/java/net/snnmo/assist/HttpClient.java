package net.snnmo.assist;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cc on 16/7/25.
 */
public class HttpClient {

    private final static String _USER_AGENT = "Mozilla/5.0";

    public static String post(String uri, Map<String, Object> params, Map<String, Object> headers) throws Exception {

        StringBuffer result = new StringBuffer();
        HttpPost post = new HttpPost(uri);
        Map<String, Object> urlParameters = params;
        BufferedReader rd = null;
        String line = "";
        HttpResponse response = null;
        Gson gson = new Gson();

        post.setHeader("User-Agent", _USER_AGENT);
        post.setHeader("Content-Type", "application/json");
        post.setHeader("Accept", "application/json");

        post.setEntity(new StringEntity(gson.toJson(urlParameters), ContentType.APPLICATION_JSON));

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

            response = httpClient.execute(post);

            rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

        } catch (IOException e) {

            throw new Exception(e);

        }

        System.out.println("Sending 'POST' request to URL : " + uri);
        System.out.println("Post parameters : " + post.getEntity());
        System.out.println("Response Code : " +
                response.getStatusLine().getStatusCode());

        System.out.println("==============================");
        System.out.println(result.toString());

        return result.toString();
    }
}
