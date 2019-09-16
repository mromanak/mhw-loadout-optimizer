package com.mromanak.loadoutoptimizer.service;

import com.google.common.collect.ImmutableSet;
import com.mromanak.loadoutoptimizer.model.dto.optimizer.ThinSetBonusSkill;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import com.mromanak.loadoutoptimizer.model.jpa.Rank;
import com.mromanak.loadoutoptimizer.model.jpa.SetBonusSkill;
import com.mromanak.loadoutoptimizer.repository.SetBonusRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Service
public class SetBonusService {

    private final SetBonusRepository setBonusRepository;

    public SetBonusService(SetBonusRepository setBonusRepository) {
        this.setBonusRepository = setBonusRepository;
    }

    public Set<ThinSetBonusSkill> getSetBonusSkillsAndArmorRank(String skillName, Rank rank) {

        return ImmutableSet.copyOf(setBonusRepository.eagerFindBySkillName(skillName).stream().
            map((SetBonusSkill sbsk) -> {
                sbsk.getSetBonus().getArmorPieces().
                    removeIf((ap) -> ap.getArmorType() != ArmorType.CHARM && ap.getSetType().getRank() != rank);
                return new ThinSetBonusSkill(sbsk);
            }).
            collect(toSet())
        );
    }
}
