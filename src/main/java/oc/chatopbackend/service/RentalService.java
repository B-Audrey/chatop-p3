package oc.chatopbackend.service;

import oc.chatopbackend.entity.RentalEntity;
import oc.chatopbackend.repository.RentalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;

    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public List<RentalEntity> getAllRentals() {
        return rentalRepository.findAll();
    }

    public RentalEntity saveRental(RentalEntity rentalEntity) {
        return rentalRepository.save(rentalEntity);
    }

    public RentalEntity getRentalById(Long id) {
        return rentalRepository.findById(id).orElse(null);
    }
}

