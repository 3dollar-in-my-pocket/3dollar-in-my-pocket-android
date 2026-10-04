"""내부 테스트 트랙의 릴리즈를 같은 빌드(같은 versionCode) 그대로 프로덕션 초안으로 옮긴다.

새로 빌드·업로드하지 않으므로 테스터가 확인한 AAB 가 그대로 심사에 들어간다. 심사 제출은 콘솔에서 한다.

환경 변수
- GOOGLE_PLAY_SERVICE_ACCOUNT_JSON: 서비스 계정 키(JSON 문자열)
- PACKAGE_NAME: 앱 패키지명
- VERSION_CODE: 옮길 versionCode. 비우면 내부 테스트의 배포 완료(completed) 릴리즈 중 가장 높은 것
- DRY_RUN: "true" 면 옮길 릴리즈와 현재 트랙 상태만 출력하고 아무것도 바꾸지 않는다
"""

import json
import os
import sys

from google.oauth2 import service_account
from googleapiclient.discovery import build
from googleapiclient.errors import HttpError

SCOPES = ["https://www.googleapis.com/auth/androidpublisher"]
SOURCE_TRACK = "internal"
TARGET_TRACK = "production"


def describe(track):
    releases = track.get("releases", [])
    if not releases:
        return "  (릴리즈 없음)"
    return "\n".join(
        f"  - {r.get('name', '?')} / versionCodes={r.get('versionCodes', [])} / status={r.get('status')}"
        for r in releases
    )


def pick_release(track, version_code):
    releases = [r for r in track.get("releases", []) if r.get("versionCodes")]
    if version_code:
        matched = [r for r in releases if version_code in r["versionCodes"]]
        if not matched:
            sys.exit(f"내부 테스트 트랙에 versionCode {version_code} 릴리즈가 없다.")
        return matched[0]
    completed = [r for r in releases if r.get("status") == "completed"]
    if not completed:
        sys.exit("내부 테스트 트랙에 배포 완료(completed) 릴리즈가 없다.")
    return max(completed, key=lambda r: max(int(code) for code in r["versionCodes"]))


def promote(edits, package_name, version_code, dry_run, changes_not_sent_for_review):
    edit_id = edits.insert(packageName=package_name, body={}).execute()["id"]
    try:
        source = edits.tracks().get(packageName=package_name, editId=edit_id, track=SOURCE_TRACK).execute()
        target = edits.tracks().get(packageName=package_name, editId=edit_id, track=TARGET_TRACK).execute()
        release = pick_release(source, version_code)

        print(f"[{SOURCE_TRACK}]\n{describe(source)}")
        print(f"[{TARGET_TRACK}] (현재)\n{describe(target)}")
        print(f"옮길 릴리즈: {release.get('name')} / versionCodes={release['versionCodes']}")

        if dry_run:
            print("DRY_RUN: 변경하지 않고 종료한다.")
            edits.delete(packageName=package_name, editId=edit_id).execute()
            return None

        draft = {
            "name": release.get("name"),
            "versionCodes": release["versionCodes"],
            "status": "draft",
        }
        if release.get("releaseNotes"):
            draft["releaseNotes"] = release["releaseNotes"]

        edits.tracks().update(
            packageName=package_name,
            editId=edit_id,
            track=TARGET_TRACK,
            body={"track": TARGET_TRACK, "releases": [draft]},
        ).execute()
        edits.commit(
            packageName=package_name,
            editId=edit_id,
            changesNotSentForReview=changes_not_sent_for_review,
        ).execute()
        return release
    except (HttpError, SystemExit):
        try:
            edits.delete(packageName=package_name, editId=edit_id).execute()
        except HttpError:
            pass
        raise


def main():
    credentials = service_account.Credentials.from_service_account_info(
        json.loads(os.environ["GOOGLE_PLAY_SERVICE_ACCOUNT_JSON"]), scopes=SCOPES
    )
    edits = build("androidpublisher", "v3", credentials=credentials, cache_discovery=False).edits()
    package_name = os.environ["PACKAGE_NAME"]
    version_code = os.environ.get("VERSION_CODE", "").strip()
    dry_run = os.environ.get("DRY_RUN", "false").lower() == "true"

    # Play 는 앱 상태에 따라 changesNotSentForReview 를 요구하거나 거부한다.
    # 옵션 없이 먼저 커밋하고, 실패하면 옵션을 켜서 새 edit 으로 한 번 더 시도한다 (업로드 워크플로와 같은 규칙).
    try:
        release = promote(edits, package_name, version_code, dry_run, changes_not_sent_for_review=False)
    except HttpError as error:
        print(f"첫 커밋 실패, changesNotSentForReview 로 다시 시도한다: {error}")
        release = promote(edits, package_name, version_code, dry_run, changes_not_sent_for_review=True)

    if release is None:
        return
    summary = (
        f"### 프로덕션 초안 등록 완료\n"
        f"- 릴리즈: {release.get('name')}\n"
        f"- versionCodes: {', '.join(release['versionCodes'])}\n"
        f"- 심사 제출은 Play Console 에서 진행한다.\n"
    )
    print(summary)
    summary_path = os.environ.get("GITHUB_STEP_SUMMARY")
    if summary_path:
        with open(summary_path, "a", encoding="utf-8") as file:
            file.write(summary)


if __name__ == "__main__":
    main()
