package com.mromanak.loadoutoptimizer.controller;

import com.mromanak.loadoutoptimizer.model.ArmorType;
import com.mromanak.loadoutoptimizer.model.api.LoadoutRequest;
import com.mromanak.loadoutoptimizer.model.api.LoadoutResponse;
import com.mromanak.loadoutoptimizer.model.Loadout;
import com.mromanak.loadoutoptimizer.service.LoadoutOptimizerService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@RestController
@RequestMapping("/loadout")
@Api(value="loadout", description = "Operations for obtaining optimized loadouts")
public class LoadoutController {

    private final LoadoutOptimizerService loadoutOptimizerService;

    @Autowired
    public LoadoutController(LoadoutOptimizerService loadoutOptimizerService) {
        this.loadoutOptimizerService = loadoutOptimizerService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<LoadoutResponse>> getLoadout() {
        LoadoutRequest request = loadoutOptimizerService.getSampleRequest();
        List<LoadoutResponse> loadouts = loadoutOptimizerService.optimize(request).
            stream().
            map(this::toDisplayLoadout).
            collect(toList());
        return ResponseEntity.ok(loadouts);
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<List<LoadoutResponse>> getLoadout(@RequestBody LoadoutRequest request) {
        List<LoadoutResponse> loadouts = loadoutOptimizerService.optimize(request).
            stream().
            map(this::toDisplayLoadout).
            collect(toList());
        return ResponseEntity.ok(loadouts);
    }

    private LoadoutResponse toDisplayLoadout(Loadout loadout) {
        LoadoutResponse loadoutResponse = new LoadoutResponse();
        Map<ArmorType, String> armor = new TreeMap<>(loadout.getArmorPieces().
            entrySet().
            stream().
            collect(toMap(Map.Entry::getKey, e -> e.getValue().getName())));
        loadoutResponse.setArmor(armor);
        loadoutResponse.setSkills(loadout.getSkills());
        loadoutResponse.setLevel1Slots(loadout.getLevel1Slots());
        loadoutResponse.setLevel2Slots(loadout.getLevel2Slots());
        loadoutResponse.setLevel3Slots(loadout.getLevel3Slots());
        return loadoutResponse;
    }
}
