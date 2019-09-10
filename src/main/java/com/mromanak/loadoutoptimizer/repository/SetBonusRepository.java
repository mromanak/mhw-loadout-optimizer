package com.mromanak.loadoutoptimizer.repository;

import com.mromanak.loadoutoptimizer.model.jpa.SetBonus;
import org.springframework.data.repository.CrudRepository;

public interface SetBonusRepository extends CrudRepository<SetBonus, Long> {

    SetBonus findByName(String name);
}
