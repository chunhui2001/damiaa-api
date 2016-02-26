package net.snnmo.assist;

import com.google.common.base.CharMatcher;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MediaType;
import java.util.UUID;

/**
 * Created by cc on 16/2/14.
 */
public class Common {
    public static String randomNumbers(int numberCount) {

        if (numberCount > 10 ) numberCount = 10;

        String rancomNumbers = null;

        do {

            rancomNumbers  = UUID.randomUUID().toString().replaceAll("[^0-9]+", "");

            if (rancomNumbers.indexOf('0') == 0)
                rancomNumbers = CharMatcher.is('0').trimFrom(rancomNumbers);

            if (rancomNumbers.length() >= numberCount) {
                rancomNumbers = rancomNumbers.substring(0, numberCount);
            }

        } while (rancomNumbers.length() < numberCount);

        return rancomNumbers;
    }

    public static boolean sendMail(String content) {
        // API Base URL:
        // https://api.mailgun.net/v3/mg.snnmo.com

        // API Key
        // key-a5179b50c49cbbea7aedcf1b12165d70

//        curl -s --user 'api:key-3ax6xnjp29jd6fds4gc373sgvjxteol0' \
//        https://api.mailgun.net/v3/mg.snnmo.com/messages \
//        -F from='Excited User <excited@samples.mailgun.org>' \
//        -F to='devs@mailgun.net' \
//        -F subject='Hello' \
//        -F text='Testing some Mailgun awesomeness!'

        // curl -s --user 'api:key-a5179b50c49cbbea7aedcf1b12165d70'
        // https://api.mailgun.net/v3/mg.snnmo.com/messages
        // -F from='Chunhui Zhang <mailgun@snnmo.com>'
        // -F to='18500183080@163.com'
        // -F to='chunhui2001@gmail.com'
        // -F subject='Hello Mailgun'
        // -F text='Testing some Mailgun awesomeness!'





        return true;
    }


    public static void SendSimpleMessage(String domainName, String apiKey, String username
            , String from, String[] to, String subject, String content, String contentType) {

        Runnable task = () -> {
            Client client = Client.create();
            client.addFilter(new HTTPBasicAuthFilter("api", apiKey));

            WebResource webResource =
                    client.resource(
                            "https://api.mailgun.net/v3/mg." + domainName +
                                    "/messages");

            MultivaluedMapImpl formData = new MultivaluedMapImpl();

            formData.add("from", username + from == null ? " <mailgun@" + from + ">" : " <" + from + ">");

            for (String email : to) {
                formData.add("to", email);
            }

            formData.add("subject", subject);
            formData.add(contentType, content);

            ClientResponse res = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).
                    post(ClientResponse.class, formData);
        };

        Thread thread = new Thread(task);
        thread.start();
    }
}
