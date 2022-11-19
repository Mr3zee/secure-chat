#!/bin/bash

./gradlew distZip distTar installDist

./server/build/install/server/bin/server
