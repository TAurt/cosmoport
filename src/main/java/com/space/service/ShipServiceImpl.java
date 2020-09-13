package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ShipServiceImpl implements ShipService {

    private final ShipRepository shipRepository;

    public ShipServiceImpl(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public Ship createShip(Ship ship) {
        shipRepository.save(ship);
        return ship;
    }

    @Override
    public Ship getShip(Long id) {
        Optional<Ship> ship = shipRepository.findById(id);
        return ship.orElse(null);
    }

    @Override
    public boolean deleteShip(Long id) {
        if(shipRepository.existsById(id)) {
            shipRepository.deleteById(id);
            return true;
        } else {
            return false;
        }

    }

    @Override
    public List<Ship> getAllShips(String name, String planet, ShipType shipType, Long after, Long before,
                                  Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                                  Integer maxCrewSize, Double minRating, Double maxRating, Pageable pageable) {
        List<Ship> ships =  shipRepository.findAll((Specification<Ship>) (root, cq, cb) -> {
            Predicate p = cb.conjunction();
            if(!StringUtils.isEmpty(name)) {
                p = cb.and(p, cb.like(root.get("name"), "%" + name + "%"));
            }
            if(!StringUtils.isEmpty(planet)) {
                p = cb.and(p, cb.like(root.get("planet"), "%" + planet + "%"));
            }
            if(Objects.nonNull(shipType)) {
                p = cb.and(p, cb.equal(root.get("shipType"), shipType));
            }
            if(Objects.nonNull(isUsed)) {
                p = cb.and(p, cb.equal(root.get("isUsed"), isUsed));
            }
            if(Objects.nonNull(after) && Objects.nonNull(before)) {
                p = cb.and(p, cb.between(root.get("prodDate"), new Date(after), new Date(before)));
            }
            if(Objects.nonNull(minSpeed) && Objects.nonNull(maxSpeed)) {
                p = cb.and(p, cb.between(root.get("speed"), minSpeed, maxSpeed));
            }
            if(Objects.nonNull(minCrewSize) && Objects.nonNull(maxCrewSize)) {
                p = cb.and(p, cb.between(root.get("crewSize"), minCrewSize, maxCrewSize));
            }
            if(Objects.nonNull(minRating) && Objects.nonNull(maxRating)) {
                p = cb.and(p, cb.between(root.get("rating"), minRating, maxRating));
            }
            //cq.orderBy(cb.asc(root.get("id")));
            return p;
        },pageable).getContent();
        return ships;
    }

    @Override
    public int getShipsCount(String name, String planet, ShipType shipType, Long after, Long before,
                             Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                             Integer maxCrewSize, Double minRating, Double maxRating) {
        List<Ship> ships =  shipRepository.findAll((Specification<Ship>) (root, cq, cb) -> {
            Predicate p = cb.conjunction();
            if(!StringUtils.isEmpty(name)) {
                p = cb.and(p, cb.like(root.get("name"), "%" + name + "%"));
            }
            if(!StringUtils.isEmpty(planet)) {
                p = cb.and(p, cb.like(root.get("planet"), "%" + planet + "%"));
            }
            if(Objects.nonNull(shipType)) {
                p = cb.and(p, cb.equal(root.get("shipType"), shipType));
            }
            if(Objects.nonNull(isUsed)) {
                p = cb.and(p, cb.equal(root.get("isUsed"), isUsed));
            }
            if(Objects.nonNull(after) && Objects.nonNull(before)) {
                p = cb.and(p, cb.between(root.get("prodDate"), new Date(after), new Date(before)));
            }
            if(Objects.nonNull(minSpeed) && Objects.nonNull(maxSpeed)) {
                p = cb.and(p, cb.between(root.get("speed"), minSpeed, maxSpeed));
            }
            if(Objects.nonNull(minCrewSize) && Objects.nonNull(maxCrewSize)) {
                p = cb.and(p, cb.between(root.get("crewSize"), minCrewSize, maxCrewSize));
            }
            if(Objects.nonNull(minRating) && Objects.nonNull(maxRating)) {
                p = cb.and(p, cb.between(root.get("rating"), minRating, maxRating));
            }
            cq.orderBy(cb.asc(root.get("id")));
            return p;
        });
        return ships.size();
    }

    @Override
    public void editShip(Ship ship) {
        shipRepository.save(ship);
    }

}



