#!/usr/bin/env python3
"""Check captured real contributor lifecycle logs and the fake button callback."""

import argparse
from pathlib import Path
import xml.etree.ElementTree as ET


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("evidence", type=Path)
    args = parser.parse_args()

    def delta(before, after):
        old = (args.evidence / before).read_text()
        new = (args.evidence / after).read_text()
        assert new.startswith(old), "Log buffer changed: recapture matching snapshots"
        return new[len(old):]

    resume = delta("38-contributor-log-snapshot.log", "39-contributor-resume-confirm.log")
    assert resume.count("[SDPageViewLog]") == 1
    assert resume.count("store_id: 120024") == 1
    assert "[LogManager]: PageView" not in resume

    edit = delta("39-contributor-resume-confirm.log", "40-contributor-edit.log")
    assert edit.count("[SDClickLog]") == 1
    assert "=> objectId: edit" in edit
    assert "[LogManager]: Click" not in edit

    cancel = delta("40-contributor-edit.log", "41-contributor-edit-cancel.log")
    assert "[SDPageViewLog]" not in cancel
    assert "[LogManager]: PageView" not in cancel

    error = delta("44-before-error-fallback.log", "44-after-error-fallback.log")
    assert error.count("[LogManager]: PageView") == 1
    assert "[SDPageViewLog]" not in error

    tree = ET.parse(args.evidence / "43-contributor-action-green.xml")
    assert "contributor-clicks:outer" in [node.get("text") for node in tree.iter("node")]
    print("PASS: server pageview/resume extras, click once, cancel, error fallback, outer actionBar log")


if __name__ == "__main__":
    main()
