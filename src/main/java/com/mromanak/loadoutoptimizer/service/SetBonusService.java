package com.mromanak.loadoutoptimizer.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.collect.ImmutableSet;
import com.mromanak.loadoutoptimizer.model.SetBonus;
import com.mromanak.loadoutoptimizer.model.serialization.CsvSetBonus;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Service
public class SetBonusService {

    private final Set<SetBonus> setBonuses;

    public SetBonusService() throws IOException {
        setBonuses = loadSetBonuses();
    }

    private Set<SetBonus> loadSetBonuses() throws IOException {
        Set<SetBonus> setBonuses = new HashSet<>();
        CsvSchema schema = CsvSchema.builder().setUseHeader(true).setColumnSeparator('\t').build();
        CsvMapper csvMapper = new CsvMapper();
        ObjectReader objectReader = csvMapper.reader().
            forType(CsvSetBonus.class).
            with(schema);

        Resource armorPiecesResource = new ClassPathResource("/setBonuses.tsv");
        try (MappingIterator<CsvSetBonus> iterator = objectReader.readValues(armorPiecesResource.getInputStream())) {

            iterator.forEachRemaining((CsvSetBonus csvSetBonus) -> {
                SetBonus setBonus = CsvSetBonus.inflate(csvSetBonus);
                setBonuses.add(setBonus);
            });
        }
        return ImmutableSet.copyOf(setBonuses);
    }

    public Set<SetBonus> getSetBonuses() {
        return setBonuses;
    }

    public List<SetBonus> findSetBonus(String skillName) {
        return setBonuses.stream().
            filter(sb -> sb.getBonusRequirements().keySet().contains(skillName)).
            collect(toList());
    }
}
