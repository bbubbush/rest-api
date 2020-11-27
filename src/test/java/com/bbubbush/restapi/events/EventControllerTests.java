package com.bbubbush.restapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class EventControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    EventRepository eventRepository;


    @Test
    public void createEvent() throws Exception {
        // given
        Event event = Event.builder()
                .name("bbubbush")
                .description("Spring study")
                .location("신림역")
                .basePrice(0)
                .maxPrice(100)
                .beginEventDateTime(LocalDateTime.of(2020, 11, 29, 10, 00))
                .endEventDateTime(LocalDateTime.of(2020, 11, 30, 10, 00))
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 11, 29, 10, 00))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 11, 30, 10, 00))
                .limitOfEnrollment(100)
                .build();
        event.setId(10);

        // when
        Mockito.when(eventRepository.save(event)).thenReturn(event);

        // then
        mockMvc.perform(post("/api/events/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
        ;

    }
}