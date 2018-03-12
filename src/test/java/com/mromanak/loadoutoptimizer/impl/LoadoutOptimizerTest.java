package com.mromanak.loadoutoptimizer.impl;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.mromanak.loadoutoptimizer.model.ArmorPiece;
import com.mromanak.loadoutoptimizer.model.CsvArmorPiece;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.*;

public class LoadoutOptimizerTest {

    @Test
    public void dedupeFile() throws Exception {
        Path inputFile = Paths.get("/Users/mromanak13/Downloads/mhwArmor.tsv");
        Path outputFile = Paths.get("/Users/mromanak13/Downloads/mhwArmor.tsv.tmp");

        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.typedSchemaFor(CsvArmorPiece.class).withHeader().withColumnSeparator('\t');
        ObjectReader objectReader = csvMapper.reader().
            forType(CsvArmorPiece.class).
            with(schema);

        Set<ArmorPiece> armorPieces = new TreeSet<>(Comparator.comparing(ArmorPiece::getArmorType).thenComparing(ArmorPiece::getName));
        try(MappingIterator<CsvArmorPiece> iterator = objectReader.readValues(inputFile.toFile())) {
            iterator.forEachRemaining((CsvArmorPiece cap) -> {
                ArmorPiece ap = CsvArmorPiece.inflate(cap);
                armorPieces.add(ap);
            });
        }

        try (CsvGenerator generator = new CsvFactory().createGenerator(outputFile.toFile(), JsonEncoding.UTF8)) {
            generator.setSchema(schema);
            for(ArmorPiece armorPiece : armorPieces) {
                csvMapper.writeValue(generator, CsvArmorPiece.deflate(armorPiece));
            }
        }

        Files.move(outputFile, inputFile, StandardCopyOption.REPLACE_EXISTING);
    }
}