package com.bbubbush.restapi.events;

import com.bbubbush.restapi.index.IndexController;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class EventController {
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidate eventValidate;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        eventValidate.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();

        Event newEvent = eventRepository.save(event);

        URI createUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
        EntityModel<Event> eventEntity = EntityModel.of(newEvent)
                .add(linkTo(EventController.class).withSelfRel())
                .add(linkTo(EventController.class).withRel("events"))
                .add(linkTo(EventController.class).withRel("update"));
        return ResponseEntity.created(createUri).body(eventEntity);
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
        Page<Event> page = eventRepository.findAll(pageable);
        PagedModel<EntityModel<Event>> entityModels = assembler.toModel(page, entity -> EntityModel.of(entity).add(linkTo(EventController.class).slash(entity.getId()).withRel("self")));
        entityModels.add(new Link("docs/index.html#create-event").withRel("profile"));
        return ResponseEntity.ok(entityModels);
    }


    private ResponseEntity<Errors> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(errors);

    }
}
