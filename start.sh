#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MAIN_DIR="$ROOT_DIR/main"
PID_FILE="$ROOT_DIR/.trackwarrants.pid"
LOG_FILE="$ROOT_DIR/trackwarrants.log"
JAR_FILE="$MAIN_DIR/target/main-0.0.1-SNAPSHOT.jar"
PORT="${PORT:-8080}"

is_pid_running() {
  local pid="$1"
  [[ -n "$pid" ]] && kill -0 "$pid" 2>/dev/null
}

if [[ -f "$PID_FILE" ]]; then
  EXISTING_PID="$(cat "$PID_FILE" || true)"
  if is_pid_running "${EXISTING_PID:-}"; then
    echo "TrackWarrants is already running (PID: $EXISTING_PID)."
    exit 0
  fi
  rm -f "$PID_FILE"
fi

if [[ ! -f "$JAR_FILE" ]]; then
  echo "JAR not found at: $JAR_FILE"
  echo "Building application first..."
  (
    cd "$MAIN_DIR"
    mvn -DskipTests package
  )
fi

echo "Starting TrackWarrants..."
nohup java -jar "$JAR_FILE" >"$LOG_FILE" 2>&1 &
APP_PID=$!
echo "$APP_PID" >"$PID_FILE"

sleep 2
if is_pid_running "$APP_PID"; then
  echo "Started (PID: $APP_PID)"
  echo "Log: $LOG_FILE"
  if command -v lsof >/dev/null 2>&1; then
    if lsof -i ":$PORT" >/dev/null 2>&1; then
      echo "Port $PORT is in use (expected for running app)."
    fi
  fi
else
  echo "Failed to start. Check log: $LOG_FILE"
  rm -f "$PID_FILE"
  exit 1
fi

