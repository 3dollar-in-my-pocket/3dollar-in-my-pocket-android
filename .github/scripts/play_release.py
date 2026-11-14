"""release AAB 를 내부 테스트·프로덕션 트랙에 올리고 프로덕션 심사 제출까지 한 번의 edit 으로 커밋한다.

관리형 게시가 꺼져 있으면 심사 통과 즉시 출시된다.

환경 변수
- GOOGLE_PLAY_SERVICE_ACCOUNT_JSON: 서비스 계정 키(JSON 문자열)
- PACKAGE_NAME: 앱 패키지명
- AAB_PATH: 올릴 AAB 경로
- RELEASE_NAME: 릴리즈 이름 (예: v4.37.0)
- WHATS_NEW_PATH: "새로운 기능" 문구 파일 (ko-KR, 500자 이하)
- DRY_RUN: "true" 면 업로드·트랙 지정까지 하고 커밋 대신 validate 후 edit 을 버린다

출력 (GITHUB_OUTPUT)
- version_code
- submitted: true = 심사 제출됨, false = 변경사항이 저장만 되고 콘솔에서 "검토를 위해 보내기"가 필요함
"""

import json
import os
import sys

from google.oauth2 import service_account
from googleapiclient.discovery import build
from googleapiclient.errors import HttpError
from googleapiclient.http import MediaFileUpload

SCOPES = ["https://www.googleapis.com/auth/androidpublisher"]
TRACKS = ["internal", "production"]
LANGUAGE = "ko-KR"
NOTES_LIMIT = 500


def read_notes(path):
    with open(path, encoding="utf-8") as file:
        text = file.read().strip()
    if not text:
        sys.exit("새로운 기능 문구가 비어 있다.")
    if len(text) > NOTES_LIMIT:
        sys.exit(f"새로운 기능 문구가 {len(text)}자다. Play 제한은 {NOTES_LIMIT}자.")
    return text


def run_edit(edits, package_name, aab_path, release_name, notes, dry_run, changes_not_sent_for_review):
    edit_id = edits.insert(packageName=package_name, body={}).execute()["id"]
    try:
        media = MediaFileUpload(aab_path, mimetype="application/octet-stream", resumable=True)
        bundle = edits.bundles().upload(packageName=package_name, editId=edit_id, media_body=media).execute()
        version_code = str(bundle["versionCode"])
        print(f"업로드: versionCode {version_code}")

        release = {
            "name": release_name,
            "versionCodes": [version_code],
            "status": "completed",
            "releaseNotes": [{"language": LANGUAGE, "text": notes}],
        }
        for track in TRACKS:
            edits.tracks().update(
                packageName=package_name,
                editId=edit_id,
                track=track,
                body={"track": track, "releases": [release]},
            ).execute()
            print(f"트랙 지정: {track}")

        if dry_run:
            edits.validate(packageName=package_name, editId=edit_id).execute()
            print("DRY_RUN: validate 통과, 커밋하지 않고 edit 을 버린다.")
            edits.delete(packageName=package_name, editId=edit_id).execute()
            return version_code

        edits.commit(
            packageName=package_name,
            editId=edit_id,
            changesNotSentForReview=changes_not_sent_for_review,
        ).execute()
        return version_code
    except (HttpError, SystemExit):
        try:
            edits.delete(packageName=package_name, editId=edit_id).execute()
        except HttpError:
            pass
        raise


def write_outputs(**values):
    path = os.environ.get("GITHUB_OUTPUT")
    if not path:
        return
    with open(path, "a", encoding="utf-8") as file:
        for key, value in values.items():
            file.write(f"{key}={value}\n")


def main():
    credentials = service_account.Credentials.from_service_account_info(
        json.loads(os.environ["GOOGLE_PLAY_SERVICE_ACCOUNT_JSON"]), scopes=SCOPES
    )
    edits = build("androidpublisher", "v3", credentials=credentials, cache_discovery=False).edits()
    package_name = os.environ["PACKAGE_NAME"]
    aab_path = os.environ["AAB_PATH"]
    release_name = os.environ["RELEASE_NAME"]
    notes = read_notes(os.environ["WHATS_NEW_PATH"])
    dry_run = os.environ.get("DRY_RUN", "false").lower() == "true"

    # 심사 거절 등으로 자동 심사 제출이 막힌 앱은 changesNotSentForReview 를 요구한다.
    # 그때는 저장만 하고 submitted=false 로 알린다. 커밋이 실패한 edit 의 업로드는 반영되지 않아 재시도해도 중복되지 않는다.
    submitted = True
    try:
        version_code = run_edit(edits, package_name, aab_path, release_name, notes, dry_run, False)
    except HttpError as error:
        if "changesNotSentForReview" not in str(error):
            raise
        print(f"자동 심사 제출이 막혀 저장만 한다: {error}")
        submitted = False
        version_code = run_edit(edits, package_name, aab_path, release_name, notes, dry_run, True)

    write_outputs(version_code=version_code, submitted=str(submitted and not dry_run).lower())
    state = "DRY_RUN" if dry_run else ("심사 제출" if submitted else "저장만 됨 — 콘솔에서 검토를 위해 보내기 필요")
    summary = (
        f"### {release_name} ({version_code}) — {state}\n"
        f"- 트랙: {', '.join(TRACKS)}\n"
        f"- 새로운 기능:\n\n```\n{notes}\n```\n"
    )
    print(summary)
    summary_path = os.environ.get("GITHUB_STEP_SUMMARY")
    if summary_path:
        with open(summary_path, "a", encoding="utf-8") as file:
            file.write(summary)


if __name__ == "__main__":
    main()
