package com.mromanak.loadoutoptimizer.repository;

import com.mromanak.loadoutoptimizer.model.jpa.ArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import com.mromanak.loadoutoptimizer.model.jpa.SetType;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.Set;

public interface ArmorPieceRepository extends PagingAndSortingRepository<ArmorPiece, String> {

    Optional<ArmorPiece> findBySetNameAndArmorTypeAndSetType(String setName, ArmorType armorType, SetType setType);

    Set<ArmorPiece> findAllByNameIn(Set<String> names);

    Set<ArmorPiece> findBySetName(String setName);

    Set<ArmorPiece> findBySetNameAndSetType(String setName, SetType setType);
}
