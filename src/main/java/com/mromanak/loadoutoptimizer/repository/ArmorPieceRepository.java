package com.mromanak.loadoutoptimizer.repository;

import com.mromanak.loadoutoptimizer.model.jpa.ArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import com.mromanak.loadoutoptimizer.model.jpa.SetType;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ArmorPieceRepository extends PagingAndSortingRepository<ArmorPiece, String> {

    ArmorPiece findBySetNameAndArmorTypeAndSetType(String setName, ArmorType armorType, SetType setType);
}
