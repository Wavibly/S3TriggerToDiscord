import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;

public class S3TriggerToDiscord implements RequestHandler<S3Event, String> {

    // TODO enter your Discord webhook URL here
    private static final String DISCORD_WEBHOOK_URL = "YOUR_WEBHOOK_HERE";

    private static final String S3_OBJECT_URL_TEMPLATE = "https://%s.s3.amazonaws.com/%s";

    @Override
    public String handleRequest(S3Event s3Event, Context context) {

        S3EventNotification.S3EventNotificationRecord record = s3Event.getRecords().get(0);

        String s3BucketName = record.getS3().getBucket().getName();
        System.out.println("S3 Bucket Name: " + s3BucketName);
        String s3ObjectUrlDecodedKey = record.getS3().getObject().getUrlDecodedKey();
        System.out.println("S3 Object URL-Decoded Key: " + s3ObjectUrlDecodedKey);

        String s3ObjectUrl = String.format(S3_OBJECT_URL_TEMPLATE, s3BucketName, s3ObjectUrlDecodedKey);
        System.out.println("S3 Object URL: " + s3ObjectUrl);

        String s3ObjectKey = record.getS3().getObject().getKey();
        System.out.println("S3 Object Key: " + s3ObjectKey);
        String[] s3ObjectKeySplits = s3ObjectKey.split("/");
        String s3ObjectName = s3ObjectKeySplits[s3ObjectKeySplits.length-1];
        System.out.println("S3 Object Name: " + s3ObjectName);

        String webhookPayloadTemplate = "{" +
                "\"content\": \"%s\"," +
                "\"embeds\": [{" +
                "\"image\": {" +
                "\"url\": \"%s\"" +
                "}" +
                "}]" +
                "}";
        System.out.println("Payload JSON Template:\n" + webhookPayloadTemplate);

        String webhookPayload = String.format(webhookPayloadTemplate, s3ObjectName, s3ObjectUrl);
        System.out.println("Payload JSON:\n" + webhookPayload);

        String response = sendDiscordWebhook(DISCORD_WEBHOOK_URL, webhookPayload);
        System.out.println("Discord Webhook Response:\n" + response);
        return response;
    }

    private String sendDiscordWebhook(String webhookUrl, String webhookPayloadJson) {

        HttpClient httpClient = HttpClientBuilder.create().build();
        StringEntity requestEntity = new StringEntity(
                webhookPayloadJson,
                ContentType.APPLICATION_JSON);

        HttpPost postMethod = new HttpPost(webhookUrl);
        postMethod.setEntity(requestEntity);

        try {
            HttpResponse rawResponse = httpClient.execute(postMethod);
            return rawResponse.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
