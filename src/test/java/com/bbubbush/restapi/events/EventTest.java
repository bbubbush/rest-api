package com.bbubbush.restapi.events;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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


}