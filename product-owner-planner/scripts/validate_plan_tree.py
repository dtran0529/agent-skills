#!/usr/bin/env python3
"""Validate structural completeness of a filesystem-backed product plan."""

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path


TASK_HEADINGS = (
    "Outcome",
    "Scope",
    "Non-scope",
    "Artifacts dự kiến",
    "Dependencies",
    "Run",
    "Expected result",
    "Failure scenarios",
    "Acceptance criteria",
    "Evidence khi review",
)
PLACEHOLDERS = ("Planned task map", "TODO", "TBD")
MARKDOWN_LINK = re.compile(r"\[[^]]+\]\(([^)#]+\.md)(?:#[^)]+)?\)")


def validate(root: Path) -> list[str]:
    errors: list[str] = []
    markdown_files = sorted(root.rglob("*.md"))
    if not markdown_files:
        return [f"No Markdown files found below {root}"]

    for path in markdown_files:
        text = path.read_text(encoding="utf-8")
        relative = path.relative_to(root)

        for target in MARKDOWN_LINK.findall(text):
            resolved = (path.parent / target).resolve()
            if not resolved.is_file():
                errors.append(f"{relative}: broken Markdown link -> {target}")

        for placeholder in PLACEHOLDERS:
            if placeholder in text:
                errors.append(f"{relative}: unresolved placeholder '{placeholder}'")

        if path.name.startswith("task-"):
            for heading in TASK_HEADINGS:
                if f"## {heading}" not in text:
                    errors.append(f"{relative}: missing task heading '## {heading}'")

        if path.name == "README.md" and path != root / "README.md":
            task_files = sorted(path.parent.glob("task-*.md"))
            linked_tasks = {
                target for target in MARKDOWN_LINK.findall(text) if Path(target).name.startswith("task-")
            }
            if task_files and not linked_tasks:
                errors.append(f"{relative}: feature README has task files but links to none")
            for task_file in task_files:
                if task_file.name not in {Path(link).name for link in linked_tasks}:
                    errors.append(f"{relative}: task file is not linked -> {task_file.name}")

    return errors


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("plan_root", type=Path)
    args = parser.parse_args()

    root = args.plan_root.resolve()
    if not root.is_dir():
        print(f"Plan root is not a directory: {root}", file=sys.stderr)
        return 2

    errors = validate(root)
    if errors:
        for error in errors:
            print(f"ERROR: {error}")
        print(f"Validation failed with {len(errors)} error(s).")
        return 1

    print(f"Validation passed: {root}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
