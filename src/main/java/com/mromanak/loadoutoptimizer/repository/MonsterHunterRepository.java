package com.mromanak.loadoutoptimizer.repository;

import com.mromanak.loadoutoptimizer.model.exception.EntityNotFoundException;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ProvidedSkill;
import com.mromanak.loadoutoptimizer.model.jpa.Skill;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
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

    public void saveArmorPiece(ArmorPiece armorPiece) {
        armorPieceRepository.save(armorPiece);
    }

    public Optional<ArmorPiece> findArmorPieceByName(String armorPieceName) {
        return Optional.ofNullable(armorPieceRepository.findByName(armorPieceName));
    }

    public void saveSkill(Skill skill) {
        skillRepository.save(skill);
    }

    public Optional<Skill> findSkillByName(String skillName) {
        return Optional.ofNullable(skillRepository.findByName(skillName));
    }

    public void attachSkillToArmorPiece(String skillName, String armorPieceName, Integer skillLevel) {
        Skill skill = findSkillByName(skillName).
            orElseThrow(() -> notFoundException("No skill found with name " + skillName));
        ArmorPiece armorPiece = findArmorPieceByName(armorPieceName).
            orElseThrow(() -> notFoundException("No armor piece found with name " + armorPieceName));

        if(armorPiece.getProvidedSkills() == null) {
            armorPiece.setProvidedSkills(new HashSet<>());
        }
        ProvidedSkill providedSkill = new ProvidedSkill(skill, skillLevel);
        armorPiece.getProvidedSkills().add(providedSkill);
        saveArmorPiece(armorPiece);
    }

    public void detachSkillFromArmorPiece(String skillName, String armorPieceName) {
        ArmorPiece armorPiece = findArmorPieceByName(armorPieceName).
            orElseThrow(() -> notFoundException("No armor piece found with name " + armorPieceName));

        if(armorPiece.getProvidedSkills() == null) {
            armorPiece.setProvidedSkills(new HashSet<>());
        }
        armorPiece.getProvidedSkills().removeIf((ps) -> Objects.equals(ps.getSkill().getName(), skillName));
        saveArmorPiece(armorPiece);
    }

    private EntityNotFoundException notFoundException(String message) {
        return new EntityNotFoundException(message);
    }
}
