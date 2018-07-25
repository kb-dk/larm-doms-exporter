#!/usr/bin/env bash

log() {
    scriptName=`basename "$0"`
    echo `date "+%T [$scriptName] $*"` >> "$(date "+$logFile")"
}

report_error() {
    log "$*"
    echo "$*" >&2
}

cleanup_log() {
    find $logDir -mtime +$logMaxInactiveDays -type f -name "*.log" -delete
}