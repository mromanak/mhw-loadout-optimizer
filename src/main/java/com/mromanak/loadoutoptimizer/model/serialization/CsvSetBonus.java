package com.mromanak.loadoutoptimizer.model.serialization;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.mromanak.loadoutoptimizer.model.SetBonus;
import lombok.Data;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static java.util.stream.Collectors.toMap;

@Data
@JsonPropertyOrder({"name", "bonusRequirements", "armorPieces"})
public class CsvSetBonus {
    private static final Joiner SET_JOINER = Joiner.on(",");
    private static final Splitter SET_SPLITTER = Splitter.on(",");
    private static final Joiner.MapJoiner MAP_JOINER = SET_JOINER.withKeyValueSeparator(":");
    private static final Splitter.MapSplitter MAP_SPLITTER = SET_SPLITTER.withKeyValueSeparator(":");

    private String name;
    private String bonusRequirements;
    private String armorPieces;

    public static CsvSetBonus deflate(SetBonus setBonus) {
        if(setBonus == null) {
            return null;
        }

        CsvSetBonus csvSetBonus = new CsvSetBonus();
        csvSetBonus.setName(setBonus.getName());
        csvSetBonus.setBonusRequirements(MAP_JOINER.join(setBonus.getBonusRequirements()));
        csvSetBonus.setArmorPieces(SET_JOINER.join(setBonus.getArmorPieces()));
        return csvSetBonus;
    }

    public static SetBonus inflate(CsvSetBonus csvSetBonus) {
        if(csvSetBonus == null) {
            return null;
        }

        Map<String, Integer> bonusRequirements = MAP_SPLITTER.split(csvSetBonus.getBonusRequirements()).
            entrySet().
            stream().
            collect(toMap(Map.Entry::getKey, e -> Integer.valueOf(e.getValue()), (s1, s2) -> s1 + s2, TreeMap::new));

        Set<String> armorPieces = ImmutableSet.copyOf(SET_SPLITTER.split(csvSetBonus.armorPieces));

        return SetBonus.builder().
            withName(csvSetBonus.name).
            withBonusRequirements(bonusRequirements).
            withArmorPieces(armorPieces).
            build();
    }
}
