#!/bin/sh
cp config-drain-waxman config
java -jar multidrain.jar > result-drain-waxman.txt
cp config-drain-barabasi config
java -jar multidrain.jar > result-drain-barabasi.txt
