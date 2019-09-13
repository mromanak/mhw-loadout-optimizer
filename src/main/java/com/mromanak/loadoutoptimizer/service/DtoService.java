package com.mromanak.loadoutoptimizer.service;

import com.mromanak.loadoutoptimizer.model.dto.*;
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
        if (skill.getUncappingSkill() != null) {
            dto.setUncappingSkillId(skill.getUncappingSkill().getId());
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
        if (skill == null) {
            return null;
        }

        return skill.getEffects().entrySet().stream().
            map((entry) -> new SkillEffectDto(entry.getKey(), entry.getValue())).
            collect(toCollection(TreeSet::new));
    }

    private TreeSet<SkillProviderDto> getArmorPieceDtos(Skill skill) {
        if (skill == null) {
            return null;
        }

        return skill.getArmorPieces().stream().
            map((ArmorPieceSkill mapping) -> {
                return new SkillProviderDto(mapping.getPrimaryKey().getArmorPieceId(), mapping.getSkillLevel());
            }).
            collect(toCollection(TreeSet::new));
    }

    private TreeSet<SkillProviderDto> getJewelDtos(Skill skill) {
        if (skill == null) {
            return null;
        }

        return skill.getJewels().stream().
            map((JewelSkill mapping) -> {
                return new SkillProviderDto(mapping.getPrimaryKey().getJewelId(), mapping.getSkillLevel());
            }).
            collect(toCollection(TreeSet::new));
    }

    private TreeSet<SetBonusSkillProviderDto> getSetBonusDtos(Skill skill) {
        if (skill == null) {
            return null;
        }

        return skill.getSetBonuses().stream().
            map((SetBonusSkill mapping) -> {
                return new SetBonusSkillProviderDto(mapping.getPrimaryKey().getSetBonusId(),
                    mapping.getRequiredPieces(), mapping.getSkillLevel());
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
            if (dto.getUncappingSkillId() != null) {
                skill.setUncappingSkill(getUncappingSkill(dto.getUncappingSkillId()));
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

    private List<ArmorPieceSkill> getArmorPieceSkills(Skill skill, Collection<SkillProviderDto> armorPieces) {
        if (armorPieces == null) {
            return null;
        }

        return armorPieces.stream().
            map((SkillProviderDto spd) -> {
                ArmorPiece armorPiece = armorPieceRepository.findById(spd.getSourceId()).
                    orElseThrow(() -> new EntityNotFoundException("No armor piece found with ID " + spd.getSourceId()));
                return new ArmorPieceSkill(armorPiece, skill, spd.getLevel());
            }).
            collect(toList());
    }

    private List<JewelSkill> getJewelSkills(Skill skill, Collection<SkillProviderDto> jewels) {
        if (jewels == null) {
            return null;
        }

        return jewels.stream().
            map((SkillProviderDto spd) -> {
                Jewel jewel = jewelRepository.findById(spd.getSourceId()).
                    orElseThrow(() -> new EntityNotFoundException("No jewel found with ID " + spd.getSourceId()));
                return new JewelSkill(jewel, skill, spd.getLevel());
            }).
            collect(toList());
    }

    private List<SetBonusSkill> getSetBonusSkills(Skill skill, Collection<SetBonusSkillProviderDto> setBonuses) {
        if (setBonuses == null) {
            return null;
        }

        return setBonuses.stream().
            map((SetBonusSkillProviderDto sbspd) -> {
                SetBonus setBonus = setBonusRepository.findById(sbspd.getSourceId()).
                    orElseThrow(() -> new EntityNotFoundException("No set bonus found with ID " + sbspd.getSourceId()));
                return new SetBonusSkill(setBonus, skill, sbspd.getRequiredPieces(), sbspd.getLevel());
            }).
            collect(toList());
    }

    public JewelDto toDto(Jewel jewel) {
        if (jewel == null) {
            return null;
        }

        JewelDto dto = new JewelDto();
        dto.setName(jewel.getName());
        dto.setJewelLevel(jewel.getJewelLevel());
        dto.setSkills(this.getJewelSkillDtos(jewel.getSkills()));
        return dto;
    }

    private SortedSet<ProvidedSkillDto> getJewelSkillDtos(List<JewelSkill> jewelSkills) {
        if (jewelSkills == null) {
            return null;
        }

        return jewelSkills.stream().
            map((js) -> new ProvidedSkillDto(js.getPrimaryKey().getSkillId(), js.getSkillLevel())).
            collect(toCollection(TreeSet::new));
    }

    public Jewel fromDto(JewelDto dto, boolean preserveRelationships) {
        if (dto == null) {
            return null;
        }

        Jewel jewel;
        if (preserveRelationships) {
            jewel = jewelRepository.findById(dto.getId()).orElseGet(Jewel::new);
        } else {
            jewel = new Jewel();
        }
        jewel.setName(dto.getName());
        jewel.setJewelLevel(dto.getJewelLevel());

        if (!preserveRelationships) {
            jewel.setSkills(getJewelSkills(jewel, dto.getSkills()));
        }
        return jewel;
    }

    private List<JewelSkill> getJewelSkills(Jewel jewel, SortedSet<ProvidedSkillDto> skills) {
        if (skills == null) {
            return null;
        }

        return skills.stream().
            map((ProvidedSkillDto psd) -> {
                Skill skill = skillRepository.findById(psd.getSkillId()).
                    orElseThrow(() -> new EntityNotFoundException("No skill found with ID " + psd.getSkillId()));
                return new JewelSkill(jewel, skill, psd.getLevel());
            }).
            collect(toList());
    }

    public ArmorPieceDto toDto(ArmorPiece armorPiece) {
        if (armorPiece == null) {
            return null;
        }

        ArmorPieceDto dto = new ArmorPieceDto();
        dto.setName(armorPiece.getName());
        dto.setSetName(armorPiece.getSetName());
        dto.setArmorType(armorPiece.getArmorType());
        dto.setSetType(armorPiece.getSetType());
        dto.setLevel1Slots(armorPiece.getLevel1Slots());
        dto.setLevel2Slots(armorPiece.getLevel2Slots());
        dto.setLevel3Slots(armorPiece.getLevel3Slots());
        dto.setLevel4Slots(armorPiece.getLevel4Slots());
        dto.setSkills(getArmorPieceSkillDtos(armorPiece.getSkills()));
        if (armorPiece.getSetBonus() != null) {
            dto.setSetBonusId(armorPiece.getSetBonus().getId());
        }
        return dto;
    }

    private SortedSet<ProvidedSkillDto> getArmorPieceSkillDtos(List<ArmorPieceSkill> armorPieceSkills) {
        if (armorPieceSkills == null) {
            return null;
        }

        return armorPieceSkills.stream().
            map((aps) -> new ProvidedSkillDto(aps.getPrimaryKey().getSkillId(), aps.getSkillLevel())).
            collect(toCollection(TreeSet::new));
    }

    public ArmorPiece fromDto(ArmorPieceDto dto, boolean preserveRelationships) {
        if (dto == null) {
            return null;
        }

        ArmorPiece armorPiece;
        if (preserveRelationships) {
            armorPiece = armorPieceRepository.findById(dto.getId()).orElseGet(ArmorPiece::new);
        } else {
            armorPiece = new ArmorPiece();
        }
        armorPiece.setName(dto.getName());
        armorPiece.setSetName(dto.getSetName());
        armorPiece.setArmorType(dto.getArmorType());
        armorPiece.setSetType(dto.getSetType());
        armorPiece.setLevel1Slots(dto.getLevel1Slots());
        armorPiece.setLevel2Slots(dto.getLevel2Slots());
        armorPiece.setLevel3Slots(dto.getLevel3Slots());
        armorPiece.setLevel4Slots(dto.getLevel4Slots());

        if (!preserveRelationships) {
            armorPiece.setSkills(getArmorPieceSkills(armorPiece, dto.getSkills()));

            if (dto.getSetBonusId() != null) {
                armorPiece.setSetBonus(getArmorPieceSetBonus(dto.getSetBonusId()));
            }

        }
        return armorPiece;
    }

    private List<ArmorPieceSkill> getArmorPieceSkills(ArmorPiece armorPiece, SortedSet<ProvidedSkillDto> skills) {
        if (skills == null) {
            return null;
        }

        return skills.stream().
            map((ProvidedSkillDto psd) -> {
                Skill skill = skillRepository.findById(psd.getSkillId()).
                    orElseThrow(() -> new EntityNotFoundException("No skill found with ID " + psd.getSkillId()));
                return new ArmorPieceSkill(armorPiece, skill, psd.getLevel());
            }).
            collect(toList());
    }

    private SetBonus getArmorPieceSetBonus(String setBonusId) {
        return setBonusRepository.findById(setBonusId).
            orElseThrow(() -> new EntityNotFoundException("No set bonus found with ID " + setBonusId));
    }

    public SetBonusDto toDto(SetBonus setBonus) {
        if (setBonus == null) {
            return null;
        }

        SetBonusDto dto = new SetBonusDto();
        dto.setName(setBonus.getName());
        dto.setSkills(getSetBonusSkillDtos(setBonus.getSkills()));
        dto.setArmorPieces(getSetBonusArmorPieceDtos(setBonus.getArmorPieces()));
        return dto;
    }

    private SortedSet<SetBonusSkillDto> getSetBonusSkillDtos(List<SetBonusSkill> setBonusSkills) {
        if (setBonusSkills == null) {
            return null;
        }

        return setBonusSkills.stream().
            map((sbs) -> {
                return new SetBonusSkillDto(sbs.getPrimaryKey().getSkillId(), sbs.getRequiredPieces(),
                    sbs.getSkillLevel());
            }).
            collect(toCollection(TreeSet::new));
    }

    private SortedSet<String> getSetBonusArmorPieceDtos(List<ArmorPiece> armorPieces) {
        if (armorPieces == null) {
            return null;
        }

        return armorPieces.stream().
            map(ArmorPiece::getId).
            collect(toCollection(TreeSet::new));
    }

    public SetBonus fromDto(SetBonusDto dto) {
        if (dto == null) {
            return null;
        }

        SetBonus setBonus = setBonusRepository.findById(dto.getId()).orElseGet(SetBonus::new);
        setBonus.setName(dto.getName());
        setBonus.setSkills(getSetBonusSkills(setBonus, dto.getSkills()));
        setBonus.setArmorPieces(getSetBonusArmorPieces(dto.getArmorPieces()));
        return setBonus;
    }

    private List<SetBonusSkill> getSetBonusSkills(SetBonus setBonus, SortedSet<SetBonusSkillDto> skills) {
        if (skills == null) {
            return null;
        }

        return skills.stream().
            map((SetBonusSkillDto sbsd) -> {
                Skill skill = skillRepository.findById(sbsd.getSkillId()).
                    orElseThrow(() -> new EntityNotFoundException("No skill found with ID " + sbsd.getSkillId()));
                return new SetBonusSkill(setBonus, skill, sbsd.getRequiredPieces(), sbsd.getLevel());
            }).
            collect(toList());
    }

    private List<ArmorPiece> getSetBonusArmorPieces(SortedSet<String> armorPieces) {
        if (armorPieces == null) {
            return null;
        }

        return armorPieces.stream().
            map((String armorPieceId) -> {
                ArmorPiece armorPiece = armorPieceRepository.findById(armorPieceId).
                    orElseThrow(() -> new EntityNotFoundException("No armor piece found with ID " + armorPieceId));
                return armorPiece;
            }).
            collect(toList());
    }
}
