#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
AGENT="both"
TARGET_DIR=""
FORCE=0
PRUNE=0

usage() {
  cat <<'EOF'
Usage: setup_skills.sh [--agent codex|claude|both] [--target DIR] [--force] [--prune] [--refresh]

Create symlinks for all top-level skill folders in this repo into the
skills directory of Codex and/or Claude.

Default skill directories:
  codex   ${CODEX_HOME:-$HOME/.codex}/skills
  claude  $HOME/.claude/skills

Options:
  --agent A     Which agent(s) to install for: codex | claude | both (default: both)
  --target DIR  Install into DIR only (overrides --agent target resolution)
  --force       Replace existing symlinks that point elsewhere
  --prune       Remove symlinks in target that no longer exist in repo
  --refresh     Equivalent to --force --prune
  -h, --help    Show this help
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --agent)
      shift
      [[ $# -gt 0 ]] || { echo "Missing value for --agent" >&2; exit 1; }
      AGENT="$1"
      ;;
    --target)
      shift
      [[ $# -gt 0 ]] || { echo "Missing value for --target" >&2; exit 1; }
      TARGET_DIR="$1"
      ;;
    --force) FORCE=1 ;;
    --prune) PRUNE=1 ;;
    --refresh) FORCE=1; PRUNE=1 ;;
    -h|--help) usage; exit 0 ;;
    *)
      echo "Unknown argument: $1" >&2
      usage >&2
      exit 1
      ;;
  esac
  shift
done

resolve_targets() {
  if [[ -n "$TARGET_DIR" ]]; then
    printf '%s\n' "$TARGET_DIR"
    return
  fi
  case "$AGENT" in
    codex)  printf '%s\n' "${CODEX_HOME:-$HOME/.codex}/skills" ;;
    claude) printf '%s\n' "$HOME/.claude/skills" ;;
    both)
      printf '%s\n' "${CODEX_HOME:-$HOME/.codex}/skills"
      printf '%s\n' "$HOME/.claude/skills"
      ;;
    *)
      echo "Invalid --agent value: $AGENT (expected codex|claude|both)" >&2
      exit 1
      ;;
  esac
}

link_into() {
  local target_dir="$1"
  mkdir -p "$target_dir"
  declare -A REPO_SKILLS=()

  for skill_dir in "$ROOT_DIR"/*; do
    [[ -d "$skill_dir" ]] || continue
    [[ -f "$skill_dir/SKILL.md" ]] || continue
    local skill_name target_link
    skill_name="$(basename "$skill_dir")"
    REPO_SKILLS["$skill_name"]=1
    target_link="$target_dir/$skill_name"

    if [[ -L "$target_link" ]]; then
      local current_target
      current_target="$(readlink "$target_link")"
      if [[ "$current_target" == "$skill_dir" ]]; then
        echo "OK   $skill_name -> $current_target"
        continue
      fi
      if [[ "$FORCE" -eq 1 ]]; then
        rm "$target_link"
      else
        echo "SKIP $skill_name (already linked to $current_target)" >&2
        continue
      fi
    elif [[ -e "$target_link" ]]; then
      echo "SKIP $skill_name (path exists and is not a symlink): $target_link" >&2
      continue
    fi

    ln -s "$skill_dir" "$target_link"
    echo "LINK $skill_name -> $skill_dir"
  done

  if [[ "$PRUNE" -eq 1 ]]; then
    for target_link in "$target_dir"/*; do
      [[ -L "$target_link" ]] || continue
      local name
      name="$(basename "$target_link")"
      if [[ -z "${REPO_SKILLS[$name]+x}" ]]; then
        rm "$target_link"
        echo "PRUNE $name"
      fi
    done
  fi

  echo "Done. Skills linked into: $target_dir"
}

while IFS= read -r dir; do
  [[ -n "$dir" ]] || continue
  echo "==> $dir"
  link_into "$dir"
done < <(resolve_targets)
