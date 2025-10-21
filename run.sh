#!/usr/bin/env bash
set -euo pipefail

SRC_DIR="src"
BIN_DIR="bin"
JAVA_OPTS="${JAVA_OPTS:-}"
CLASSPATH="${CLASSPATH:-$BIN_DIR}"

compile_all() {
  echo "🧩 Compiling Java sources from '$SRC_DIR' to '$BIN_DIR'..."
  mkdir -p "$BIN_DIR"
  sources=()
  while IFS= read -r file; do
    sources+=("$file")
  done < <(find "$SRC_DIR" -type f -name "*.java" | sort)

  if [[ ${#sources[@]} -eq 0 ]]; then
    echo "❌ No Java sources found under '$SRC_DIR'."
    exit 1
  fi

  javac -encoding UTF-8 -d "$BIN_DIR" -cp "$CLASSPATH" "${sources[@]}"
  echo "✅ Compilation complete."
}

run_class() {
  local main_class="$1"; shift || true
  echo "🚀 Running ${main_class} $*"
  java -cp "$CLASSPATH" $JAVA_OPTS "$main_class" "$@"
}

# --- report 下のエントリ ---
run_report_ism_arex_jggm() { run_class "report.TSIsmArexJggM" "$@"; }
run_report_ms_arex_jggm()  { run_class "report.TSMsArexJggM"  "$@"; }

run_report_all() {
  run_report_ism_arex_jggm "$@"
  run_report_ms_arex_jggm  "$@"
}

clean() {
  echo "🧹 Cleaning build directory '$BIN_DIR'..."
  rm -rf "$BIN_DIR"/*
  echo "✅ Clean complete."
}

usage() {
  cat <<'EOF'
Usage: ./run.sh <command> [args...]

Commands:
  compile                         ソースを全部コンパイル（src → bin）
  clean                           bin を削除
  report-ism [args...]            report.TSIsmArexJggM を実行
  report-ms  [args...]            report.TSMsArexJggM  を実行
  report-all [args...]            上記2つを順に実行
  run <FQCN> [args...]            任意のメインクラスを実行（例: run report.TSUndxMggS）

Examples:
  ./run.sh compile
  ./run.sh report-ism
  ./run.sh report-ms 100 0.8
  ./run.sh report-all
  ./run.sh run report.TSUndxMggS --seed 42
EOF
}

cmd="${1:-}"; shift || true
case "$cmd" in
  compile)     compile_all ;;
  clean)       clean ;;
  report-ism)  compile_all; run_report_ism_arex_jggm "$@";;
  report-ms)   compile_all; run_report_ms_arex_jggm  "$@";;
  report-all)  compile_all; run_report_all "$@";;
  run)         compile_all; [[ -n "${1:-}" ]] || { echo "❌ FQCN required. See 'run <FQCN>' in usage."; exit 1; }; run_class "$@";;
  ""|-h|--help|help) usage ;;
  *) echo "❌ Unknown command: $cmd"; usage; exit 1 ;;
esac
