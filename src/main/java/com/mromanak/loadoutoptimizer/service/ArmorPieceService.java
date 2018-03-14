package com.mromanak.loadoutoptimizer.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.collect.ImmutableSet;
import com.mromanak.loadoutoptimizer.model.ArmorPiece;
import com.mromanak.loadoutoptimizer.model.CsvArmorPiece;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toSet;

@Service
public class ArmorPieceService {

    private final Set<ArmorPiece> armorPieces;

    public ArmorPieceService() throws IOException {
        armorPieces = loadArmorPieces();
    }

    private Set<ArmorPiece> loadArmorPieces() throws IOException {
        Set<ArmorPiece> armorPieces = new HashSet<>();
        CsvSchema schema = CsvSchema.builder().setUseHeader(true).setColumnSeparator('\t').build();
        CsvMapper csvMapper = new CsvMapper();
        ObjectReader objectReader = csvMapper.reader().
            forType(CsvArmorPiece.class).
            with(schema);

        Resource armorPiecesResource = new ClassPathResource("/armorPieces.tsv");
        try (MappingIterator<CsvArmorPiece> iterator = objectReader.readValues(armorPiecesResource.getInputStream())) {

            iterator.forEachRemaining((CsvArmorPiece cap) -> {
                ArmorPiece ap = CsvArmorPiece.inflate(cap);
                armorPieces.add(ap);
            });
        }
        return ImmutableSet.copyOf(armorPieces);
    }

    public Set<ArmorPiece> getArmorPieces() {
        return armorPieces;
    }

    public Set<ArmorPiece> getArmorPieces(Predicate<ArmorPiece> filter) {
        return ImmutableSet.copyOf(armorPieces.stream().
            filter(filter).
            collect(toSet()));
    }
}
