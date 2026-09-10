#!/usr/bin/env python3
"""Assert shared SDUI rendering from the debug preview's real UI tree and pixels."""

import argparse
from pathlib import Path
import re
import xml.etree.ElementTree as ET

from PIL import Image


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("xml", type=Path)
    parser.add_argument("png", type=Path)
    args = parser.parse_args()
    xml = args.xml.read_text()
    root = ET.fromstring(xml[xml.index("<?xml"):xml.rindex("</hierarchy>") + len("</hierarchy>")])
    nodes = list(root.iter("node"))
    texts = [node.get("text", "") for node in nodes]
    failures = []
    if "zero-image-dp:0x0" not in texts:
        failures.append(f"explicit nonpositive image sizes must not use a fallback: {[t for t in texts if t.startswith('zero-image-dp:')]}")
    if "image-dp:120x75" not in texts:
        failures.append(f"image must shrink with its ratio: {[t for t in texts if t.startswith('image-dp:')]}")
    if "empty-chip-dp:0x0" not in texts:
        failures.append(f"empty chip must not reserve content space: {[t for t in texts if t.startswith('empty-chip-dp:')]}")
    if not {"HEADER SUBTITLE", "HEADER MORE"}.issubset(texts):
        failures.append("header subtitle/trailing action must be displayed")
    if "ad-states:2" not in texts:
        failures.append(f"every advertising card must have its own state: {[t for t in texts if t.startswith('ad-states:')]}")
    chip = next((node for node in nodes if node.get("content-desc") == "probe-chip-end"), None)
    if chip is None:
        failures.append("chip marker missing from accessibility tree")
    else:
        x1, y1, x2, y2 = map(int, re.findall(r"\d+", chip.get("bounds", "")))
        image = Image.open(args.png).convert("RGB")
        icon_x = []
        ink_x = []
        for y in range(y1, y2):
            for x in range(x1, x2):
                red, green, blue = image.getpixel((x, y))
                if red > 220 and green < 40 and blue > 220:
                    icon_x.append(x)
                elif max(red, green, blue) < 160:
                    ink_x.append(x)
        if not icon_x or not ink_x:
            failures.append("chip icon/text pixels unavailable")
        elif min(icon_x) <= max(ink_x):
            failures.append("END chip image must follow the text")
    for failure in failures:
        print(f"FAIL: {failure}")
    if failures:
        raise SystemExit(1)
    print("PASS: image aspect ratio, empty chip, END ordering, header fields and advertising states")


if __name__ == "__main__":
    main()
