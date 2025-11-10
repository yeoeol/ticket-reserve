package ticket.reserve.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticket.reserve.admin.client.event.EventServiceClient;
import ticket.reserve.admin.client.event.dto.EventDetailResponseDto;
import ticket.reserve.admin.client.event.dto.EventRequestDto;
import ticket.reserve.admin.client.event.dto.EventResponseDto;
import ticket.reserve.admin.client.event.dto.EventUpdateRequestDto;
import ticket.reserve.admin.client.user.UserServiceClient;
import ticket.reserve.admin.client.user.dto.UserResponseDto;
import ticket.reserve.admin.client.user.dto.UserUpdateRequestDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserServiceClient userServiceClient;
    private final EventServiceClient eventServiceClient;

    public List<UserResponseDto> getUsers() {
        return userServiceClient.getUsers();
    }

    public UserResponseDto getUser(Long userId) {
        return userServiceClient.getUser(userId);
    }

    public void updateUser(UserUpdateRequestDto request) {
        userServiceClient.updateUser(request);
    }


    public List<EventResponseDto> getEvents() {
        return eventServiceClient.getEvents();
    }

    public EventDetailResponseDto getEvent(Long eventId) {
        return eventServiceClient.getEvent(eventId);
    }

    public EventDetailResponseDto createEvent(EventRequestDto request) {
        return eventServiceClient.createEvent(request);
    }

    public void updateEvent(Long eventId, EventUpdateRequestDto request) {
        eventServiceClient.updateEvent(eventId, request);
    }

    public void deleteEvent(Long eventId) {
        eventServiceClient.deleteEvent(eventId);
    }
}
