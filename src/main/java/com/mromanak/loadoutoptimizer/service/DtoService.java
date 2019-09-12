package com.mromanak.loadoutoptimizer.service;

import com.mromanak.loadoutoptimizer.model.dto.BonusSkillProviderDto;
import com.mromanak.loadoutoptimizer.model.dto.SkillDto;
import com.mromanak.loadoutoptimizer.model.dto.SkillEffectDto;
import com.mromanak.loadoutoptimizer.model.dto.SkillLevelProviderDto;
import com.mromanak.loadoutoptimizer.model.exception.EntityNotFoundException;
import com.mromanak.loadoutoptimizer.model.jpa.*;
import com.mromanak.loadoutoptimizer.repository.ArmorPieceRepository;
import com.mromanak.loadoutoptimizer.repository.JewelRepository;
import com.mromanak.loadoutoptimizer.repository.SetBonusRepository;
import com.mromanak.loadoutoptimizer.repository.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class DtoService {

    private final ArmorPieceRepository armorPieceRepository;
    private final JewelRepository jewelRepository;
    private final SetBonusRepository setBonusRepository;
    private final SkillRepository skillRepository;

    public DtoService(ArmorPieceRepository armorPieceRepository, JewelRepository jewelRepository,
        SetBonusRepository setBonusRepository, SkillRepository skillRepository) {
        this.armorPieceRepository = armorPieceRepository;
        this.jewelRepository = jewelRepository;
        this.setBonusRepository = setBonusRepository;
        this.skillRepository = skillRepository;
    }

    public SkillDto toDto(Skill skill) {
        if (skill == null) {
            return null;
        }

        SkillDto dto = new SkillDto();
        dto.setName(skill.getName());
        dto.setMaxLevel(skill.getMaxLevel());
        dto.setMaxUncappedLevel(skill.getMaxUncappedLevel());
        if (skill.getUncappedBy() != null) {
            dto.setUncappedBy(skill.getUncappedBy().getId());
        }
        dto.setDescription(skill.getDescription());
        dto.setEffects(getEffectDtos(skill));
        if (skill.getArmorPieces() != null) {
            dto.setArmorPieces(getArmorPieceDtos(skill));
        }
        if (skill.getJewels() != null) {
            dto.setJewels(getJewelDtos(skill));
        }
        if (skill.getSetBonuses() != null) {
            dto.setSetBonuses(getSetBonusDtos(skill));
        }
        return dto;
    }

    private TreeSet<SkillEffectDto> getEffectDtos(Skill skill) {
        return skill.getEffects().entrySet().stream().
            map((entry) -> new SkillEffectDto(entry.getKey(), entry.getValue())).
            collect(toCollection(TreeSet::new));
    }

    private TreeSet<SkillLevelProviderDto> getArmorPieceDtos(Skill skill) {
        return skill.getArmorPieces().stream().
            map((ArmorPieceSkill mapping) -> {
                return new SkillLevelProviderDto(mapping.getPrimaryKey().getArmorPieceId(), mapping.getSkillLevel());
            }).
            collect(toCollection(TreeSet::new));
    }

    private TreeSet<SkillLevelProviderDto> getJewelDtos(Skill skill) {
        return skill.getJewels().stream().
            map((JewelSkill mapping) -> {
                return new SkillLevelProviderDto(mapping.getPrimaryKey().getJewelId(), mapping.getSkillLevel());
            }).
            collect(toCollection(TreeSet::new));
    }

    private TreeSet<BonusSkillProviderDto> getSetBonusDtos(Skill skill) {
        return skill.getSetBonuses().stream().
            map((SetBonusSkill mapping) -> {
                return new BonusSkillProviderDto(mapping.getPrimaryKey().getSetBonusId(), mapping.getRequiredPieces());
            }).
            collect(toCollection(TreeSet::new));
    }

    public Skill fromDto(SkillDto dto, boolean preserveRelationships) {
        if (dto == null) {
            return null;
        }

        Skill skill;
        if (preserveRelationships) {
            skill = skillRepository.findById(dto.getId()).orElseGet(Skill::new);
        } else {
            skill = new Skill();
        }
        skill.setName(dto.getName());
        skill.setMaxLevel(dto.getMaxLevel());
        skill.setMaxUncappedLevel(dto.getMaxUncappedLevel());
        skill.setDescription(dto.getDescription());
        skill.setEffects(getEffects(dto));

        if (!preserveRelationships) {
            if (dto.getUncappedBy() != null) {
                skill.setUncappedBy(getUncappingSkill(dto.getUncappedBy()));
            }
            if (dto.getArmorPieces() != null) {
                skill.setArmorPieces(getArmorPieceSkills(skill, dto.getArmorPieces()));
            }
            if (dto.getJewels() != null) {
                skill.setJewels(getJewelSkills(skill, dto.getJewels()));
            }
            if (dto.getSetBonuses() != null) {
                skill.setSetBonuses(getSetBonusSkills(skill, dto.getSetBonuses()));
            }
        }
        return skill;
    }

    private Map<Integer, String> getEffects(SkillDto dto) {
        return dto.getEffects().stream().
            collect(toMap(SkillEffectDto::getLevel, SkillEffectDto::getDescription));
    }

    private Skill getUncappingSkill(String uncappedBy) {
        return skillRepository.findById(uncappedBy).
            orElseThrow(() -> new EntityNotFoundException("No skill found with ID " + uncappedBy));
    }

    private List<ArmorPieceSkill> getArmorPieceSkills(Skill skill, Collection<SkillLevelProviderDto> armorPieces) {
        return armorPieces.stream().
            map((SkillLevelProviderDto slpd) -> {
                ArmorPiece armorPiece = armorPieceRepository.findById(slpd.getSource()).
                    orElseThrow(() -> new EntityNotFoundException("No armor piece found with ID " + slpd.getSource()));
                return new ArmorPieceSkill(armorPiece, skill, slpd.getLevel());
            }).
            collect(toList());
    }

    private List<JewelSkill> getJewelSkills(Skill skill, Collection<SkillLevelProviderDto> jewels) {
        return jewels.stream().
            map((SkillLevelProviderDto slpd) -> {
                Jewel jewel = jewelRepository.findById(slpd.getSource()).
                    orElseThrow(() -> new EntityNotFoundException("No jewel found with ID " + slpd.getSource()));
                return new JewelSkill(jewel, skill, slpd.getLevel());
            }).
            collect(toList());
    }

    private List<SetBonusSkill> getSetBonusSkills(Skill skill, Collection<BonusSkillProviderDto> setBonuses) {
        return setBonuses.stream().
            map((BonusSkillProviderDto bspd) -> {
                SetBonus setBonus = setBonusRepository.findById(bspd.getSource()).
                    orElseThrow(() -> new EntityNotFoundException("No set bonus found with ID " + bspd.getSource()));
                return new SetBonusSkill(setBonus, skill, bspd.getRequiredPieces());
            }).
            collect(toList());
    }
}
