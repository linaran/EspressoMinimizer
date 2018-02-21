# Readme

## Currently working on
* Single output version of the espresso algorithm.
* During development of this phase, some components are already being prepared for the multiple output version.

## Done so far
* Classes (use Cover and Cube) to model boolean functions. Check test cases for usage.
* Minimization algorithm Simplify for single output boolean functions. It's been tested on several cases and it seems to work. This minimizer is faster than Espresso but less powerful so to say.
* A basic Expand-Irredundant-Reduce Espresso algorithm for single output functions has been implemented and tested on smaller functions for correctness regarding minimization. Check out examples to see how to invoke the algorithm.
