package com.mromanak.loadoutoptimizer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillLevelProviderDto implements Comparable<SkillLevelProviderDto> {

    private static final Comparator<SkillLevelProviderDto> COMPARATOR = Comparator.comparingInt(SkillLevelProviderDto::getLevel).
        thenComparing(SkillLevelProviderDto::getSource);

    private String source;
    private Integer level;

    @Override
    public int compareTo(SkillLevelProviderDto that) {
        return COMPARATOR.compare(this, that);
    }
}
