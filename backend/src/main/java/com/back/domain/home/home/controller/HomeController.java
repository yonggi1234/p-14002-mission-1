package com.back.domain.home.home.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static java.net.InetAddress.getLocalHost;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

@RestController
@Tag(name = "HomeController", description = "홈 컨트롤러")
public class HomeController {
    @SneakyThrows
    @GetMapping(produces = TEXT_HTML_VALUE)
    @Operation(summary = "메인 페이지")
    public String main() {
        InetAddress localHost = getLocalHost();

        return """
                <h1>API 서버</h1>
                <p>Host Name: %s</p>
                <p>Host Address: %s</p>
                <div>
                    <a href="/swagger-ui/index.html">API 문서로 이동</a>
                </div>
                """.formatted(localHost.getHostName(), localHost.getHostAddress());
    }

    @GetMapping("/session")
    @Operation(summary = "세션 확인")
    public Map<String, Object> session(HttpSession session) {
        return Collections.list(session.getAttributeNames()).stream()
                .collect(Collectors.toMap(
                        name -> name,
                        session::getAttribute
                ));
    }
}
