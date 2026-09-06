# TH-1226 클릭 검증 및 수정 — 2026-09-06

아이콘의 후속 앱 보정은 사용자 요청에 따라 [07-icon-url-compatibility-2026-09-06.md](07-icon-url-compatibility-2026-09-06.md)에서 완료했다. 아래 403 관련 내용은 그 보정 전의 조사 결과다.

## 결과

store `120024`의 현재 서버 응답에 있는 탭, 화면 진입, 이미지 확대, 공유 선택, 주소 복사, 신고 사유창과 CTA를 emulator에서 클릭했다. 관찰한 오류와 코드에서 재현한 요청·lifecycle 오류를 수정했으며, 최종 app 67 tests와 data screen 21 tests가 통과했다. Debug APK를 기존 로그인 데이터를 보존하며 업데이트했다.

실제 좋아요 취소·재추가는 서버 상태를 변경하므로 사전 확인을 요청한 상태다. 자동 테스트에서는 현재/과거 취소 payload의 repository 요청값까지 검증했다. 신고·리뷰·방문·사진의 최종 제출, 공유 전송은 실행하지 않았다.

## 환경

- `emulator-5554`, 1080×2400, density 420.
- `com.zion830.threedollars.dev`, `4.22.0 (127)`.
- 기준 commit `691389fe`, 이번 수정은 working tree에 있다. push/PR/배포 없음.
- 원래 Home Expanded에서 시작했다. 기존 리뷰 작성 경로가 별도 V2 Activity를 열어, 수정 전 조사에는 full-screen 컨테이너 검증도 포함됐다.
- 최초 시각 조사: [05-visual-review-2026-09-06.md](05-visual-review-2026-09-06.md).
- 모든 실제 클릭은 직전에 수집한 UI tree의 text 또는 bounds를 사용했다.
- 캡처와 UI tree: `build/harness/th-1226-click-20260906/`. 로그 전체 대신 테스트 결과와 필요한 화면 증거만 기록한다.

## 클릭 검증

| 항목 | 실제 관찰 | 범위/수정 결과 |
| --- | --- | --- |
| 기여자 | `StoreContributorActivity` 진입 | 수정 전 back 후 Preview로 접힘. 수정 후 MainActivity Expanded 유지 |
| 상단 리뷰 작성 | `AddReviewDialog` 표시 | 수정 전 V2 Activity 중첩. 수정 후 MainActivity에서 직접 표시하고 취소 시 Expanded 유지 |
| 방문 인증 | `StoreCertificationActivity` 진입 | 서버 제출 없이 확인. 수정 전 `#계란빵, 와플 #5.0 (2)`, 수정 후 `#계란빵, 와플` |
| 공유 | 카카오톡 공유 대상 선택창 | 수신자 선택·전송 없이 취소하고 앱 복귀 |
| 길안내 | 카카오 지도/네이버 지도 선택창 | 목적지 선택창 진입까지만 확인 |
| 홈 탭 | PREVIEW 상단으로 이동 | 정상. 소비된 탭 clickLog 누락은 수정 및 회귀 테스트 |
| 가게 정보 탭 | INFO_V1 시작 위치로 이동 | 정상 |
| 가게 사진 탭 | IMAGE 시작 위치로 이동 | 정상 |
| 리뷰 탭 | REVIEW가 보이는 하단 위치로 이동 | 정상. scroll 최대값 때문에 section이 화면 최상단에 붙지 않을 수 있음 |
| 주소 복사 | system clipboard overlay와 복사 Toast에 정확한 주소 | 정상 |
| 지도 확대 | `FullScreenMapActivity` 진입/복귀 | 정상. 확대 버튼 image는 HTTP 403으로 비어 있음 |
| 정보 수정 | `EditStoreFragment`의 수정 항목 선택창 | 저장 없이 닫아 원래 위치로 복귀 |
| 없는 장소 제보 | 삭제 요청 사유 dialog | 사유 확정/신고 전송 없이 취소 |
| 사진 제보 | `MoreImageActivity` 진입 | 선택·업로드 없이 복귀. 수정 후 Expanded와 사진 section의 좌표가 그대로 유지됨 |
| 사진 531/530 | 각각 해당 이미지가 `StorePhotoDialog`에서 확대 | 두 장 모두 확인. 삭제는 실행하지 않음 |
| 하단 리뷰 쓰기 | `AddReviewDialog` 표시 | 제출 없이 취소 |
| 리뷰 신고 | 서버 사유 목록과 기타 입력란 | 사유 미선택/기타 입력 공란일 때 확인 버튼 disabled. 신고 확정하지 않음 |
| 리뷰 좋아요 취소 | 현재 ADD/CANCEL 모두 `STICKER_ID=LIKE` | 취소 요청을 `[]`로 분리. 실서버 왕복은 승인 대기 |
| 사장님 앱 CTA | 응답의 Notion URL 및 소개 페이지 로드 | 정상. 브라우저 알림 안내는 ‘나중에’로 건너뛰고 앱 복귀 |

대표 증거:

- [수정 전 기여자 복귀](../../../build/harness/th-1226-click-20260906/02-contributors-return.png), [수정 후 복귀](../../../build/harness/th-1226-click-20260906/58-updated-contributors-return.png).
- [수정 후 같은 Activity의 리뷰 작성창](../../../build/harness/th-1226-click-20260906/59-updated-review-write.png).
- [방문 카테고리 교정](../../../build/harness/th-1226-click-20260906/61-updated-visit.png).
- [주소 복사](../../../build/harness/th-1226-click-20260906/43-copy-address.png), [지도 확대](../../../build/harness/th-1226-click-20260906/44-enlarge-map.png).
- [첫 사진 확대](../../../build/harness/th-1226-click-20260906/20-photo-enlarge-first.png), [두 번째 사진 확대](../../../build/harness/th-1226-click-20260906/22-photo-enlarge-second.png).
- [신고 기타 필수 입력](../../../build/harness/th-1226-click-20260906/28-review-report-other.png), [CTA 웹페이지](../../../build/harness/th-1226-click-20260906/35-cta-loaded.png).

## 수정 사항

1. `StoreDetailV2ViewModel`: POST/REVIEW의 ADD와 CANCEL을 구분한다. 취소는 `STICKER_ID`가 `LIKE`, 빈 문자열, 누락 중 어느 형태든 빈 stickers 목록을 보낸다.
2. `StoreDetailV2Content`: 동일 페이지 탭 이동 시 outer 또는 button clickLog를 한 번 기록한다. target이 없으면 기존 onAction 경로가 로그와 라우팅을 담당한다.
3. Home 선택: `selectStore`가 같은 store ID의 lifecycle replay를 생략해 Expanded와 스크롤을 유지한다. null 선택에서는 load 취소·현재 ID·표시 상태·favorite override를 정리하고, 같은 가게 재선택 시 다시 load한다.
4. `HomeFragment`: `RESULT_CANCELED`에는 refresh하지 않는다. USER 리뷰 작성은 기존 `AddReviewDialog`를 MainActivity 위에 직접 표시하며 성공/실패 이벤트를 기존 legacy ViewModel에서 수집한다.
5. 방문 인증: 공용 category 변환에서 primary metadata의 category chip만 사용한다. rating chip을 메뉴 카테고리로 전달하지 않는다.
6. IMAGE: 서버 width/height를 사용하고 title/subTitle이 없는 카드에 고정 높이를 남기지 않는다. 현재 카드 bounds가 `205×205px`로, density 2.625에서 서버 `78×78dp`와 일치한다.
7. 별점: 투명 이미지에 강제로 추가하던 Gray10을 제거하고 `stars.style`을 Row에 적용한다.
8. 비동기 경계: mutation·삭제 요청·리뷰 신고 사유 조회의 응답/예외는 요청한 가게가 여전히 선택된 경우에만 refresh·favorite 변경·dialog/close/message를 처리한다. 이전 가게의 성공으로 발생한 서버 변경 사실은 `hasUpdates`에 남긴다.

[수정 후 사진·별점](../../../build/harness/th-1226-click-20260906/63-updated-tab-images.png)과 [사진 제보에서 돌아온 화면](../../../build/harness/th-1226-click-20260906/65-updated-photo-return.png)의 가게 사진 제목 bounds는 `[53,221][214,284]`, 리뷰 제목 bounds는 `[53,690][191,753]`로 동일했다.

## 자동 검증

```bash
./gradlew :app:testDebugUnitTest --tests 'com.zion830.threedollars.ui.storeDetail.v2.*' --tests 'com.zion830.threedollars.ui.home.*' :data:testDebugUnitTest --tests 'com.threedollar.data.screen.*' :app:assembleDebug
./gradlew :app:testDebugUnitTest --tests 'com.zion830.threedollars.ui.storeDetail.v2.*' --tests 'com.zion830.threedollars.ui.home.*' :app:assembleDebug
git diff --check
```

- 첫 수정 후 app 65/data 21 tests 통과. 비동기 guard 추가 후 관련 app 전체를 다시 실행해 최종 67 tests 통과. data 변경은 없어 21건 결과를 유지한다.
- 최종 app 67 / data 21: failures 0, errors 0, skipped 0.
- Debug assemble 성공. dependency/toolchain 변경 없음.
- 기존 구현을 추출한 상태에서 6개 assertion 실패를 확인: 취소 body, TAB outer/button 로그, lifecycle replay, category 오염.
- 추가로 3개 assertion 실패를 확인: deselect 후 stale Content, A의 늦은 성공 때문에 B load가 2→3회로 증가, A의 `not_exists_store`가 B CloseContainer를 발생시킴.
- [첫 RED 증거](../../../build/harness/th-1226-click-20260906/tdd-red-failures.json), [비동기 RED 증거](../../../build/harness/th-1226-click-20260906/tdd-selection-red-failures.json), [최종 테스트 집계](../../../build/harness/th-1226-click-20260906/test-summary.json).
- 별도 읽기 전용 코드 리뷰에서 확인된 비동기 결함을 보강했고, 보강 diff 재검토에서 추가 실질 회귀는 발견되지 않았다.
- 최종 APK에서 사진 탭을 한 번 클릭한 뒤 `LogManager`에 `screen=store_detail`, `objectId=tab`, `value=IMAGE`가 1건 기록된 것을 확인했다. [실제 탭 로그](../../../build/harness/th-1226-click-20260906/final-tab-click-log.json).
- 최종 프로세스 PID `31218`의 crash buffer는 0 byte였다. [crash 로그](../../../build/harness/th-1226-click-20260906/final-crash.log).

## 남은 제한

- `copy.png`, `zoom_3x.png`, `deletion.png`, `heart_fill.png`, `heart_line.png`의 HTTP 403은 서버 asset 문제로 남아 있다. strict 표시 정책에 따라 로컬 icon으로 대체하지 않았다.
- `null님…`, 빈 menu item, 공백 없는 span 연결 문구는 서버 원문이다.
- 실서버 좋아요 왕복, 최종 리뷰/방문/신고/사진 제출, 공유 전송, 외부 지도 앱 경로 실행은 완료 판정에 포함하지 않는다. 실제 전송 여부는 사용자 승인 범위에 따른다.
- 광고 클릭은 검증하지 않았다. 화면 이동 중 광고 로드/실패 상태가 존재하므로 이번 수정에서 광고 fill을 보장하지 않는다.
- 현재 payload의 10개 section을 검증했다. 여기 없는 쿠폰·post·BOSS 전용 UI의 실서버 QA는 포함하지 않았다. POST 취소 요청은 unit test로 검증했다.

## 아이콘 누락 원인 추가 확인

사용자가 아이콘 누락을 다시 문의해 현재 화면과 원본 URL을 재확인했다. 403이라는 현상에서 더 나아가, 같은 파일의 정상 접근 경로를 확인했다.

기준 host는 `https://storage.threedollars.co.kr/`이다.

| 응답의 경로 | GET 결과 | 같은 파일의 정상 경로 | GET 결과 |
| --- | --- | --- | --- |
| `copy.png` | 403 `AccessDenied` | `app/copy.png` | 200 image/png, 48×48 |
| `zoom_3x.png` | 403 `AccessDenied` | `app/zoom_3x.png` | 200 image/png, 60×60 |
| `deletion.png` | 403 `AccessDenied` | `app/deletion.png` | 200 image/png, 60×60 |
| `Edit_fill.png` | 403 `AccessDenied` | `app/Edit_fill.png` | 200 image/png, 60×60 |
| `heart_fill.png` | 403 `AccessDenied` | `app/heart_fill.png` | 200 image/png, 48×48 |
| `heart_line.png` | 403 `AccessDenied` | `app/heart_line.png` | 200 image/png, 48×48 |

정상 표시되는 `app/share_4x.png`, `app/star_solid_bold.png`도 200이다. 정상 경로에서 다운로드한 6개 PNG를 열어 복사·확대·삭제 제보·수정·채움/빈 하트 이미지임을 확인했다. 실패 URL과 정상 URL의 차이는 `/app/` prefix이며, 서버 응답의 해당 image URL 생성 경로를 교정하는 것이 직접 해결책이다. 저장소 객체 자체의 존재/ACL 상태는 공개 403만으로 구분하지 않는다.

`SDImageRenderer`는 `AsyncImage(model = image.url)`로 전달된 URL을 그대로 로드한다. error placeholder나 로컬 fallback이 없으므로 실패한 이미지의 공간만 남는다. 이는 앞서 승인된 strict server-driven 정책과 일치한다. 이번 추가 조사에서는 앱 코드를 변경하지 않았다.

증거: [현재 화면](../../../build/harness/th-1226-click-20260906/70-icons-current.png), [14개 URL 대조 결과](../../../build/harness/th-1226-click-20260906/icon-url-diagnosis.json).
