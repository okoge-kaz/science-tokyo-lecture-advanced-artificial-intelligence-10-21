#!/bin/bash

find src -type f -print0 | while IFS= read -r -d '' f; do
  if ! grep -Iq . "$f"; then continue; fi
  if iconv -f UTF-8 -t UTF-8 "$f" >/dev/null 2>&1; then continue; fi
  echo "Converting (SJIS→UTF-8): $f"
  iconv -f CP932 -t UTF-8 "$f" > "$f.tmp" && mv "$f.tmp" "$f"
done
