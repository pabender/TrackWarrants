#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PID_FILE="$ROOT_DIR/.trackwarrants.pid"
LOG_FILE="$ROOT_DIR/trackwarrants.log"
PORT="${PORT:-8080}"
PATTERN="main-0.0.1-SNAPSHOT.jar|spring-boot:run|TrackWarrants"

running=false

if [[ -f "$PID_FILE" ]]; then
  PID="$(cat "$PID_FILE" || true)"
  if [[ -n "${PID:-}" ]] && kill -0 "$PID" 2>/dev/null; then
    running=true
    echo "Status: RUNNING"
    echo "PID: $PID (from PID file)"
  fi
fi

if [[ "$running" == false ]]; then
  MATCHES="$(pgrep -af "$PATTERN" || true)"
  if [[ -n "$MATCHES" ]]; then
    running=true
    echo "Status: RUNNING"
    echo "Processes:"
    echo "$MATCHES"
  else
    echo "Status: STOPPED"
  fi
fi

if command -v lsof >/dev/null 2>&1; then
  if lsof -i ":$PORT" >/dev/null 2>&1; then
    echo "Port $PORT: IN USE"
  else
    echo "Port $PORT: FREE"
  fi
else
  echo "Port check skipped: lsof not available"
fi

if [[ -f "$LOG_FILE" ]]; then
  echo "Log: $LOG_FILE"
else
  echo "Log: (not created yet)"
fi

