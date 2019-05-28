package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

@Controller
@RequestMapping("/rest")
public class ShipController {

    private final ShipService shipService;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping("/ships")
    @ResponseBody
    public ResponseEntity<?> getAll(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating,
            @RequestParam(value = "order", defaultValue = "ID") ShipOrder order ,
            @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize
    ){
        Page<Ship> mainTable = shipService.getAll(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, order, pageNumber, pageSize);
        return new ResponseEntity<>(mainTable.getContent(), HttpStatus.OK);
    }


    @GetMapping("/ships/count")
    @ResponseBody
    public ResponseEntity<?> getCount(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating){

        Integer count = shipService.getCount(name, planet, shipType, after, before, isUsed,
                    minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }


    @PostMapping("/ships")
    @ResponseBody
    public ResponseEntity<?> createShip(@RequestBody Map<String, String> body){
        if(body == null || body.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

         String name = body.get("name");
         if(name == null || name.isEmpty() || name.length() > 50) {
             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
         }

         String planet = body.get("planet");
         if(planet == null || planet.isEmpty() || planet.length() > 50) {
             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
         }

         String shipTypeText = body.get("shipType");
         if(shipTypeText == null || shipTypeText.isEmpty()) {
             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
         }
         ShipType shipType = ShipType.valueOf(shipTypeText);

         String prodDateLongText = body.get("prodDate");
         if(prodDateLongText == null || prodDateLongText.isEmpty()) {
             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
         }
         Long prodDateLong = Long.parseLong(prodDateLongText);
         if(prodDateLong < 0 ||
                 prodDateLong < new GregorianCalendar(2800, 0, 1).getTimeInMillis() ||
                 prodDateLong >= new GregorianCalendar(3020, 0, 1).getTimeInMillis()) {
             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
         }

         String isUsedText = body.get("isUsed");
         Boolean isUsed = false;
         if(isUsedText != null && !isUsedText.isEmpty()) {
             isUsed = Boolean.parseBoolean(isUsedText);
         }

         String speedText = body.get("speed");
         if(speedText == null || speedText.isEmpty()) {
             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
         }
         Double speed = Double.parseDouble(speedText);
         speed = Math.round(speed * 100)/100.0;
         if(speed < 0.01d || speed > 0.99d) {
             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
         }

         String crewSizeText = body.get("crewSize");
         if(crewSizeText == null || crewSizeText.isEmpty()) {
             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
         }
         Integer crewSize = Integer.parseInt(crewSizeText);
         if(crewSize < 1 || crewSize > 9999) {
             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
         }

        Ship ship = shipService.createShip(name, planet, shipType, prodDateLong, isUsed, speed, crewSize);

        return new ResponseEntity<>(ship, HttpStatus.OK);
    }


    @GetMapping("/ships/{id}")
    @ResponseBody
    public ResponseEntity<?> getShip(@PathVariable Long id){
        if(id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Ship ship = shipService.getShip(id);
        if(ship == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(ship, HttpStatus.OK);
        }
    }


    @PostMapping("/ships/{id}")
    @ResponseBody
    public ResponseEntity<?> updateShip(@PathVariable Long id, @RequestBody Map<String, String> body){
        if(id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Ship ship = shipService.getShip(id);
        if(ship == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(body != null && !body.isEmpty()) {

            String name = body.get("name");
            if (name != null) {
                if (name.isEmpty() || name.length() > 50) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                } else {
                    ship.setName(name);
                }
            }

            String planet = body.get("planet");
            if (planet != null) {
                if (planet.isEmpty() || planet.length() > 50) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                } else {
                    ship.setPlanet(planet);
                }
            }

            String shipTypeText = body.get("shipType");
            if (shipTypeText != null) {
                if (shipTypeText.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                } else {
                    ShipType shipType = ShipType.valueOf(shipTypeText);
                    ship.setShipType(shipType);
                }
            }

            String prodDateLongText = body.get("prodDate");
            if (prodDateLongText != null) {
                if (prodDateLongText.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                } else {
                    Long prodDateLong = Long.parseLong(prodDateLongText);
                    if (prodDateLong < 0 ||
                            prodDateLong < new GregorianCalendar(2800, 0, 1).getTimeInMillis() ||
                            prodDateLong >= new GregorianCalendar(3020, 0, 1).getTimeInMillis()) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    } else {
                        ship.setProdDate(new Date(prodDateLong));
                    }
                }
            }

            String isUsedText = body.get("isUsed");
            if (isUsedText != null && !isUsedText.isEmpty()) {
                Boolean isUsed = Boolean.parseBoolean(isUsedText);
                ship.setUsed(isUsed);
            }

            String speedText = body.get("speed");
            if (speedText != null) {
                if (speedText.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                } else {
                    Double speed = Double.parseDouble(speedText);
                    speed = Math.round(speed * 100)/100.0;
                    if (speed < 0.01d || speed > 0.99d) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    } else {
                        ship.setSpeed(speed);
                    }
                }
            }

            String crewSizeText = body.get("crewSize");
            if (crewSizeText != null) {
                if (crewSizeText.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                } else {
                    Integer crewSize = Integer.parseInt(crewSizeText);
                    if (crewSize < 1 || crewSize > 9999) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    } else {
                        ship.setCrewSize(crewSize);
                    }
                }
            }
            ship = shipService.updateShip(ship);
        }
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }


    @DeleteMapping("/ships/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteShip(@PathVariable Long id) {
        if (id <= 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Ship ship = shipService.getShip(id);
        if (ship == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            shipService.deleteShip(ship);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
