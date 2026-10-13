#!/usr/bin/env bash
# ==============================================================================
# dev-live.sh — Real-Time Live Development & Phone Mirroring Runner
# Evrry Super App — Elifsi Technologies Private Limited
# ==============================================================================

set -e

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# Ensure adb is in PATH
if ! command -v adb &> /dev/null; then
  if [ -f "$HOME/Android/Sdk/platform-tools/adb" ]; then
    export PATH="$HOME/Android/Sdk/platform-tools:$PATH"
  fi
fi

TARGET="${1:-help}"

case "$TARGET" in
  mirror)
    if ! command -v scrcpy &> /dev/null; then
      echo "❌ scrcpy is not installed yet."
      echo "👉 Please run: sudo apt install -y scrcpy"
      exit 1
    fi
    echo "📱 Launching phone mirror (scrcpy)..."
    scrcpy --stay-awake --window-title "Evrry Device Mirror" &
    ;;

  consumer)
    echo "🚀 Building & pushing Evrry Consumer App..."
    cd "$REPO_ROOT/apps/consumer/android"
    ./gradlew installDebug
    echo "📲 Launching Consumer app on phone screen..."
    adb shell am start -n com.elifsi.evrry.consumer.debug/.MainActivity
    echo "✅ Consumer App refreshed live on device!"
    ;;

  partner)
    echo "🚀 Building & pushing Evrry Partner App..."
    cd "$REPO_ROOT/apps/partner/android"
    ./gradlew installDebug
    echo "📲 Launching Partner app on phone screen..."
    adb shell am start -n com.elifsi.evrry.partner.debug/.MainActivity
    echo "✅ Partner App refreshed live on device!"
    ;;

  status)
    echo "🔍 Checking connected devices..."
    adb devices -l
    ;;

  *)
    echo "=================================================================="
    echo "  EVRRY REAL-TIME PHONE DEVELOPMENT RUNNER"
    echo "=================================================================="
    echo "Usage:"
    echo "  ./scripts/dev-live.sh mirror      -> Mirror phone screen to your PC (scrcpy)"
    echo "  ./scripts/dev-live.sh consumer    -> Compile & auto-launch Consumer App"
    echo "  ./scripts/dev-live.sh partner     -> Compile & auto-launch Partner App"
    echo "  ./scripts/dev-live.sh status      -> Check phone connection"
    echo "=================================================================="
    ;;
esac
