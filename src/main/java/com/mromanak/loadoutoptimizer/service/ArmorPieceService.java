package com.mromanak.loadoutoptimizer.service;

import com.google.common.collect.ImmutableSet;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorPieceSkill;
import com.mromanak.loadoutoptimizer.model.jpa.Skill;
import com.mromanak.loadoutoptimizer.repository.ArmorPieceRepository;
import com.mromanak.loadoutoptimizer.repository.SkillRepository;
import com.mromanak.loadoutoptimizer.utils.NameUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toSet;

@Service
public class ArmorPieceService {

    private final ArmorPieceRepository armorPieceRepository;
    private final SkillRepository skillRepository;

    public ArmorPieceService(ArmorPieceRepository armorPieceRepository, SkillRepository skillRepository) {
        this.armorPieceRepository = armorPieceRepository;
        this.skillRepository = skillRepository;
    }

    public Set<ArmorPiece> getArmorPieces() {
        return ImmutableSet.copyOf(armorPieceRepository.findAll());
    }

    public Set<ArmorPiece> getArmorPiecesWithNames(Set<String> armorPieceNames) {
        return ImmutableSet.copyOf(armorPieceRepository.findAllByNameIn(armorPieceNames));
    }

    public Set<ArmorPiece> getArmorPieces(Predicate<ArmorPiece> filter) {
        return ImmutableSet.copyOf(StreamSupport.stream(armorPieceRepository.findAll().spliterator(), false).
            filter(filter).
            collect(toSet()));
    }

    public Set<ArmorPiece> getArmorPiecesWithSkillsNamed(Set<String> skillNames) {
        return streamArmorPiecesWithSkillsNamed(skillNames).
            collect(toSet());
    }

    public Set<ArmorPiece> getArmorPiecesWithSkillsNamed(Set<String> skillNames, Predicate<ArmorPiece> predicate) {
        return streamArmorPiecesWithSkillsNamed(skillNames).
            filter(predicate).
            collect(toSet());
    }

    private Stream<ArmorPiece> streamArmorPiecesWithSkillsNamed(Set<String> skillNames) {
        if (skillNames == null) {
            return Stream.of();
        }

        return skillNames.stream().
            map(NameUtils::toSlug).
            map(skillRepository::findById).
            filter(Optional::isPresent).
            map(Optional::get).
            map(Skill::getArmorPieces).
            filter(Objects::nonNull).
            flatMap(List::stream).
            map(ArmorPieceSkill::getArmorPiece);
    }
}
