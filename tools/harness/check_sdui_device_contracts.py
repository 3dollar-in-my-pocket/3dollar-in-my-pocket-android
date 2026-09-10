#!/usr/bin/env python3
"""Validate captured SDK ad states, reply fields and the eight fixture scroll runs."""

import argparse
import json
import re
from pathlib import Path
import xml.etree.ElementTree as ET


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("evidence", type=Path)
    args = parser.parse_args()

    def texts(file):
        root = ET.parse(args.evidence / file)
        return {node.get("text") for node in root.iter("node") if node.get("text")}

    assert {"ad-first:Loaded", "ad-second:Loaded", "ad-height-dp:144"} <= texts("66-demo-ads-loaded.xml")
    assert {"ad-first:Failed", "ad-second:Failed", "ad-height-dp:0"} <= texts("67-demo-ads-failed.xml")
    for host in ("full", "home"):
        assert {"테스트 사장님", "2026.09.09", "답글 계약 확인"} <= texts(f"68-reply-{host}.xml")

    host_result = (args.evidence / "89-final-host-review-green.log").read_text()
    assert host_result.startswith("PASS:") and "FAIL:" not in host_result
    assert "missing-coordinate visit stays open" in host_result
    assert "fake review save refreshes once" in host_result
    assert "post" in texts("88-home-post-present.xml")
    absent = ET.parse(args.evidence / "88-home-post-absent.xml")
    assert any(node.get("enabled") == "false" for node in absent.iter("node"))

    pageviews = (args.evidence / "87-home-pageviews.log").read_text()
    marker = re.search(r"(SDUI_HOME_\d+)_BEGIN", pageviews).group(1)
    for start, end in (("BEGIN", "LEAVE"), ("RETURN", "END")):
        part = pageviews.split(marker + "_" + start, 1)[1].split(marker + "_" + end, 1)[0]
        events = re.split(r"(?=\[SDPageViewLog\]|\[SDClickLog\]|\[SDImpressionLog\]|\[LogManager\]:)", part)
        home = [event for event in events if (
            event.startswith("[SDPageViewLog]") or event.startswith("[LogManager]: PageView")
        ) and re.search(r"=> screen: home\s", event)]
        assert len(home) == 1 and home[0].startswith("[SDPageViewLog]") and "preset:" in home[0]

    captured = json.loads((args.evidence / "lower-fixture-matrix.json").read_text())
    for fixture in ("user", "boss", "verified-user", "verified-boss"):
        for host in ("full", "home"):
            steps = [row for row in captured if row["fixture"] == fixture and row["container"] == host]
            assert {row["step"] for row in steps} == set(range(6))
            assert all(row["labels"] for row in steps)
            for step in range(6):
                root = ET.parse(args.evidence / f"lower-{fixture}-{host}-{step}.xml")
                assert any(node.get("package") == "com.zion830.threedollars.dev" for node in root.iter("node"))
    print("PASS: SDK ads, replies, host regression, Home POST/pageviews, eight fixture scroll runs")


if __name__ == "__main__":
    main()
