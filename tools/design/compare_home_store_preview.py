#!/usr/bin/env python3
import argparse
import json
import math
from pathlib import Path

from PIL import Image, ImageChops, ImageDraw


def flatten_on_white(image: Image.Image) -> Image.Image:
    rgba = image.convert("RGBA")
    background = Image.new("RGBA", rgba.size, (255, 255, 255, 255))
    background.alpha_composite(rgba)
    return background.convert("RGB")


def mismatch_metrics(diff: Image.Image) -> dict:
    pixels = diff.convert("RGB").getdata()
    total = diff.width * diff.height
    channel_sum = 0
    channel_square_sum = 0
    max_delta = 0
    mismatch_over_8 = 0
    mismatch_over_16 = 0
    mismatch_over_32 = 0

    for red, green, blue in pixels:
        pixel_max = max(red, green, blue)
        max_delta = max(max_delta, pixel_max)
        channel_sum += red + green + blue
        channel_square_sum += red * red + green * green + blue * blue
        if pixel_max > 8:
            mismatch_over_8 += 1
        if pixel_max > 16:
            mismatch_over_16 += 1
        if pixel_max > 32:
            mismatch_over_32 += 1

    channel_count = total * 3
    mae = channel_sum / channel_count
    rmse = math.sqrt(channel_square_sum / channel_count)

    return {
        "mae": round(mae, 4),
        "rmse": round(rmse, 4),
        "max_delta": max_delta,
        "mismatch_percent_over_8": round(mismatch_over_8 / total * 100, 4),
        "mismatch_percent_over_16": round(mismatch_over_16 / total * 100, 4),
        "mismatch_percent_over_32": round(mismatch_over_32 / total * 100, 4),
    }


def draw_label(draw: ImageDraw.ImageDraw, x: int, text: str) -> None:
    draw.text((x, 10), text, fill=(15, 15, 15))


def main() -> int:
    parser = argparse.ArgumentParser(description="Compare the home store preview capture against a Figma PNG.")
    parser.add_argument("--figma", required=True, help="Path to the Figma reference PNG.")
    parser.add_argument("--capture", required=True, help="Path to the full-device app screenshot PNG.")
    parser.add_argument("--out-dir", default="build/design-verification/home-store-preview")
    parser.add_argument("--max-mae", type=float, default=None)
    parser.add_argument("--max-mismatch-percent", type=float, default=None)
    args = parser.parse_args()

    out_dir = Path(args.out_dir)
    out_dir.mkdir(parents=True, exist_ok=True)

    figma = flatten_on_white(Image.open(args.figma))
    capture = flatten_on_white(Image.open(args.capture))

    scale = capture.width / figma.width
    crop_height = int(round(figma.height * scale))
    crop_box = (0, capture.height - crop_height, capture.width, capture.height)
    app_crop = capture.crop(crop_box)
    app_normalized = app_crop.resize(figma.size, Image.Resampling.LANCZOS)

    diff = ImageChops.difference(figma, app_normalized)
    diff_visual = diff.point(lambda value: min(255, value * 4))
    metrics = mismatch_metrics(diff)
    metrics.update(
        {
            "figma_size": list(figma.size),
            "capture_size": list(capture.size),
            "crop_box": list(crop_box),
            "app_crop_size": list(app_crop.size),
        }
    )

    figma_path = out_dir / "figma-reference.png"
    crop_path = out_dir / "app-crop.png"
    diff_path = out_dir / "diff-x4.png"
    contact_sheet_path = out_dir / "comparison.png"
    metrics_path = out_dir / "metrics.json"

    figma.save(figma_path)
    app_normalized.save(crop_path)
    diff_visual.save(diff_path)

    gutter = 16
    label_height = 32
    sheet = Image.new(
        "RGB",
        (figma.width * 3 + gutter * 4, figma.height + label_height + gutter),
        (255, 255, 255),
    )
    draw = ImageDraw.Draw(sheet)
    x1 = gutter
    x2 = figma.width + gutter * 2
    x3 = figma.width * 2 + gutter * 3
    draw_label(draw, x1, "Figma")
    draw_label(draw, x2, "App")
    draw_label(draw, x3, "Diff x4")
    y = label_height
    sheet.paste(figma, (x1, y))
    sheet.paste(app_normalized, (x2, y))
    sheet.paste(diff_visual, (x3, y))
    sheet.save(contact_sheet_path)

    metrics_path.write_text(json.dumps(metrics, ensure_ascii=False, indent=2) + "\n")
    print(json.dumps(metrics, ensure_ascii=False, indent=2))
    print(f"comparison: {contact_sheet_path}")

    failed = False
    if args.max_mae is not None and metrics["mae"] > args.max_mae:
        print(f"MAE {metrics['mae']} exceeded threshold {args.max_mae}")
        failed = True
    if (
        args.max_mismatch_percent is not None and
        metrics["mismatch_percent_over_16"] > args.max_mismatch_percent
    ):
        print(
            "mismatch_percent_over_16 "
            f"{metrics['mismatch_percent_over_16']} exceeded threshold {args.max_mismatch_percent}"
        )
        failed = True
    return 1 if failed else 0


if __name__ == "__main__":
    raise SystemExit(main())
