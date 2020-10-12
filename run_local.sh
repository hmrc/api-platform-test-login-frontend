#!/bin/bash

sm -f --start ASSETS_FRONTEND -r 3.11.0

sm -f --start DATASTREAM

sbt "~run -Dhttp.port=9619 $*"
