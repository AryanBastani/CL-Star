# Decomposed Automata Learning
This repository contains the source codes and results of "Compositional Learning for Interleaving Parallel Automata".
This project is a result of the Mater's research of Faezeh Labbaf at the [Tehran Institute for Advanced Studies(TeIAS)](https://teias.institute/) under the supervision of Hossein Hojjat and Mohammad Mousavi.


## Replicating Experiments
To replicate this experiment, the `Run_experiment` class must be run.
The learnLib and slf4j libraries needs to be installed to properly run the experiments. The jar files of these libraries can be found in [\libs](\libs) directory.

The parameters of the experiment can be modified in [`.experimentProps`](/.experimentProps) file. the experiment properties are as followed:

- __benchmarks_base_dir__: the base directory where benchmarks are in it.
- __benchmarks_file__: a file that local address of benchmarks(a .dot file for each benchmark) is written in it. 
- __result_path__: address of the file that results will be written in it.
- __final_check_mode__: a boolean indicating that whether run the experiments with an extra deterministic equivalence query or not.

## Experiment Results
All the experiment results containing the csv file output of results, the summarised results (statistical analysis) and plots are in [`\Results`](Results) directory. 
the statistical analysis and visualizations are performed in python (the source code is in [Decomposed_Learning_Results.ipynb](/Experiments/Decomposed_Learning_Results.ipynb) file).

The data and plots mentioned before is the results of running the experiment on 100 FSMs consisting of a minimum of two and a maximum of nine components in this
case study.
. Our subject systems have a minimum of 300 states and
a maximum of 3840, and their average number of states is 1278.2 with a standard deviation 847.
