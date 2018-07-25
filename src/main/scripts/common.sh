#!/usr/bin/env bash

log() {
    scriptName=`basename "$0"`
    echo `date "+%T [$scriptName] $*"` >> "$(date "+$logFile")"
}

report_error() {
    log "$*"
    echo "$*" >&2
}