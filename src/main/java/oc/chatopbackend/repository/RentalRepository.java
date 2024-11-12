package oc.chatopbackend.repository;

import oc.chatopbackend.entity.RentalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<RentalEntity, Long> {
}

