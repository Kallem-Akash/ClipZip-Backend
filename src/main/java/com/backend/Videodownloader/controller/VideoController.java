package com.backend.Videodownloader.controller;

import com.backend.Videodownloader.service.VideoDownloadService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
public class VideoController {

    private final VideoDownloadService service;
    public VideoController(VideoDownloadService service) { this.service = service; }

    @GetMapping("hello")
    public String greet(){
        return "hello";
    }

    @PostMapping("/download")
    public ResponseEntity<String> download(@RequestBody UrlRequest body) {
        return ResponseEntity.ok(service.fetchDownloadLinks(body.url()));
    }


    @GetMapping("/proxy")
    public void proxyVideo(@RequestParam String url, HttpServletResponse resp) {
        streamBinary(url, resp, false);
    }


    @GetMapping("/img-proxy")
    public void proxyImage(@RequestParam String url, HttpServletResponse resp) {
        streamBinary(url, resp, true);
    }


    private void streamBinary(String url, HttpServletResponse resp, boolean imageMode) {
        try {
            URL remote = new URL(url);
            HttpURLConnection con = (HttpURLConnection) remote.openConnection();
            con.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                            "(KHTML, like Gecko) Chrome/125.0 Safari/537.36");
            con.connect();
            if (con.getResponseCode() >= 400) {
                resp.sendError(502, "Remote fetch failed"); return;
            }


            String cType = con.getContentType();
            if (cType == null || cType.isBlank())
                cType = imageMode ? MediaType.IMAGE_JPEG_VALUE
                        : MediaType.APPLICATION_OCTET_STREAM_VALUE;
            resp.setContentType(cType);


            if (!imageMode) {
                String raw = remote.getPath().substring(remote.getPath().lastIndexOf('/') + 1);
                raw = raw.isBlank() ? "video" : raw.split("\\?")[0];
                String file = URLEncoder.encode(raw, StandardCharsets.UTF_8);
                if (!file.contains(".") && cType.startsWith("video/")) file += ".mp4";
                resp.setHeader("Content-Disposition", "attachment; filename=\"" + file + "\"");
            }

            int len = con.getContentLength();
            if (len > 0) resp.setContentLength(len);

            try (InputStream in = con.getInputStream();
                 OutputStream out = resp.getOutputStream()) {
                byte[] buf = new byte[16384];
                int n;
                while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
            }
        } catch (Exception e) {
            try { resp.sendError(500, "Proxy error"); } catch (Exception ignored) {}
        }
    }

    /* DTO for download */
    public record UrlRequest(String url) {}
}
