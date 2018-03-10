# Loadout Optimizer for Monster Hunter: World

A program that attempts to efficiently find the most optimal loadout(s) according to a user-provided scoring function.
Uses dynamic programming to reduce search time in exchange for using more memory.

## How To Run
Modify the scoring function at line 30 of `com.mromanak.loadoutoptimizer.LoadoutOptimizerCLI` to reflect the desired
loadout, then invoke the main method to run the search and output the results to the command line as JSON.

## To Do
* **Add a GUI** or really any way to define the criteria without modifying the code itself.
* **Complete the data.** The armor piece data is stored in a tab-separated value file on the classpath. It is not
complete; I've simply been transcribing data from [Kiranico](https://mhworld.kiranico.com/) based on the skills I was
interested in using at a given moment.