#!/bin/bash

sm -f --start DATASTREAM

sbt "~run -Dhttp.port=9619 $*"
