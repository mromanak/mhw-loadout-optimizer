package com.mromanak.loadoutoptimizer.model.dto;

import com.mromanak.loadoutoptimizer.model.jpa.Skill;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillEffectDto implements Comparable<SkillEffectDto> {

    private static final Comparator<SkillEffectDto> COMPARATOR = Comparator.comparingInt(SkillEffectDto::getLevel);

    private Integer level;
    private String description;

    @Override
    public int compareTo(SkillEffectDto that) {
        return COMPARATOR.compare(this, that);
    }
}
