# KOChanceCalculator
KO Chance Calculator, written by DaWoblefet. Special thanks to Stats for algorithm design.

![Screenshot](https://i.imgur.com/A4XECQL.png)

This program is used to calculate the exact chance of a combination attack KOing a Pokemon (2 or more attacks). It can also be used to get more precise % to KO chances than the damage calculator currently provides. Results are given as a percent, as a raw fraction, and as a simplied fraction.


Example 1: suppose you wanted to calculate the odds of 4 Attack Incineroar and +2 Timid Xerneas Moonblast KOing a 252 HP / 4 Sp. Def Stakataka when used together as a combination attack.

Steps:
1) Input the HP stat of the target Pokemon. In this case, Stakataka has 168 HP.
2) Copy and paste the damage rolls for each of your attacks from the damage calculator (https://trainertower.com/damagecalc/). In this case, here are the damage rolls for each:

- +2 252 SpA Fairy Aura Xerneas Moonblast vs. 252 HP / 4 SpD Stakataka: 106-126 (63 - 75%)
(106, 108, 109, 110, 111, 113, 114, 115, 117, 117, 119, 120, 121, 123, 124, 126)
- 4 Atk Incineroar Flare Blitz vs. 252 HP / 0 Def Stakataka: 42-49 (25 - 29.1%)
(42, 42, 42, 43, 43, 43, 45, 45, 45, 46, 46, 46, 48, 48, 48, 49)

You want to paste in just the numbers between the parentheses. So paste in 106, 108, 109, 110, 111, 113, 114, 115, 117, 117, 119, 120, 121, 123, 124, 126 and 42, 42, 42, 43, 43, 43, 45, 45, 45, 46, 46, 46, 48, 48, 48, 49 for set 1 and set 2.

3) Press Calculate. In our case, we see this combination attack has only a 17.578125% chance to KO. Not too great.

Let's add U-turn chip damage and see what happens. To add U-turn, simply press "+" and paste in the damage rolls for U-turn. In this case, it's 8, 8, 8, 8, 8, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10. After pressing Calculate, you see that the odds of KOing Stakataka now have jumped considerably, to 60.791015625%!


Example 2: Let's say you wanted to calculate the odds of Breloom's 4-hit Bullet Seed KOing Pheromosa (I just pulled this out of thin air).

The damage rolls are as follows for each hit: 33, 33, 34, 34, 35, 35, 36, 36, 36, 36, 37, 37, 38, 38, 39, 39.

Copy and paste this in 4 times. We see the given percent chance to KO is 25.7568359375%. Notice how much more precise this is compared to the Showdown/Trainer Tower-style damage calculators:
- 252 Atk Technician Breloom Bullet Seed (4 hits) vs. 4 HP / 0 Def Pheromosa: 132-156 (89.7 - 106.1%) -- approx. 18.8% chance to OHKO

NOTE: if you add a lot of damage rolls (like more than 5), the program may lag and run more slowly, but it's working.

- To compile: `javac KOChanceGUI.java`
- To run: `java KOChanceGUI`
- To build an executable jar: `-jar cvfm KOChanceCalculator.jar manifest.MF KOChanceGUI.class woblescientist.png`
