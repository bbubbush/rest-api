package com.bbubbush.restapi.events;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


public class EventTest {

    @Test
    public void builder() {
        Event event = Event.builder().build();

        assertNotNull(event);
    }

    @Test
    public void javaBean() {
        // given
        String name = "bbubbush";
        String description = "server developer";

        // when
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        // then
        assertEquals(name, event.getName());
        assertEquals(description, event.getDescription());
    }

    private static Stream<Arguments> parametersForTestFree() {
        return Stream.of(
            Arguments.of(0, 0, true)
            , Arguments.of(100, 0, false)
            , Arguments.of(0, 100, false)
            , Arguments.of(100, 100, false)
        );
    }
    @ParameterizedTest
    @MethodSource("parametersForTestFree")
    public void testFree(int basePrice, int maxPrice, boolean expectedIsFree) {
        // given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();

        // when
        event.update();

        // then
        assertEquals(expectedIsFree, event.isFree());
    }

    private static Stream<Arguments> parametersForTestOffline() {
        return Stream.of(
                Arguments.of("신림역", true)
                , Arguments.of(null, false)
                , Arguments.of("       ", false)
        );
    }
    @ParameterizedTest
    @MethodSource("parametersForTestOffline")
    public void testOffline(String location, boolean expectedIsLocation) {
        // given
        Event event = Event.builder()
                .location(location)
                .build();

        // when¡
        event.update();

        // then
        assertEquals(expectedIsLocation, event.isOffline());
    }


}