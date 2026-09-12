#!/usr/bin/env bash
# Scarica una toolchain Android minima (aapt2, android.jar, d8, apksigner) in .toolchain/
# Non serve Android Studio: i binari arrivano da pacchetti npm pubblici che li ridistribuiscono.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SDK="${FUMINO_SDK:-$ROOT/.toolchain}"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT

mkdir -p "$SDK"

if [ ! -x "$SDK/aapt2" ]; then
  echo "==> aapt2 (linux x64)"
  curl -sSL -o "$TMP/aapt.tgz" https://registry.npmjs.org/aaptjs3/-/aaptjs3-2.0.2.tgz
  tar xzf "$TMP/aapt.tgz" -C "$TMP"
  cp "$TMP/package/bin/x64/linux/aapt2" "$SDK/aapt2"
  chmod +x "$SDK/aapt2"
fi

if [ ! -f "$SDK/android.jar" ]; then
  echo "==> android.jar (API 34), d8, apksigner"
  curl -sSL -o "$TMP/minapk.tgz" https://registry.npmjs.org/@drxiaozhi/minapk/-/minapk-0.3.0.tgz
  tar xzf "$TMP/minapk.tgz" -C "$TMP"
  cp "$TMP/package/tools/android.jar" "$SDK/android.jar"
  cp "$TMP/package/tools/d8.jar" "$SDK/d8.jar"
  cp "$TMP/package/tools/apksigner.jar" "$SDK/apksigner.jar"
fi

if [ ! -f "$SDK/dx.jar" ]; then
  echo "==> dx (dexer AOSP, da Maven Central)"
  curl -sSL -o "$SDK/dx.jar" \
    https://repo1.maven.org/maven2/com/jakewharton/android/repackaged/dalvik-dx/16.0.1/dalvik-dx-16.0.1.jar
fi

echo "Toolchain pronta in $SDK"
