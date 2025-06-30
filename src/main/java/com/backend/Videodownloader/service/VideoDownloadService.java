package com.backend.Videodownloader.service;

import com.backend.Videodownloader.RapidApiProperties;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class VideoDownloadService {

    private final RapidApiProperties props;
    private final HttpClient http;   // reuse one client

    public VideoDownloadService(RapidApiProperties props) {
        this.props = props;
        this.http  = HttpClient.newHttpClient();
    }

    /**
     * Calls RapidAPI and returns the JSON response as a String.
     */
    public String fetchDownloadLinks(String videoUrl) {

        // 1️⃣  JSON body to send
        String json = String.format("{\"url\":\"%s\"}", videoUrl);

        // 2️⃣  Build the request (this is the snippet you asked about)
        String endpoint = String.format("https://%s/v1/social/autolink", props.host());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("x-rapidapi-key",  props.key())
                .header("x-rapidapi-host", props.host())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        // 3️⃣  Send it and return the body
        try {
            HttpResponse<String> response =
                    http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("RapidAPI error " + response.statusCode() +
                        ": " + response.body());
            }
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Failed to contact RapidAPI", e);
        }
    }
}
