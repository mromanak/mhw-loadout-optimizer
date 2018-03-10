package com.mromanak.loadoutoptimizer.impl;

import com.google.common.collect.ImmutableList;
import com.mromanak.loadoutoptimizer.model.ArmorPiece;
import com.mromanak.loadoutoptimizer.model.ArmorType;
import com.mromanak.loadoutoptimizer.model.Loadout;
import com.mromanak.loadoutoptimizer.scoring.LoadoutScoringFunction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoadoutOptimizer {

    private final Map<ArmorType, List<ArmorPiece>> armorPieces;
    private final LoadoutScoringFunction scoringFunction;
    private final ConcurrentMap<OptimizerRequest, OptimizerResponse> solutionCache = new ConcurrentHashMap<>();
    private final Map<ArmorType, AtomicLong> cacheHits = new TreeMap<>();
    private final Map<ArmorType, AtomicLong> cacheMisses = new TreeMap<>();

    public static List<Loadout> findBestLoadouts(Collection<ArmorPiece> armorPieces,
        LoadoutScoringFunction scoringFunction)
    {
        if(armorPieces == null || armorPieces.isEmpty()) {
            return ImmutableList.of();
        }

        Map<ArmorType, List<ArmorPiece>> armorPiecesMap = armorPieces.stream().
            collect(toMap(
                ArmorPiece::getArmorType,
                ImmutableList::of,
                (l1, l2) -> ImmutableList.<ArmorPiece>builder().addAll(l1).addAll(l2).build()
            ));
        LoadoutOptimizer optimizer = new LoadoutOptimizer(armorPiecesMap, scoringFunction);
        OptimizerResponse response = optimizer.findBestLoadouts();
        return response.getArmorPiecesToAdd().
            stream().
            map(l -> Loadout.builder().withArmorPieces(l).build()).
            collect(toList());
    }

    private OptimizerResponse findBestLoadouts() {
        return bestLoadoutsGiven(Loadout.builder().build(), nextArmorType(null));
    }

    private OptimizerResponse bestLoadoutsGiven(Loadout currentLoadout, ArmorType armorType) {
        OptimizerRequest request = OptimizerRequest.forLoadout(currentLoadout, armorType);
        if(solutionCache.containsKey(request)) {
            cacheHits.computeIfAbsent(armorType, at -> new AtomicLong()).incrementAndGet();
            return solutionCache.get(request);
        } else if(hasNextArmorType(armorType)) {
            cacheMisses.computeIfAbsent(armorType, at -> new AtomicLong()).incrementAndGet();
            List<ArmorPiece> currentArmorPieces = armorPieces.getOrDefault(armorType, emptyList());
            if(currentArmorPieces.isEmpty()) {
                OptimizerResponse response = bestLoadoutsGiven(currentLoadout, nextArmorType(armorType));
                solutionCache.put(request, response);
                return response;
            }

            OptimizerResponse responseWithType = currentArmorPieces.stream().
                map(armorPiece -> optimizeNonTerminal(currentLoadout, armorType, armorPiece)).
                reduce(OptimizerResponse.empty(), OptimizerResponse.merger());
            OptimizerResponse responseWithoutType = bestLoadoutsGiven(currentLoadout, nextArmorType(armorType));
            OptimizerResponse response = OptimizerResponse.merger().apply(responseWithType, responseWithoutType);
            solutionCache.put(request, response);
            return response;
        } else {
            cacheMisses.computeIfAbsent(armorType, at -> new AtomicLong()).incrementAndGet();
            OptimizerResponse responseWithType = armorPieces.getOrDefault(armorType, emptyList()).
                stream().
                map(armorPiece-> optimizeTerminal(currentLoadout, armorPiece)).
                reduce(OptimizerResponse.empty(), OptimizerResponse.merger());
            OptimizerResponse responseWithoutType = OptimizerResponse.of(emptyList(), scoringFunction.apply(currentLoadout));
            OptimizerResponse response = OptimizerResponse.merger().apply(responseWithType, responseWithoutType);
            solutionCache.put(request, response);
            return response;
        }
    }

    private OptimizerResponse optimizeNonTerminal(Loadout currentLoadout, ArmorType armorType, ArmorPiece armorPiece) {
        Loadout nextLoadout = Loadout.builder(currentLoadout).withArmorPiece(armorPiece).build();
        OptimizerResponse nextResponse = bestLoadoutsGiven(nextLoadout, nextArmorType(armorType));

        if(nextResponse.getArmorPiecesToAdd().isEmpty()) {
            double score = scoringFunction.apply(nextLoadout);
            return OptimizerResponse.of(ImmutableList.of(ImmutableList.of(armorPiece)), score);
        }

        return bestLoadoutsGiven(nextLoadout, nextArmorType(armorType)).
            getArmorPiecesToAdd().
            stream().
            map((List<ArmorPiece> ps) -> {
                List<ArmorPiece> nextPiecesToAdd = ImmutableList.<ArmorPiece> builder().add(armorPiece).addAll(ps).build();
                Loadout loadout = Loadout.builder(nextLoadout).withArmorPieces(nextPiecesToAdd).build();
                double score = scoringFunction.apply(loadout);
                return OptimizerResponse.of(ImmutableList.of(nextPiecesToAdd), score);
            }).
            reduce(OptimizerResponse.empty(), OptimizerResponse.merger());
    }

    private OptimizerResponse optimizeTerminal(Loadout currentLoadout, ArmorPiece armorPiece) {
        List<ArmorPiece> armorPiecesToAdd = ImmutableList.of(armorPiece);
        Loadout resultingLoadout = Loadout.builder(currentLoadout).withArmorPiece(armorPiece).build();
        double score = scoringFunction.apply(resultingLoadout);
        return OptimizerResponse.of(ImmutableList.of(armorPiecesToAdd), score);
    }

    private static boolean hasNextArmorType(ArmorType armorType) {
        return armorType != ArmorType.CHARM;
    }

    private static ArmorType nextArmorType(ArmorType armorType) {
        if(armorType == null) {
            return ArmorType.HEAD;
        }

        switch(armorType) {
            case HEAD:
                return ArmorType.ARMS;
            case ARMS:
                return ArmorType.BODY;
            case BODY:
                return ArmorType.WAIST;
            case WAIST:
                return ArmorType.LEGS;
            case LEGS:
                return ArmorType.CHARM;
            case CHARM:
            default:
                throw new NoSuchElementException();
        }
    }
}
