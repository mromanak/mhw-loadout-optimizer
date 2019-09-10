package com.mromanak.loadoutoptimizer.repository;

import com.mromanak.loadoutoptimizer.model.jpa.Jewel;
import org.springframework.data.repository.CrudRepository;

public interface JewelRepository extends CrudRepository<Jewel, Long> {

    Jewel findByName(String name);
}
