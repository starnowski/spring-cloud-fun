package com.github.starnowski.spring.cloud.fun.gateway

import com.github.starnowski.spring.cloud.fun.gateway.model.Item
import com.github.starnowski.spring.cloud.fun.gateway.services.ItemService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.RequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor
import spock.lang.Specification
import spock.lang.Unroll

@WebMvcTest(ItemController)
class ItemControllerTest extends Specification {

    @Autowired
    MockMvc mockMvc

    @SpringBean
    ItemService itemService = Mock(ItemService)

    def "should return bad request when path variable size is outbound"(){
        when:
            def result = mockMvc.perform(MockMvcRequestBuilders.get("/item/{id}", "12341234123412341234123412341234"))
                    .andDo(MockMvcResultHandlers.print())

        then:
            result.andExpect(MockMvcResultMatchers.status().isBadRequest())
    }

    @Unroll
    def "should return expected item #item when id is #id"(){
        given:
            itemService.getItemById(id) >> item

        when:
            def result = mockMvc.perform(MockMvcRequestBuilders.get("/item/{id}", id))
                .andDo(MockMvcResultHandlers.print())

        then:
            result.andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json("{\"id\": \"dfasdfadf\", \"name\": \"deser\"}"))

        where:
            id                  |    item
            "dfasdfadf"         |   new Item("dfasdfadf", "deser")
    }
}
