package com.mromanak.loadoutoptimizer;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.collect.Sets;
import com.mromanak.loadoutoptimizer.impl.LoadoutOptimizer;
import com.mromanak.loadoutoptimizer.model.*;
import com.mromanak.loadoutoptimizer.scoring.SimpleLoadoutScoringFunction;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.mromanak.loadoutoptimizer.scoring.LoadoutScoringUtils.preferSmallerLoadouts;
import static com.mromanak.loadoutoptimizer.scoring.LoadoutScoringUtils.simpleSkillWeightFunction;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class LoadoutOptimizerCLI {

    public static void main(String[] args) throws IOException {
        SimpleLoadoutScoringFunction scoringFunction = SimpleLoadoutScoringFunction.builder().
            withSkillWeightFunction("Earplugs", simpleSkillWeightFunction(1.0, 5)).
            withSkillWeightFunction("Windproof", simpleSkillWeightFunction(1.0, 3)).
            withSkillWeightFunction("Tremor Resistance", simpleSkillWeightFunction(1.0, 3)).
            withLevel3SlotWeight(0.5).
            withLevel2SlotWeight(0.5).
            withLevel1SlotWeight(0.25).
            withLoadoutSizeWeightFunction(preferSmallerLoadouts(1.0)).
            build();

        Set<String> desiredSkills = scoringFunction.getSkillWieghtFunctions().keySet();
        Pattern namesToIgnorePattern = Pattern.compile("^$");
        Predicate<ArmorPiece> filter = armorPiece -> !namesToIgnorePattern.matcher(armorPiece.getName()).matches();

        Set<ArmorPiece> armorPieces = loadArmorPieces(desiredSkills, filter);
        List<DisplayLoadout> loadouts = LoadoutOptimizer.findBestLoadouts(armorPieces, scoringFunction).stream().
            map(LoadoutOptimizerCLI::toDisplayLoadout).
            collect(toList());

        ObjectWriter objectWriter = new ObjectMapper().writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(System.out, loadouts);
    }

    private static InputStream getArmorPiecesStream() {
        return LoadoutOptimizerCLI.class.getResourceAsStream("/armorPieces.tsv");
    }

    private static Set<ArmorPiece> loadArmorPieces(Set<String> desiredSkills, Predicate<ArmorPiece> filter) throws IOException {
        Set<ArmorPiece> armorPieces = new HashSet<>();
        CsvSchema schema = CsvSchema.builder().setUseHeader(true).setColumnSeparator('\t').build();
        CsvMapper csvMapper = new CsvMapper();
        ObjectReader objectReader = csvMapper.reader().
            forType(CsvArmorPiece.class).
            with(schema);
        try(MappingIterator<CsvArmorPiece> iterator = objectReader.readValues(getArmorPiecesStream()))
        {
            iterator.forEachRemaining((CsvArmorPiece cap) -> {
                ArmorPiece ap = CsvArmorPiece.inflate(cap);
                if(Sets.intersection(desiredSkills, ap.getSkills().keySet()).isEmpty()) {
                    return;
                }

                if(filter.test(ap)) {
                    armorPieces.add(ap);
                }
            });
        }
        return armorPieces;
    }

    private static DisplayLoadout toDisplayLoadout(Loadout loadout) {
        DisplayLoadout displayLoadout = new DisplayLoadout();
        Map<ArmorType, String> armor = new TreeMap<>(loadout.getArmorPieces().
            entrySet().
            stream().
            collect(toMap(Map.Entry::getKey, e -> e.getValue().getName())));
        displayLoadout.setArmor(armor);
        displayLoadout.setSkills(loadout.getSkills());
        displayLoadout.setLevel1Slots(loadout.getLevel1Slots());
        displayLoadout.setLevel2Slots(loadout.getLevel2Slots());
        displayLoadout.setLevel3Slots(loadout.getLevel3Slots());
        return displayLoadout;
    }
}
