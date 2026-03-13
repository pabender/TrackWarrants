#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PID_FILE="$ROOT_DIR/.trackwarrants.pid"
PATTERN="main-0.0.1-SNAPSHOT.jar|spring-boot:run|TrackWarrants"

stopped=false

if [[ -f "$PID_FILE" ]]; then
  PID="$(cat "$PID_FILE" || true)"
  if [[ -n "${PID:-}" ]] && kill -0 "$PID" 2>/dev/null; then
    echo "Stopping TrackWarrants (PID: $PID)..."
    kill "$PID" || true
    sleep 1
    if kill -0 "$PID" 2>/dev/null; then
      echo "Process still alive, sending SIGKILL..."
      kill -9 "$PID" || true
    fi
    stopped=true
  fi
  rm -f "$PID_FILE"
fi

if pgrep -f "$PATTERN" >/dev/null 2>&1; then
  echo "Stopping remaining TrackWarrants processes..."
  pkill -f "$PATTERN" || true
  stopped=true
fi

if [[ "$stopped" == true ]]; then
  echo "TrackWarrants stopped."
else
  echo "TrackWarrants is not running."
fi

