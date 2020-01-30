// node src/main/node/loadouts.js | pbcopy

const _ = require('lodash')

const MR_1_SETS = '^(Vespoid|Kestodon|Gajau|Shamos|Hornetaur|Gastodon|Barnos|Wulg|Cortos|Beo|Banbaro|Bone|Alloy|High Metal|Jagras|Kulu|Pukei|Kadachi|Barroth|Jyura|Tzitzi|Girros|Dodogama|Pearlspring) .*'
const MR_2_SETS = '^(Viper Kadachi|Lumu( Phantasm)?|Pukei Lagoon|Rathian|Anja|Rath Heart|Baan) .*'
const MR_3_SETS = '^(Barioth|Nargacuga|Glavenus|Tigrex|Brachydios|Ingot|Clockwork|Artian|Rathalos|Diablos|Legiana|Lavasioth|Uragaan|Odogaron) .*'
const MR_4_SETS = '^(Hoarcry|Fulgur Anja|Death Garon|Acidic Glavenus|Rath Soul|Diablos Nero) .*'
const MR_5_SETS = '^(Rimeguard|Pride|Tentacle|Fellshroud|Damascus|Dober|Kushala|Kaiser|Kirin|Esurient) .*'
const MR_6_SETS = '^(Shara Ishvalda|Guildwork|Zinogre|Zorah|Garuga|Empress|Rex Roar|Ruinous) .*|^(Silver Sol|Golden Lune).*'
const MR_DLC_1_SETS = '^(Golden (Headdress|Haori|Kote|Obi|Hakama)) .*'
const MR_DLC_2_SETS = '^(Stygian|Safi Crested) .*'
const MR_ARENA_SETS = '^(Black Belt|Guild Palace) .*'
const MR_JOY_FEST_SETS = '^(Oolong|Buff|Duffel Penguin|Sealed Dragon|Banuk) .*'
const MR_APPRECIATION_FEST_SETS = '^(Astral|Wyverian) .*'
const MR_PS4_EXCLUSIVES = '^(Banuk) .*'
const MR_SIEGE_SETS = '^(Safi Crested) .*'
const GUIDING_LANDS_CHARMS = '^(Blaze|Friendship|Frost|Flood|Grit|Ironside|Shock|Wyrmsbane) Charm V' +
  '|^(Blaze|Challenger|Demolition|Earplugs|Evasion|Fitness|Frost|Flood|Handicraft|Flood|Fury|Grit|Immobilize|Power|Shock|Tranq|Venom|Windproof|Wyrmsbane) Charm IV' +
  '|^(Awakening|Breaker|Challenger|Evasion|Focus|Fury|Guardian|Invigorate|Impact|Leaping|Marathon|Mushroom|Power|Surge|Tremor|Unscathed) Charm III' +
  '|^(Critical|Dispersal|Normal Shots|Penetration|Phoenix) Charm II'

const requestTemplate = {
  desiredSkills: [
    {
      name: 'Attack Boost',
      maximum: 7,
      weight: 1
    },
    {
      name: 'Agitator',
      maximum: 5,
      weight: 2
    },
    {
      name: 'Dragon Resistance',
      maximum: 3,
      weight: 0
    }
  ],
  requiredSkills: [
    {
      name: 'Health Boost',
      maximum: 3,
      weight: 1
    },
    {
      name: 'Partbreaker',
      maximum: 3,
      weight: 2
    },
    {
      name: 'Artillery',
      maximum: 3,
      weight: 1
    }
  ],
  targetDefense: 900,
  includeDefenseBoost:       false,
  includeFireResistance:     false,
  includeWaterResistance:    false,
  includeThunderResistance:  false,
  includeIceResistance:      false,
  includeDragonResistance:   true,
  rank: 'Master Rank',
  setBonus: null,
  excludePatterns: [
    // MR_1_SETS,
    // MR_2_SETS,
    // MR_3_SETS,
    // MR_4_SETS,
    // MR_5_SETS,
    MR_6_SETS,
    MR_DLC_1_SETS,
    MR_DLC_2_SETS,
    MR_ARENA_SETS,
    MR_JOY_FEST_SETS,
    MR_APPRECIATION_FEST_SETS,
    MR_PS4_EXCLUSIVES,
    MR_SIEGE_SETS,
    GUIDING_LANDS_CHARMS
  ],
  defenseWeight: 0,
  defenseBucketSize: 20,
  fireResistanceWeight: 0,
  waterResistanceWeight: 0,
  thunderResistanceWeight: 0,
  iceResistanceWeight: 0,
  dragonResistanceWeight: 0,
  resistanceBucketSize: 7,
  negativeResistanceWeightModifier: 3.0,
  level1SlotWeight: 1,
  level2SlotWeight: 2,
  level3SlotWeight: 3,
  level4SlotWeight: 4,
  loadoutSizeWeight: 0
}

function createLoadoutRequest(template) {
  const request = {
    skillWeights: {},
    rank: template.rank,
    setBonus: template.setBonus,
    excludePatterns: template.excludePatterns,
    level1SlotWeight: template.level1SlotWeight,
    level2SlotWeight: template.level2SlotWeight,
    level3SlotWeight: template.level3SlotWeight,
    level4SlotWeight: template.level4SlotWeight,
    defenseWeight: 0,
    defenseBucketSize: template.defenseBucketSize,
    fireResistanceWeight: 0,
    waterResistanceWeight: 0,
    thunderResistanceWeight: 0,
    iceResistanceWeight: 0,
    dragonResistanceWeight: 0,
    resistanceBucketSize: template.resistanceBucketSize,
    negativeResistanceWeightModifier: template.negativeResistanceWeightModifier,
    loadoutSizeWeight: template.loadoutSizeWeight
  }

  let sumProduct = 0
  for (let skill of template.desiredSkills) {
    sumProduct += skill.maximum * skill.weight
  }

  if (template.defenseWeight > 0) {
    request.defenseWeight = template.defenseWeight
  } else {
    request.defenseWeight = sumProduct / template.targetDefense
  }
  setElementalResistances(request, template, sumProduct)

  const skills = [
    ...template.desiredSkills,
    ...getRequiredSkills(template, sumProduct),
    ...getDefenseBoostSkill(template, sumProduct),
    ...getElementalResistanceSkills(template, sumProduct)
  ]
  for (let skill of skills) {
    if (_.has(request.skillWeights, skill.name)) {
      continue
    }

    request.skillWeights[skill.name] = {
      maximum: skill.maximum,
      weight: skill.weight
    }
  }

  request.idealScore = sumProduct * (2 + template.requiredSkills.length)
  if (template.includeFireResistance) {
    request.idealScore += sumProduct
  }
  if (template.includeWaterResistance) {
    request.idealScore += sumProduct
  }
  if (template.includeThunderResistance) {
    request.idealScore += sumProduct
  }
  if (template.includeIceResistance) {
    request.idealScore += sumProduct
  }
  if (template.includeDragonResistance) {
    request.idealScore += sumProduct
  }
  request.idealScore += 6 * template.loadoutSizeWeight
  request.idealScore += 5 * (template.level4SlotWeight + 2 * template.level2SlotWeight)

  return request
}

function setElementalResistances (request, template, sumProduct) {
  if (template.includeFireResistance) {
    if (template.fireResistanceWeight > 0) {
      request.fireResistanceWeight = template.fireResistanceWeight
    } else {
      request.fireResistanceWeight = sumProduct / 20
    }
  }
  if (template.includeWaterResistance) {
    if (template.waterResistanceWeight > 0) {
      request.waterResistanceWeight = template.waterResistanceWeight
    } else {
      request.waterResistanceWeight = sumProduct / 20
    }
  }
  if (template.includeThunderResistance) {
    if (template.thunderResistanceWeight > 0) {
      request.thunderResistanceWeight = template.thunderResistanceWeight
    } else {
      request.thunderResistanceWeight = sumProduct / 20
    }
  }
  if (template.includeIceResistance) {
    if (template.iceResistanceWeight > 0) {
      request.iceResistanceWeight = template.iceResistanceWeight
    } else {
      request.iceResistanceWeight = sumProduct / 20
    }
  }
  if (template.includeDragonResistance) {
    if (template.dragonResistanceWeight > 0) {
      request.dragonResistanceWeight = template.dragonResistanceWeight
    } else {
      request.dragonResistanceWeight = sumProduct / 20
    }
  }
}

function getRequiredSkills(template, sumProduct) {
  const skills = []
  for (let skill of template.requiredSkills) {
    skills.push({
      name: skill.name,
      maximum: skill.maximum,
      weight: sumProduct / skill.maximum
    })
  }
  return skills
}

function getDefenseBoostSkill(template, sumProduct) {
  if (template.includeDefenseBoost) {
    const hasElement = template.includeFireResistance ||
      template.includeWaterResistance ||
      template.includeThunderResistance ||
      template.includeIceResistance ||
      template.includeDragonResistance

    // TODO Determine the order of operations for defense boost
    // let weight = (0.1 * (template.targetDefense + 35)) * sumProduct / (7 * template.targetDefense)
    let weight = ((0.1 * template.targetDefense) + 35) * sumProduct / (7 * template.targetDefense)
    if (hasElement) {
      weight += + 5 * sumProduct / 140
    }
    return [
      {
        name: 'Defense Boost',
        maximum: 7,
        weight: weight
      }
    ]
  } else {
    return []
  }
}

function getElementalResistanceSkills (template, sumProduct) {
  const skills = []

  const weight = (sumProduct/ 3) + (10 * sumProduct /  (3 * template.targetDefense))

  if (template.includeFireResistance) {
    skills.push({
      name: 'Fire Resistance',
      maximum: 3,
      weight
    })
  }
  if (template.includeWaterResistance) {
    skills.push({
      name: 'Water Resistance',
      maximum: 3,
      weight
    })
  }
  if (template.includeThunderResistance) {
    skills.push({
      name: 'Thunder Resistance',
      maximum: 3,
      weight: weight
    })
  }
  if (template.includeIceResistance) {
    skills.push({
      name: 'Ice Resistance',
      maximum: 3,
      weight
    })
  }
  if (template.includeDragonResistance) {
    skills.push({
      name: 'Dragon Resistance',
      maximum: 3,
      weight
    })
  }
  return skills
}

console.log(JSON.stringify(createLoadoutRequest(requestTemplate), null, 4))
