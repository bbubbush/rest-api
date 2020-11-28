package com.bbubbush.restapi.events;

import com.bbubbush.restapi.common.annotation.TestDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @TestDescription("201 등록성공 응답을 전달")
    public void createEvent() throws Exception {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        // given
        EventDto eventDto = EventDto.builder()
                .name("bbubbush")
                .description("Spring study")
                .beginEventDateTime(LocalDateTime.of(2020, 11, 29, 10, 00))
                .endEventDateTime(LocalDateTime.of(2020, 11, 30, 10, 00))
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 11, 29, 10, 00))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 11, 30, 10, 00))
                .location("신림역")
                .basePrice(100)
                .maxPrice(100)
                .limitOfEnrollment(100)
                .build();

        // then
        mockMvc.perform(post("/api/events/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("id").value(Matchers.not(10)))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
        ;
    }

    @Test
    @TestDescription("정해진 데이터 외 다른 데이터를 전달해 400오류를 발생")
    public void createEvent_BadRequest() throws Exception {
        // given
        Event event = Event.builder()
                .id(10)
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
                .free(true)
                .offline(false)
                .build();

        // then
        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("빈 데이터를 전달하여 401 발생")
    public void createEvent_BadRequest_Nodata() throws Exception {
        // given
        EventDto event = EventDto.builder()
                .build();

        // then
        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("로직에 위반되는 데이터를 전달해 401 발생")
    public void createEvent_BadRequest_InvalidData() throws Exception {
        // given
        EventDto event = EventDto.builder()
                .name("bbubbush")
                .description("Spring study")
                .beginEventDateTime(LocalDateTime.of(2020, 11, 30, 10, 00))
                .endEventDateTime(LocalDateTime.of(2020, 11, 29, 10, 00))
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 11, 30, 10, 00))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 11, 29, 10, 00))
                .location("신림역")
                .basePrice(10000)
                .maxPrice(100)
                .limitOfEnrollment(100)
                .build();

        // then
        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
        ;
    }
}
