package com.mromanak.loadoutoptimizer.repository;

import com.google.common.collect.ImmutableList;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MonsterHunterRepository {

    private final ArmorPieceRepository armorPieceRepository;
    private final JewelRepository jewelRepository;
    private final SetBonusRepository setBonusRepository;
    private final SkillRepository skillRepository;

    public MonsterHunterRepository(ArmorPieceRepository armorPieceRepository, JewelRepository jewelRepository,
        SetBonusRepository setBonusRepository, SkillRepository skillRepository)
    {
        this.armorPieceRepository = armorPieceRepository;
        this.jewelRepository = jewelRepository;
        this.setBonusRepository = setBonusRepository;
        this.skillRepository = skillRepository;
    }

    public void saveSkill(Skill skill) {
        skillRepository.save(skill);
    }

    public Optional<Skill> findSkill(String skillId) {
        return skillRepository.findById(skillId);
    }

    public List<Skill> findAllSkills() {
        return ImmutableList.copyOf(skillRepository.findAll());
    }

    public Page<Skill> findAllSkills(Pageable pageable) {
        return skillRepository.findAll(pageable);
    }

    public void deleteSkill(String skillId) {
        skillRepository.deleteById(skillId);
    }
}
