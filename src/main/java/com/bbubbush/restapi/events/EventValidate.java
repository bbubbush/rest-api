package com.bbubbush.restapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class EventValidate {

    public void validate(EventDto eventDto, Errors errors) {
        if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() > 0) {
            errors.rejectValue("basePrice", "wrongValue", "BasePrice is wrong.");
            errors.rejectValue("maxPrice", "wrongValue", "MaxPrice is wrong.");
        }

        if (eventDto.getEndEventDateTime().isBefore(eventDto.getBeginEventDateTime())
        || eventDto.getEndEventDateTime().isBefore(eventDto.getCloseEnrollmentDateTime())
        || eventDto.getEndEventDateTime().isBefore(eventDto.getBeginEnrollmentDateTime())) {
            errors.rejectValue("endEventDateTime", "wrongValue", "EndEventDateTime is wrong.");
        }
        // TODO beginEventDateTime

        // TODO bebinEnrollmentDateTime
    }
}
