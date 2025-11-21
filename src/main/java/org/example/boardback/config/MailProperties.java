package org.example.boardback.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter @Setter
@Component
@ConfigurationProperties(prefix = "spring.mail")
public class MailProperties {
    private String host;
    private int port;
    private String username;
    private String password;

    @org.springframework.beans.factory.annotation.Value("${app.mail.from}")
    private String from;
}