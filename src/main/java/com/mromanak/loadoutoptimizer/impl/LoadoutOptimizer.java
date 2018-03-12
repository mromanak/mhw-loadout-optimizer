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

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoadoutOptimizer {

    private final Map<ArmorType, List<ArmorPiece>> armorPieces;
    private final LoadoutScoringFunction scoringFunction;
    private final Map<OptimizerRequest, OptimizerResponse> solutionCache = new HashMap<>();

    public static List<Loadout> findBestLoadouts(Collection<ArmorPiece> armorPieces,
        LoadoutScoringFunction scoringFunction)
    {
        return findBestLoadoutsGiven(Loadout.empty(), armorPieces, scoringFunction);
    }

    public static List<Loadout> findBestLoadoutsGiven(Loadout startingLoadout, Collection<ArmorPiece> armorPieces,
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
        OptimizerResponse response = optimizer.findBestLoadoutsGiven(startingLoadout, nextArmorType(null));
        return response.getArmorPiecesToAdd().
            stream().
            map(l -> Loadout.builder(startingLoadout).withArmorPieces(l).build()).
            collect(toList());
    }

    public static List<Loadout> findBestLoadoutsWithSetBonus(Collection<ArmorPiece> setArmorPieces, int minimumPieces,
        Collection<ArmorPiece> otherArmorPieces, LoadoutScoringFunction scoringFunction)
    {
        List<Loadout> combinationsForSetBonus = findCombinations(setArmorPieces, minimumPieces);
        return combinationsForSetBonus.stream().
            map(startingLoadout -> findBestLoadoutsGiven(startingLoadout, otherArmorPieces, scoringFunction)).
            map((List<Loadout> loadouts) -> {
                if(loadouts.isEmpty()) {
                    return OptimizerResponse.empty();
                }

                return OptimizerResponse.ofLoadouts(loadouts, scoringFunction.apply(loadouts.get(0)));
            }).
            reduce(OptimizerResponse.empty(), OptimizerResponse.merger()).
            getArmorPiecesToAdd().
            stream().
            map(r -> Loadout.builder().withArmorPieces(r).build()).
            collect(toList());
    }

    private static List<Loadout> findCombinations(Collection<ArmorPiece> armorPieces, int minimumPieces) {
        Map<ArmorType, List<ArmorPiece>> armorPiecesMap = armorPieces.stream().
            collect(toMap(
                ArmorPiece::getArmorType,
                ImmutableList::of,
                (l1, l2) -> ImmutableList.<ArmorPiece>builder().addAll(l1).addAll(l2).build()
            ));

        return findCombinations(Loadout.empty(), armorPiecesMap, nextArmorType(null)).stream().
            filter(l -> l.getArmorPieces().size() >= minimumPieces).
            collect(toList());
    }

    private static List<Loadout> findCombinations(Loadout currentLoadout,
        Map<ArmorType, List<ArmorPiece>> armorPiecesMap, ArmorType armorType)
    {
        if(hasNextArmorType(armorType)) {
            List<ArmorPiece> currentArmorPieces = armorPiecesMap.getOrDefault(armorType, emptyList());
            if(currentArmorPieces.isEmpty()) {
                return findCombinations(currentLoadout, armorPiecesMap, nextArmorType(armorType));
            }

            List<Loadout> loadoutsWithNextPiece = findCombinations(currentLoadout, armorPiecesMap, nextArmorType(armorType));
            List<Loadout> loadoutsToReturn = new ArrayList<>(loadoutsWithNextPiece);
            for (Loadout loadoutWithNextPiece : loadoutsWithNextPiece) {
                for (ArmorPiece armorPiece : currentArmorPieces) {
                    loadoutsToReturn.add(Loadout.builder(loadoutWithNextPiece).
                        withArmorPiece(armorPiece).
                        build());
                }
            }
            return loadoutsToReturn;
        } else {
            List<ArmorPiece> currentArmorPieces = armorPiecesMap.getOrDefault(armorType, emptyList());
            List<Loadout> loadoutsToReturn = new ArrayList<>();
            loadoutsToReturn.add(currentLoadout);
            for (ArmorPiece armorPiece : currentArmorPieces) {
                loadoutsToReturn.add(Loadout.builder().
                    withArmorPiece(armorPiece).
                    build());
            }
            return loadoutsToReturn;
        }
    }

    private OptimizerResponse findBestLoadoutsGiven(Loadout currentLoadout, ArmorType armorType) {
        if(currentLoadout.getArmorPieces().containsKey(armorType)) {
            if(hasNextArmorType(armorType)) {
                return findBestLoadoutsGiven(currentLoadout, nextArmorType(armorType));
            } else {
                return OptimizerResponse.of(emptyList(), scoringFunction.apply(currentLoadout));
            }
        }

        OptimizerRequest request = OptimizerRequest.forLoadout(currentLoadout, armorType);
        if(solutionCache.containsKey(request)) {
            return solutionCache.get(request);
        } else if(hasNextArmorType(armorType)) {
            List<ArmorPiece> currentArmorPieces = armorPieces.getOrDefault(armorType, emptyList());
            if(currentArmorPieces.isEmpty()) {
                OptimizerResponse response = findBestLoadoutsGiven(currentLoadout, nextArmorType(armorType));
                solutionCache.put(request, response);
                return response;
            }

            OptimizerResponse responseWithType = currentArmorPieces.stream().
                map(armorPiece -> optimizeNonTerminal(currentLoadout, armorType, armorPiece)).
                reduce(OptimizerResponse.empty(), OptimizerResponse.merger());
            OptimizerResponse responseWithoutType = findBestLoadoutsGiven(currentLoadout, nextArmorType(armorType));
            OptimizerResponse response = OptimizerResponse.merger().apply(responseWithType, responseWithoutType);
            solutionCache.put(request, response);
            return response;
        } else {
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
        OptimizerResponse nextResponse = findBestLoadoutsGiven(nextLoadout, nextArmorType(armorType));

        if(nextResponse.getArmorPiecesToAdd().isEmpty()) {
            double score = scoringFunction.apply(nextLoadout);
            return OptimizerResponse.of(ImmutableList.of(ImmutableList.of(armorPiece)), score);
        }

        return findBestLoadoutsGiven(nextLoadout, nextArmorType(armorType)).
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
