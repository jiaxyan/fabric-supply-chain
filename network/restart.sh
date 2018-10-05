#!/bin/bash
echo "Current dir:"
pwd
echo "#########################restarting fabri#########################"
./stop.sh
./teardown.sh
./build.sh
