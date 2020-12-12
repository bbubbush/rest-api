package com.bbubbush.restapi.events;

import com.bbubbush.restapi.accounts.Account;
import com.bbubbush.restapi.accounts.AccountAdapter;
import com.bbubbush.restapi.common.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class EventController {
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidate eventValidate;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto
                                        , Errors errors
                                        , @AuthenticationPrincipal AccountAdapter accountAdapter
                                        , @CurrentUser Account currentUser) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        eventValidate.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        event.setManager(currentUser);

        Event newEvent = eventRepository.save(event);

        URI createUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
        EntityModel<Event> eventEntity = EntityModel.of(newEvent)
                .add(linkTo(EventController.class).withSelfRel())
                .add(linkTo(EventController.class).withRel("events"))
                .add(linkTo(EventController.class).withRel("update"));
        return ResponseEntity.created(createUri).body(eventEntity);
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable
                                        , PagedResourcesAssembler<Event> assembler
                                        , @CurrentUser Account account) {
        Page<Event> page = eventRepository.findAll(pageable);
        PagedModel<EntityModel<Event>> entityModels = assembler.toModel(page, entity -> EntityModel.of(entity)
                .add(linkTo(EventController.class).slash(entity.getId()).withRel("self")));
        entityModels.add(new Link("http://localhost:8080//docs/index.html#create-event").withRel("profile"));
        if (account != null) {
            entityModels.add(linkTo(EventController.class).withRel("create-event"));
        }
        return ResponseEntity.ok(entityModels);
    }

    @GetMapping("/{id}")
    public ResponseEntity queryEvent(@PathVariable Integer id, @CurrentUser Account account){
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Event event = optionalEvent.get();
        EntityModel<Event> eventEntity = EntityModel.of(event)
                .add(linkTo(EventController.class).slash(event.getId()).withRel("self"))
                .add(new Link("http://localhost:8080/docs/index.html#get-event").withRel("profile"))
                ;
        if (event.getManager().equals(account)) {
            eventEntity.add(linkTo(EventController.class).slash(event.getId()).withRel("update"));
        }
        return ResponseEntity.ok(eventEntity);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id
                                        , @RequestBody @Valid EventDto eventDto, Errors errors
                                        , @CurrentUser Account account) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        eventValidate.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Event findEvent = optionalEvent.get();
        if (!findEvent.getManager().equals(account)) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        this.modelMapper.map(eventDto, findEvent);
        Event saveEvent = eventRepository.save(findEvent);

        EntityModel<Event> entityModel = EntityModel.of(saveEvent)
                .add(linkTo(EventController.class).slash(saveEvent.getId()).withRel("self"))
                .add(new Link("http://localhost:8080/docs/index.html#update-event").withRel("profile"));
        return ResponseEntity.ok(entityModel);
    }


    private ResponseEntity<Errors> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(errors);

    }
}
