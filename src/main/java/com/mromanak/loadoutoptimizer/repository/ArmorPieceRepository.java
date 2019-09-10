package com.mromanak.loadoutoptimizer.repository;

import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.SetType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArmorPieceRepository extends CrudRepository<ArmorPiece, Long> {

    ArmorPiece findByName(String name);

    ArmorPiece findBySetNameAndArmorTypeAndSetType(String setName, ArmorType armorType, SetType setType);
}
