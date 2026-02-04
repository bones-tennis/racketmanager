package com.example.racketmanager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LineMessagingService {

    @Value("${line.channel-access-token}")
    private String accessToken;

    private static final String PUSH_URL = "https://api.line.me/v2/bot/message/push";

    /**
     * LINEにPushでテキスト送信
     */
    public void pushText(String lineUserId, String message) {
        if (lineUserId == null || lineUserId.isBlank()) return;

        String json = """
        {
          "to": "%s",
          "messages": [
            { "type": "text", "text": "%s" }
          ]
        }
        """.formatted(escape(lineUserId), escape(message));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        new RestTemplate().postForEntity(PUSH_URL, entity, String.class);
    }

    // JSON文字列用の簡易エスケープ（" と \ と改行だけ）
    private String escape(String s) {
        return s
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "");
    }
}
