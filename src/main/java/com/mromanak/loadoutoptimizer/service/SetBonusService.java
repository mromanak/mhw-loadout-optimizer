package com.mromanak.loadoutoptimizer.service;

import com.mromanak.loadoutoptimizer.model.jpa.SetBonusSkill;
import com.mromanak.loadoutoptimizer.model.jpa.Skill;
import com.mromanak.loadoutoptimizer.repository.SkillRepository;
import com.mromanak.loadoutoptimizer.utils.NameUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

@Service
public class SetBonusService {

    private final SkillRepository skillRepository;

    public SetBonusService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Set<SetBonusSkill> getSetBonusSkillsWithSkillsNamed(String skillName) {
        return streamArmorPiecesWithSkillNamed(skillName).
            collect(toSet());
    }

    private Stream<SetBonusSkill> streamArmorPiecesWithSkillNamed(String skillName) {
        if (skillName == null) {
            return Stream.of();
        }

        return Stream.of(skillName).
            map(NameUtils::toSlug).
            map(skillRepository::findById).
            filter(Optional::isPresent).
            map(Optional::get).
            map(Skill::getSetBonuses).
            filter(Objects::nonNull).
            flatMap(List::stream);
    }
}
