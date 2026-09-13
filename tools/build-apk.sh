#!/usr/bin/env bash
# Compila fumINO in un APK firmato senza Gradle e senza Android Studio.
#   ./tools/build-apk.sh            -> dist/fumINO.apk
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SDK="${FUMINO_SDK:-$ROOT/.toolchain}"
APP="$ROOT/app/src/main"
BUILD="$ROOT/build"
OUT="$ROOT/dist/fumINO.apk"
KEYSTORE="${FUMINO_KEYSTORE:-$ROOT/.keystore/fumino.jks}"
PASSFILE="$(dirname "$KEYSTORE")/password.txt"

MIN_SDK=24
TARGET_SDK=34
VERSION_CODE="${VERSION_CODE:-3}"
VERSION_NAME="${VERSION_NAME:-1.3}"

if [ ! -x "$SDK/aapt2" ] || [ ! -f "$SDK/android.jar" ] || [ ! -f "$SDK/dx.jar" ]; then
  "$ROOT/tools/fetch-toolchain.sh"
fi

rm -rf "$BUILD"
mkdir -p "$BUILD/res" "$BUILD/gen" "$BUILD/classes" "$ROOT/dist" "$(dirname "$KEYSTORE")"

echo "==> 1/6 risorse"
"$SDK/aapt2" compile --dir "$APP/res" -o "$BUILD/res.zip" >/dev/null

echo "==> 2/6 collegamento risorse e manifest"
"$SDK/aapt2" link \
  -o "$BUILD/base.apk" \
  -I "$SDK/android.jar" \
  --manifest "$APP/AndroidManifest.xml" \
  --java "$BUILD/gen" \
  --min-sdk-version "$MIN_SDK" \
  --target-sdk-version "$TARGET_SDK" \
  --version-code "$VERSION_CODE" \
  --version-name "$VERSION_NAME" \
  --no-version-vectors \
  "$BUILD/res.zip"

echo "==> 3/6 compilazione Java"
find "$APP/java" "$BUILD/gen" -name '*.java' > "$BUILD/sources.txt"
javac -nowarn -encoding UTF-8 -source 8 -target 8 \
  -bootclasspath "$SDK/android.jar" \
  -d "$BUILD/classes" @"$BUILD/sources.txt" 2>&1 | grep -v '^Picked up JAVA_TOOL_OPTIONS' || true

echo "==> 4/6 dex"
# Nota: la build di D8 disponibile senza SDK Google va in errore sulle classi
# anonime annidate, quindi usiamo dx (AOSP) pubblicato su Maven Central.
java -cp "$SDK/dx.jar" com.android.dx.command.Main \
  --dex --min-sdk-version="$MIN_SDK" \
  --output="$BUILD/classes.dex" "$BUILD/classes" 2>&1 | grep -v '^Picked up JAVA_TOOL_OPTIONS' || true
test -f "$BUILD/classes.dex"

echo "==> 5/6 confezionamento e allineamento"
cp "$BUILD/base.apk" "$BUILD/unsigned.apk"
(cd "$BUILD" && zip -q -X "unsigned.apk" classes.dex)
if [ -d "$APP/assets" ]; then
  (cd "$APP" && zip -q -r "$BUILD/unsigned.apk" assets)
fi
python3 "$ROOT/tools/zipalign.py" "$BUILD/unsigned.apk" "$BUILD/aligned.apk"

echo "==> 6/6 firma"
# La password della chiave non sta nel repository: vive accanto alla chiave,
# in .keystore/ (ignorato da git), oppure in $FUMINO_KEYPASS.
if [ -n "${FUMINO_KEYPASS:-}" ]; then
  KEYPASS="$FUMINO_KEYPASS"
elif [ -f "$PASSFILE" ]; then
  KEYPASS="$(cat "$PASSFILE")"
else
  KEYPASS="$(head -c 24 /dev/urandom | base64 | tr -d '/+=' | head -c 24)"
  printf '%s' "$KEYPASS" > "$PASSFILE"
  chmod 600 "$PASSFILE"
fi

if [ ! -f "$KEYSTORE" ]; then
  keytool -genkeypair -v -keystore "$KEYSTORE" -alias fumino \
    -keyalg RSA -keysize 2048 -validity 10000 \
    -storepass "$KEYPASS" -keypass "$KEYPASS" \
    -dname "CN=fumINO, OU=App, O=fumINO, L=Italia, C=IT" >/dev/null 2>&1
  echo "    nuova chiave creata in $KEYSTORE, password in $PASSFILE"
  echo "    CONSERVA ENTRAMBI: senza, gli aggiornamenti non si installano sopra l'app esistente"
fi
java -jar "$SDK/apksigner.jar" sign \
  --ks "$KEYSTORE" --ks-key-alias fumino \
  --ks-pass "pass:$KEYPASS" --key-pass "pass:$KEYPASS" \
  --v1-signing-enabled true --v2-signing-enabled true --v3-signing-enabled true \
  --out "$OUT" "$BUILD/aligned.apk" 2>&1 | grep -v '^Picked up JAVA_TOOL_OPTIONS' || true

java -jar "$SDK/apksigner.jar" verify --print-certs "$OUT" 2>&1 \
  | grep -v '^Picked up JAVA_TOOL_OPTIONS' | head -4 || true

echo
echo "APK pronto: $OUT  ($(du -h "$OUT" | cut -f1))"
