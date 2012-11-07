#!/bin/sh
cp config-cost-waxman config
java -jar multicost.jar > result-cost-waxman.txt
cp config-cost-barabasi config
java -jar multicost.jar > result-cost-barabasi.txt
