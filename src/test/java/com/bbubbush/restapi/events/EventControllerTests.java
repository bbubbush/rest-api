package com.bbubbush.restapi.events;

import com.bbubbush.restapi.common.RestdocsConfiguration;
import com.bbubbush.restapi.common.annotation.TestDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestdocsConfiguration.class)
public class EventControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EventRepository eventRepository;

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
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("id").value(Matchers.not(10)))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update").exists())
                .andExpect(jsonPath("_links.events").exists())
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("self")
                                , linkWithRel("events").description("query events")
                                , linkWithRel("update").description("update event")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept")
                                , headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType")
                        ),
                        requestFields(
                                fieldWithPath("name").description("event name")
                                , fieldWithPath("description").description("event description")
                                , fieldWithPath("beginEventDateTime").description("event beginEventDateTime")
                                , fieldWithPath("endEventDateTime").description("event endEventDateTime")
                                , fieldWithPath("beginEnrollmentDateTime").description("event beginEnrollmentDateTime")
                                , fieldWithPath("closeEnrollmentDateTime").description("event closeEnrollmentDateTime")
                                , fieldWithPath("location").description("event location")
                                , fieldWithPath("basePrice").description("event basePrice")
                                , fieldWithPath("maxPrice").description("event maxPrice")
                                , fieldWithPath("limitOfEnrollment").description("event limitOfEnrollment")
                        ),
                        responseFields(
                                fieldWithPath("id").description("event id")
                                , fieldWithPath("name").description("event name")
                                , fieldWithPath("description").description("event description")
                                , fieldWithPath("beginEventDateTime").description("event beginEventDateTime")
                                , fieldWithPath("endEventDateTime").description("event endEventDateTime")
                                , fieldWithPath("beginEnrollmentDateTime").description("event beginEnrollmentDateTime")
                                , fieldWithPath("closeEnrollmentDateTime").description("event closeEnrollmentDateTime")
                                , fieldWithPath("location").description("event location")
                                , fieldWithPath("basePrice").description("event basePrice")
                                , fieldWithPath("maxPrice").description("event maxPrice")
                                , fieldWithPath("limitOfEnrollment").description("event limitOfEnrollment")
                                , fieldWithPath("free").description("event free")
                                , fieldWithPath("offline").description("event offline")
                                , fieldWithPath("eventStatus").description("event eventStatus")
                                , fieldWithPath("_links.self.href").description("event links.self")
                                , fieldWithPath("_links.update.href").description("event links.update")
                                , fieldWithPath("_links.events.href").description("event links.events")
                        )
                ))
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
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("빈 데이터를 전달하여 400 발생")
    public void createEvent_BadRequest_Nodata() throws Exception {
        // given
        EventDto event = EventDto.builder()
                .build();

        // then
        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].code").exists())
                .andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
//                .andExpect(jsonPath("_links.index.href").exists())
        ;
    }

    @Test
    @TestDescription("로직에 위반되는 데이터를 전달해 400 발생")
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
                .andDo(print())
                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("_links.index.href").exists())
        ;
    }

    @Test
    @TestDescription("이벤트 목록 조회 중 10개씩 페이징하여 2번째 페이지 조회")
    public void getEvents() throws Exception{
        // given
        IntStream.range(0, 30).forEach(i -> generateEvents(i));

        // when
        this.mockMvc.perform(get("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .param("page", "1")
                    .param("size", "10")
                    .param("sort", "name,DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self.href").exists())
                .andDo(print())
                .andDo(document("query-events"))
        ;

        // then
    }

    @Test
    @TestDescription("이벤트 단일 조회")
    public void getEvent() throws Exception{
        // given
        Event event = generateEvents(100);

        // when
        this.mockMvc.perform(get("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(print())
                .andDo(document("get-event"))
        ;
    }

    @Test
    @TestDescription("이벤트 단일 조회 실패")
    public void getEvent_NotFound() throws Exception{
        // given

        // when
        this.mockMvc.perform(get("/api/events/{id}", "100")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(print())
                .andDo(document("get-event"))
        ;
    }



    private Event generateEvents(int i) {
        Event event = Event.builder()
                .name("Evnet " + i)
                .description("For paging " + i)
                .build();
        return eventRepository.save(event);
    }
}
