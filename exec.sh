#!/bin/sh
cp config-waxman config
java -jar jmcritmcast.jar > result-waxman.txt
cp config-barabasi config
java -jar jmcritmcast.jar > result-barabasi.txt
