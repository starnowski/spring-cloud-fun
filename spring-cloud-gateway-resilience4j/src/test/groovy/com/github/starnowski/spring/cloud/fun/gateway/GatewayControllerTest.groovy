package com.github.starnowski.spring.cloud.fun.gateway

import com.github.starnowski.spring.cloud.fun.gateway.model.User
import com.github.tomakehurst.wiremock.WireMockServer
import com.maciejwalkowiak.wiremock.spring.ConfigureWireMock
import com.maciejwalkowiak.wiremock.spring.EnableWireMock
import com.maciejwalkowiak.wiremock.spring.InjectWireMock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.core.env.Environment
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo

//@SpringBootTest
//@EnableWireMock(
//    @ConfigureWireMock(name = "user-service", property = "remote.home")
//)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class GatewayControllerTest extends Specification {

    @InjectWireMock("user-service")
    private WireMockServer wiremock;

    @Autowired
    private TestRestTemplate testRestTemplate

    @Autowired
    private Environment env;

    def "should forward request to wiremock server and pass through response from wiremock"() {
        given:
            stubFor(get(urlEqualTo("/test.json")).willReturn(
                    aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    { "id": 1, "userId": 1, "title": "my todo" }
                                    """)
            )
            )

        when:
            def result = testRestTemplate.getForEntity("/test", User)

        then:
            result.getBody().id() == 1
            result.getBody().userId() == 1
            result.getBody().title() == "my todo"
    }

    def "should return fall back and return dummy user when wiremock returns 400 response"() {
        given:
            stubFor(get(urlEqualTo("/service1")).willReturn(
                    aResponse()
                    .withStatus(400)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                        { "message": "invalid user" }
                                        """)
            )
            )

        when:
            def result = testRestTemplate.getForEntity("/test-with-circuit-breaker", User)

        then:
            result.getBody().id() == -1
            result.getBody().userId() == -1
            result.getBody().title() == "Dummy user"
    }
}
