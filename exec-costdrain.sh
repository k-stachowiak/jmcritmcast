#!/bin/sh

cp config-costdrain-waxman config
java -jar multicostdrain.jar > result-costdrain-waxman.txt
cp result-costdrain-waxman.txt ~/Dropbox/

cp config-costdrain-barabasi config
java -jar multicostdrain.jar > result-costdrain-barabasi.txt
cp result-costdrain-barabasi.txt ~/Dropbox/
