#!/bin/bash

./gradlew distZip distTar installDist

./server/application/build/install/application/bin/application
