package com.example.portfolio.service.event;

import com.example.portfolio.domain.Event;
import java.util.List;

public interface EventService {
    List<Event> getAllEvents();
    List<Event> getEventsByUser(String userId);
    Event createEvent(String userId, Event event);
    void deleteEvent(String userId, Long eventId);
}
