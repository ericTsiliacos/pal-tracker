package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    private final String response;

    public WelcomeController(@Value("${WELCOME_MESSAGE}") String response) {
        this.response = response;
    }

    @GetMapping("/")
    public String sayHello() {
        return response;
    }
}
