# Readme

## Currently working on
* Single output version of the espresso algorithm.
* During development of this phase, some components are already being prepared for the multiple output version.

## Done so far
* Classes (use Cover and Cube) to model boolean functions. Check test cases for usage.
* Minimization algorithm Simplify for single output boolean functions. It's been tested on several cases and it seems to work. This minimizer is faster than Espresso but less powerful so to say.
* A crippled version of Espresso minimization algorithm lives! This crippled version consists of single Expand and Irredundant step. So it will (as much as possible) expand all implicants into prime implicants and then remove redundant prime implicants.