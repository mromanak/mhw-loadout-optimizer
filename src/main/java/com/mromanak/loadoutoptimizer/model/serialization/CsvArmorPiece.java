package com.mromanak.loadoutoptimizer.model.serialization;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.mromanak.loadoutoptimizer.model.ArmorPiece;
import com.mromanak.loadoutoptimizer.model.ArmorType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
@JsonPropertyOrder({"name", "armorType", "skills", "level1Slots", "level2Slots", "level3Slots"})
public class CsvArmorPiece {

    private static final Joiner.MapJoiner SKILLS_JOINER = Joiner.on(",").withKeyValueSeparator(":");
    private static final Splitter.MapSplitter SKILLS_SPLITTER = Splitter.on(",").withKeyValueSeparator(":");

    private String name;
    private ArmorType armorType;
    private String skills;
    private int level1Slots;
    private int level2Slots;
    private int level3Slots;

    public static CsvArmorPiece deflate(ArmorPiece armorPiece) {
        if(armorPiece == null) {
            return null;
        }

        CsvArmorPiece csvArmorPiece = new CsvArmorPiece();
        csvArmorPiece.setName(armorPiece.getName());
        csvArmorPiece.setArmorType(armorPiece.getArmorType());
        csvArmorPiece.setSkills(SKILLS_JOINER.join(armorPiece.getSkills()));
        csvArmorPiece.setLevel1Slots(armorPiece.getLevel1Slots());
        csvArmorPiece.setLevel2Slots(armorPiece.getLevel2Slots());
        csvArmorPiece.setLevel3Slots(armorPiece.getLevel3Slots());
        return csvArmorPiece;
    }

    public static ArmorPiece inflate(CsvArmorPiece csvArmorPiece) {
        if(csvArmorPiece == null) {
            return null;
        }

        Map<String, Integer> skills;
        if(isNotBlank(csvArmorPiece.getSkills())) {
            skills = SKILLS_SPLITTER.split(csvArmorPiece.getSkills()).
                entrySet().
                stream().
                collect(
                    toMap(Map.Entry::getKey, e -> Integer.valueOf(e.getValue()), (s1, s2) -> s1 + s2, TreeMap::new));
        } else {
            skills = new HashMap<>();
        }

        return ArmorPiece.builder().
            withName(csvArmorPiece.name).
            withArmorType(csvArmorPiece.armorType).
            withSkills(skills).
            withLevel1Slots(csvArmorPiece.level1Slots).
            withLevel2Slots(csvArmorPiece.level2Slots).
            withLevel3Slots(csvArmorPiece.level3Slots).
            build();
    }
}
