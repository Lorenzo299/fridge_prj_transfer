package com.gdserv.fridge.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gdserv.fridge.models.Fridge;

public interface FridgeRepository extends JpaRepository<Fridge, Long> {

    List<Fridge> findByName(String name);

}
