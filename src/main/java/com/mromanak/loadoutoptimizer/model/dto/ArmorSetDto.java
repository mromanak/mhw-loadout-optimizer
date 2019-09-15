package com.mromanak.loadoutoptimizer.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mromanak.loadoutoptimizer.model.jpa.SetType;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder(
    {"setName", "setType", "setBonusId", "head", "body", "arms", "waist", "legs", "charm", "level1Slots", "level2Slots",
        "level3Slots", "level4Slots", "skills"})
public class ArmorSetDto {

    @NotBlank(message = "Set name must be non-null")
    private String setName;

    @NotNull(message = "Set type must be non-null")
    private SetType setType;

    private String setBonusId;

    @Valid
    private ArmorSetComponentDto head;

    @Valid
    private ArmorSetComponentDto body;

    @Valid
    private ArmorSetComponentDto arms;

    @Valid
    private ArmorSetComponentDto waist;

    @Valid
    private ArmorSetComponentDto legs;

    @Valid
    private ArmorSetComponentDto charm;

    @JsonIgnore
    public List<ArmorSetComponentDto> getArmorSetComponents() {
        return Stream.of(head, body, arms, legs, waist).
            filter(Objects::nonNull).
            collect(toList());
    }

    public int getLevel1Slots() {
        return getArmorSetComponents().stream().
            filter(Objects::nonNull).
            mapToInt(ArmorSetComponentDto::getLevel1Slots).
            sum();
    }

    public int getLevel2Slots() {
        return getArmorSetComponents().stream().
            filter(Objects::nonNull).
            mapToInt(ArmorSetComponentDto::getLevel2Slots).
            sum();
    }

    public int getLevel3Slots() {
        return getArmorSetComponents().stream().
            mapToInt(ArmorSetComponentDto::getLevel3Slots).
            filter(Objects::nonNull).
            sum();
    }

    public int getLevel4Slots() {
        return getArmorSetComponents().stream().
            mapToInt(ArmorSetComponentDto::getLevel4Slots).
            filter(Objects::nonNull).
            sum();
    }

    public SortedSet<ProvidedSkillDto> getSkills() {
        Collection<ProvidedSkillDto> set = getArmorSetComponents().stream().
            filter(Objects::nonNull).
            map(ArmorSetComponentDto::getSkills).
            filter(Objects::nonNull).
            flatMap(Collection::stream).
            collect(toMap(ProvidedSkillDto::getSkillId, Function.identity(),
                (psd1, psd2) -> new ProvidedSkillDto(psd1.getSkillId(), psd1.getLevel() + psd2.getLevel())))
            .values();
        return new TreeSet<>(set);
    }
}
