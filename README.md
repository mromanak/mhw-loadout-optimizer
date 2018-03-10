# Loadout Optimizer for Monster Hunter: World

A program that attempts to efficiently find the most optimal loadout(s) according to a user-provided scoring function.
Uses dynamic programming to reduce search time in exchange for using more memory.

## How To Run
Modify the scoring function at line 30 of `com.mromanak.loadoutoptimizer.LoadoutOptimizerCLI` to reflect the desired
loadout, then invoke the main method to run the search and output the results to the command line as JSON.

Unmodified, the CLI will output a list of loadouts that best match the following criteria:
* Skills:
    * Earplugs 5
    * Tremor Resistance 3
    * Windproof 3
* As many decoration slots as possible. Level 1 slots are considered half as desirable as level 2 or 3 slots.
* As few armor pieces as possible (although with these criteria, it will almost certainly use all 6 pieces.)

## To Do
* **Add a GUI** or really any way to define the criteria without modifying the code itself.
* **Complete the data.** The armor piece data is stored in a tab-separated value file on the classpath. It is not
complete; I've simply been transcribing data from [Kiranico](https://mhworld.kiranico.com/) based on the skills I was
interested in using at a given moment. The names used for armor pieces do not match the in-game names.
* **Add the ability to recommend decorations for loadouts.**
* **Add the ability to account for set bonuses.** The current algorithm will not do well at this, since it has no way to
anticipate the sudden jump in value caused by equipping a certain number of armor pieces from the same set.
