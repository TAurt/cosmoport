package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController()
@RequestMapping(value = "/rest")
@Controller
public class ShipController {
    private final ShipService shipService;
    private final static int MAX_NAME_LENGTH = 50;
    private final static int MAX_PLANET_NAME_LENGTH = 50;
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static Long maxDate;
    private static Long minDate;
    private final static int MAX_CREW_SIZE = 9999;
    private final static int MIN_CREW_SIZE = 1;
    private final static double MIN_SHIP_SPEED = 0.01;
    private final static double MAX_SHIP_SPEED = 0.99;
    static {
        try {
            maxDate = simpleDateFormat.parse("3019-01-01").getTime();
            minDate = simpleDateFormat.parse("2800-01-01").getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping(value = "/ships")
    public ResponseEntity<?> getAllShips(@RequestParam(required = false) String name,
                                         @RequestParam(required = false) String planet,
                                         @RequestParam(required = false) ShipType shipType,
                                         @RequestParam(required = false) Long after,
                                         @RequestParam(required = false) Long before,
                                         @RequestParam(required = false) Boolean isUsed,
                                         @RequestParam(required = false) Double minSpeed,
                                         @RequestParam(required = false) Double maxSpeed,
                                         @RequestParam(required = false) Integer minCrewSize,
                                         @RequestParam(required = false) Integer maxCrewSize,
                                         @RequestParam(required = false) Double minRating,
                                         @RequestParam(required = false) Double maxRating,
                                         @RequestParam(required = false) Integer pageNumber,
                                         @RequestParam(required = false) Integer pageSize,
                                         @RequestParam(required = false) ShipOrder order) {

        if(pageNumber == null) pageNumber = 0;
        if(pageSize == null) pageSize = 3;
        if(order == null) order = ShipOrder.ID;
        if(after == null) after = minDate;
        if(before == null) before = maxDate;
        if(minCrewSize == null) minCrewSize = 1;
        if(maxCrewSize == null) maxCrewSize = 9999;
        if(minRating == null) minRating = 0.0;
        if(maxRating == null) maxRating = 100000.0;
        if(minSpeed == null) minSpeed = 0.01;
        if(maxSpeed == null) maxSpeed = 0.99;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        List<Ship> shipsList = shipService.getAllShips(name, planet, shipType, after, before,
                                                       isUsed, minSpeed, maxSpeed, minCrewSize,
                                                       maxCrewSize, minRating, maxRating, pageable);
        return new ResponseEntity<>(shipsList, HttpStatus.OK);
    }

    @GetMapping(value = "/ships/{id}")
    public ResponseEntity<?> getShip(@PathVariable @Validated Long id) {
        if (id == 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Ship ship = shipService.getShip(id);
        return ship != null ? new ResponseEntity<>(ship, HttpStatus.OK) :
                              new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/ships/{id}")
    public ResponseEntity<?> deleteShip(@PathVariable @Validated Long id) {
        if (id == 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return shipService.deleteShip(id) ? new ResponseEntity<>(HttpStatus.OK) :
                                            new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/ships")
    public ResponseEntity<?> createShip(@RequestBody Ship ship) {
        if (isShipValidated(ship)) {
            return new ResponseEntity<>(shipService.createShip(ship),HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/ships/{id}")
    public ResponseEntity<?> editShip(@PathVariable @Validated Long id, @RequestBody Ship updShip) {
        if(id == 0 || updShip == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Ship ship = shipService.getShip(id);
        if (ship == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        ship = updateShip(ship, updShip);
        if (isShipValidated(ship)) {
            ship.setId(id);
            shipService.editShip(ship);
            return new ResponseEntity<>(ship, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }



    @GetMapping(value = "/ships/count")
    public ResponseEntity<?> getShipCount(@RequestParam(required = false) String name,
                                         @RequestParam(required = false) String planet,
                                         @RequestParam(required = false) ShipType shipType,
                                         @RequestParam(required = false) Long after,
                                         @RequestParam(required = false) Long before,
                                         @RequestParam(required = false) Boolean isUsed,
                                         @RequestParam(required = false) Double minSpeed,
                                         @RequestParam(required = false) Double maxSpeed,
                                         @RequestParam(required = false) Integer minCrewSize,
                                         @RequestParam(required = false) Integer maxCrewSize,
                                         @RequestParam(required = false) Double minRating,
                                         @RequestParam(required = false) Double maxRating) {
        if(after == null) after = minDate;
        if(before == null) before = maxDate;
        if(minCrewSize == null) minCrewSize = 1;
        if(maxCrewSize == null) maxCrewSize = 9999;
        if(minRating == null) minRating = 0.0;
        if(maxRating == null) maxRating = 100000.0;
        if(minSpeed == null) minSpeed = 0.01;
        if(maxSpeed == null) maxSpeed = 0.99;
        int count = shipService.getShipsCount(name, planet, shipType, after, before,
                isUsed, minSpeed, maxSpeed, minCrewSize,
                maxCrewSize, minRating, maxRating);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    private boolean isShipValidated(Ship ship) {
        if(ship == null) return false;
        String name = ship.getName();
        String planet = ship.getPlanet();
        Integer crewSize = ship.getCrewSize();
        if(ship.getProdDate() == null) return false;
        Long dateProd = ship.getProdDate().getTime();
        Double shipSpeed = ship.getSpeed();
        ShipType shipType = ship.getShipType();

        if(StringUtils.isEmpty(name) || name.length() > MAX_NAME_LENGTH) return false;
        if(StringUtils.isEmpty(planet) || planet.length() > MAX_PLANET_NAME_LENGTH) return false;
        if(crewSize == null || crewSize > MAX_CREW_SIZE || crewSize < MIN_CREW_SIZE) return false;
        if(dateProd > maxDate || dateProd < minDate) return false;
        if(shipSpeed == null || shipSpeed > MAX_SHIP_SPEED || shipSpeed < MIN_SHIP_SPEED) return false;
        if(shipType == null) return false;
        if(ship.getUsed() == null) ship.setUsed(false);
        double kof = ship.getUsed() ? 0.5 : 1;
        int maxYear = Integer.parseInt(simpleDateFormat.format(maxDate).substring(0, 4));
        int prodYear = Integer.parseInt(simpleDateFormat.format(dateProd).substring(0, 4));
        double rating = (80 * shipSpeed * kof) / (maxYear - prodYear + 1);
        rating = Math.round(rating * 100) / 100.0;
        ship.setRating(rating);
        return true;
    }

    private Ship updateShip(Ship ship,  Ship updShip) {
        if(updShip.getName() != null) ship.setName(updShip.getName());
        if(updShip.getPlanet() != null) ship.setPlanet(updShip.getPlanet());
        if(updShip.getProdDate() != null) ship.setProdDate(updShip.getProdDate());
        if(updShip.getCrewSize() != null) ship.setCrewSize(updShip.getCrewSize());
        if(updShip.getUsed() != null) ship.setUsed(updShip.getUsed());
        if(updShip.getShipType() != null) ship.setShipType(updShip.getShipType());
        if(updShip.getSpeed() != null) ship.setSpeed(updShip.getSpeed());
        return ship;
    }

}
