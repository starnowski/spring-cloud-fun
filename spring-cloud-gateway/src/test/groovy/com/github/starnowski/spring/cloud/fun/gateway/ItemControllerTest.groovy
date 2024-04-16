package com.github.starnowski.spring.cloud.fun.gateway

import com.github.starnowski.spring.cloud.fun.gateway.services.ItemService
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.RequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

class ItemControllerTest extends Specification {

    MockMvc mockMvc

    ItemController itemController

    ItemService itemService

    def setup(){
        itemService = Mock(ItemService)
        itemController = new ItemController(itemService)
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
    }

    def "should return bad request when path variable size is outbound"(){
        when:
            def result = mockMvc.perform(MockMvcRequestBuilders.get("/item/{id}", "1234123412341234")).andDo(MockMvcResultHandlers.print())

        then:
            result.andExpect(MockMvcResultMatchers.status().isBadRequest())
    }
}
