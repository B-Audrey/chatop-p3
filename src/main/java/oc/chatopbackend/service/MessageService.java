package oc.chatopbackend.service;

import oc.chatopbackend.entity.MessageEntity;
import oc.chatopbackend.entity.RentalEntity;
import oc.chatopbackend.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final RentalService rentalService;

    public MessageService(MessageRepository messageRepository, RentalService rentalService) {
        this.messageRepository = messageRepository;
        this.rentalService = rentalService;
    }

    public void saveMessage(MessageEntity message) {
        messageRepository.save(message);
    }

    public Optional<RentalEntity> getRentalById(Long rentalId) {
        return Optional.ofNullable(rentalService.getRentalById(rentalId));
    }
}
