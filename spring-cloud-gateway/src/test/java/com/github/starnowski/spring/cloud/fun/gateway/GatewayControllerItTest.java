package com.github.starnowski.spring.cloud.fun.gateway;

import com.github.starnowski.spring.cloud.fun.gateway.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class GatewayControllerItTest {

    @Autowired
    private TestRestTemplate testRestTemplate;


    @Test
    public void getTestJson() throws Exception {
        // GIVEN
        stubFor(get(urlEqualTo("/test.json")).willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("""
                                { "id": 1, "userId": 1, "title": "my todo" }
                                """)
                        )
                );

        // WHEN
        var result = testRestTemplate.getForEntity("/test", User.class);

        // THEN
        assertEquals(result.getBody().id(), 1);
        assertEquals(result.getBody().userId(), 1);
        assertEquals(result.getBody().title(), "my todo");
    }
}