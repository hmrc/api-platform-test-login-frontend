#!/bin/bash

sm2 --start DATASTREAM

sbt "~run -Dhttp.port=9619 $*"
