#!/bin/sh

cp config-timecost-waxman config
java -jar timecost.jar > result-timecost-waxman.txt
cp result-timecost-waxman.txt ~/Dropbox

cp config-timecost-barabasi config
java -jar timecost.jar > result-timecost-barabasi.txt
cp result-timecost-barabasi.txt ~/Dropbox
