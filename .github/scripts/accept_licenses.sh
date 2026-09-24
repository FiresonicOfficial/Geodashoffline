#!/usr/bin/env bash
set -e

# Target all common Android SDK locations on GitHub Actions runners and Linux environments
TARGET_DIRS=(
  "${ANDROID_HOME}/licenses"
  "${ANDROID_SDK_ROOT}/licenses"
  "/usr/local/lib/android/sdk/licenses"
  "$HOME/Android/Sdk/licenses"
  "/opt/android-sdk/licenses"
)

for D in "${TARGET_DIRS[@]}"; do
  [ -z "$D" ] && continue
  mkdir -p "$D" 2>/dev/null || true

  # Android SDK License
  cat > "$D/android-sdk-license" 2>/dev/null << 'EOF' || true
24333f8a63b6825ea9c5514f83c2829b004d1fee
d56f5187479451eabf01fb78af6dfcb131a6481e
e6b7c2ab7fa2298e15165e1118370ff9c1f82663
89388d6e79037b964a1f1ec640e3f73d8684755a
27229f7d25e1a2f6ca3c023fe21f379f8ef4e112
EOF

  # Android SDK Preview License
  cat > "$D/android-sdk-preview-license" 2>/dev/null << 'EOF' || true
84831b9409646a918e30573bab4c9c91346d8abd
7993a44a35a40a15b1d68c814441464e137b2fc8
EOF

  # Android Google TV License
  cat > "$D/android-googletv-license" 2>/dev/null << 'EOF' || true
601085b94cd77f0b54ff86406957099ebe79c4d6
601085b94cd77f6b54b86eebd637d4c2a17b4a84
EOF

  # Android Google XR License
  cat > "$D/android-googlexr-license" 2>/dev/null << 'EOF' || true
ceff83576aac4f7f37cb98fe189e9fb3c49d3b81
EOF

  # Android SDK ARM DBT License
  cat > "$D/android-sdk-arm-dbt-license" 2>/dev/null << 'EOF' || true
859f317696f67ef3d7f30a50a5560e7834b43903
EOF

  # Google Glass / GDK License
  cat > "$D/google-gdk-license" 2>/dev/null << 'EOF' || true
33b6a2b64607f11b759f320ef9dff4ae5c47d97a
33b6a2b64f07f11d23a55955ab86103a4930c657
258832a828114f7b60098f98ec8294a56c071b7a
d975f751698a77b662f1254ddbeed3901e976f5a
89388d6e79037b964a1f1ec640e3f73d8684755a
EOF

  # MIPS Android Sysimage License
  cat > "$D/mips-android-sysimage-license" 2>/dev/null << 'EOF' || true
e9acab5b5fbb560a72cfaecce8946896ff6aab9d
e9acab5b5fbb560a72cfa4f46c0b907b9468f2d0
EOF
done

# Also run sdkmanager accept if available
if command -v sdkmanager >/dev/null 2>&1; then
  yes 2>/dev/null | sdkmanager --licenses >/dev/null 2>&1 || true
fi

echo "Android SDK and GDK licenses accepted successfully."
