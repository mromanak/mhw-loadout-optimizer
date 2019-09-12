package com.mromanak.loadoutoptimizer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Comparator;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BonusSkillProviderDto implements Comparable<BonusSkillProviderDto> {

    private static final Comparator<BonusSkillProviderDto> COMPARATOR = Comparator.comparingInt(BonusSkillProviderDto::getRequiredPieces).
        thenComparing(BonusSkillProviderDto::getSource);

    @NotBlank(message = "Source must be non-blank")
    private String source;

    @NotNull(message = "Required pieces must be non-null")
    private Integer requiredPieces;

    @Override
    public int compareTo(BonusSkillProviderDto that) {
        return COMPARATOR.compare(this, that);
    }
}
