package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface ShipService {
    Ship getShip(Long id);

    boolean deleteShip(Long id);

    Ship createShip(Ship ship);

    void editShip(Ship ship);

    List<Ship> getAllShips(String name, String planet, ShipType shipType, Long after, Long before,
                           Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                           Integer maxCrewSize, Double minRating, Double maxRating, Pageable pageable);

    int getShipsCount(String name, String planet, ShipType shipType, Long after, Long before,
                      Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                      Integer maxCrewSize, Double minRating, Double maxRating);




}
