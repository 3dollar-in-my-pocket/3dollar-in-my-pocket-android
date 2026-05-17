# 홈 초기 진입 시 현재 위치 미반영 조사 문서

작성일: 2026-05-14

## 목적

앱 첫 진입 시 홈 지도와 주변 가게 목록이 간헐적으로 사용자의 실제 현재 위치가 아니라 기본 위치인 `37.56, 126.97` 기준으로 뜨는 원인을 조사한다.

사용자 관점의 증상은 다음과 같다.

- 앱을 처음 열면 홈이 삼각지역 근처로 보인다.
- 위치 권한을 허용했거나 이전에 허용한 상태에서도 간혹 내 위치로 이동하지 않는다.
- 시간이 조금 지나면 위치가 잡히거나, 내 위치 버튼을 누르면 정상 동작하는 것처럼 보일 수 있다.

이 문서는 수정안 확정 전의 원인 분석 문서다. 현재 조사 단계에서는 코드 변경을 하지 않았다.

## 결론 요약

가장 가능성이 높은 원인은 홈 초기화 순서의 비동기 타이밍 문제다.

`HomeFragment`는 현재 위치 이동을 요청한 직후 위치 콜백을 기다리지 않고 `fetchAroundStores()`를 호출한다. 반면 실제 위치는 `FusedLocationProviderClient.lastLocation.addOnSuccessListener` 안에서 나중에 들어온다. 이 사이에 `HomeViewModel.uiState`가 아직 기본 위치를 들고 있으면 주변 가게 조회가 기본 위치 기준으로 먼저 실행된다.

두 번째 원인은 위치 획득 방식이 `lastLocation` 캐시에만 의존한다는 점이다. `lastLocation`은 빠르지만 캐시가 없으면 `null`을 반환할 수 있다. 현재 코드는 `lastLocation == null`일 때 `getCurrentLocation()` 또는 1회 location update fallback을 수행하지 않는다.

세 번째로, 광고 조회와 주소 표시도 기본 위치가 먼저 노출될 수 있는 구조다. 지도 준비 전 `getMapCenterLatLng()`가 호출되면 기본 위치를 반환하고, 주소 표시용 `currentLocation` flow도 기본 위치로 시작한다.

## 관련 코드 위치

### 기본 위치 정의

- `app/src/main/java/com/zion830/threedollars/utils/NaverMapUtils.kt`
  - `DEFAULT_LOCATION = LatLng(37.56, 126.97)`
  - `DEFAULT_DISTANCE_M = 100000.0`
- `app/src/main/java/com/zion830/threedollars/ui/home/data/HomeUIState.kt`
  - `mapPosition` 기본값: `DEFAULT_LOCATION`
  - `userLocation` 기본값: `DEFAULT_LOCATION`

현재 기본 위치는 코드 주석상 "서울"로 되어 있지만, 실제 사용자에게는 삼각지역 근처처럼 보일 수 있는 좌표다.

### 홈 초기 진입 흐름

- `app/src/main/res/navigation/mobile_navigation.xml`
  - `startDestination="@+id/navigation_home"`
  - 홈 첫 화면은 `HomeFragment`
- `app/src/main/java/com/zion830/threedollars/ui/home/ui/HomeFragment.kt`
  - `initView()`에서 `initMap()`, `initViewModel()`, `initFlow()` 등이 순서대로 호출된다.
  - `initViewModel()`에서 `getAdvertisement(latLng = naverMapFragment.getMapCenterLatLng())` 호출
  - `initMap()`에서 500ms 지연 후 `checkAndRequestLocationPermission()` 호출
  - 권한이 있으면 `moveToCurrentLocation(false)` 직후 `viewModel.fetchAroundStores()` 호출
- `app/src/main/java/com/zion830/threedollars/ui/map/ui/NearStoreNaverMapFragment.kt`
  - 지도 준비 시 저장된 지도 위치가 있으면 해당 위치로 이동
  - 저장된 위치가 없고 위치 권한이 있으면 `moveToCurrentLocation()` 호출
- `app/src/main/java/com/zion830/threedollars/ui/map/ui/NaverMapFragment.kt`
  - `moveToCurrentLocation()`에서 `fusedLocationProviderClient.lastLocation` 사용
  - 위치가 `null`이면 별도 처리 없이 종료
- `app/src/main/java/com/zion830/threedollars/ui/home/viewModel/HomeViewModel.kt`
  - `fetchAroundStores()`는 `uiState.value`를 즉시 읽어서 API 파라미터로 사용
  - `mapLatitude/mapLongitude`는 `state.mapPosition`
  - `deviceLatitude/deviceLongitude`는 `state.userLocation`

## 현재 초기화 시퀀스

아래는 권한이 이미 허용되어 있는 일반적인 홈 첫 진입 경로다.

```text
SplashActivity
  -> MainActivity
    -> Navigation startDestination: HomeFragment
      -> HomeFragment.initView()
        -> initMap()
          -> NearStoreNaverMapFragment 생성
          -> childFragmentManager.replace(...).commit()
          -> lifecycleScope.launch { delay(500); checkAndRequestLocationPermission() }
          -> currentPosition LiveData observe 등록
          -> mapPosition LiveData observe 등록
          -> mapViewPortDistance LiveData observe 등록
        -> initAdapter()
        -> initFilterComposeView()
        -> initViewModel()
          -> getUserInfo()
          -> getAdvertisement(naverMapFragment.getMapCenterLatLng())
        -> initFlow()
```

여기서 첫 번째 주의점은 `initViewModel()`의 광고 요청이다. 이 시점에 Naver Map이 아직 준비되지 않았으면 `getMapCenterLatLng()`는 `NaverMapUtils.DEFAULT_LOCATION`을 반환한다.

두 번째 주의점은 `initMap()` 안의 500ms 지연이다. 500ms 뒤 아래 흐름이 실행된다.

```text
checkAndRequestLocationPermission()
  -> isLocationAvailable() == true
    -> naverMapFragment.enableLocationTracking()
    -> naverMapFragment.moveToCurrentLocation(false)
       -> fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            currentPosition.value = LatLng(location.latitude, location.longitude)
            moveCamera(position)
            onMyLocationLoaded(position)
          }
    -> viewModel.fetchAroundStores()
       -> val state = uiState.value
       -> API request with state.mapPosition and state.userLocation
```

문제는 `moveToCurrentLocation(false)`가 위치 획득 완료를 기다려주는 함수가 아니라는 점이다. 이 함수는 비동기 위치 요청을 시작하고 바로 반환한다. 따라서 `fetchAroundStores()`가 먼저 실행될 수 있다.

## 데이터 흐름 상세

### 지도 위치

`NaverMapFragment`는 카메라가 움직일 때마다 `mapPosition` LiveData를 갱신한다.

```text
NaverMap.addOnCameraChangeListener
  -> mapPosition.value = map.cameraPosition.target
  -> HomeFragment observer
  -> viewModel.updateMapPosition(it)
  -> uiState.mapPosition 업데이트
```

현재 위치 이동이 성공해서 카메라가 이동하면 최종적으로 `uiState.mapPosition`도 갱신된다. 하지만 이 갱신은 카메라 이동 이벤트 이후에 발생하므로, 그 전에 `fetchAroundStores()`가 실행되면 이전 값 또는 기본값을 쓸 수 있다.

### 사용자 실제 위치

`NaverMapFragment.moveToCurrentLocation()`은 `currentPosition` LiveData를 갱신한다.

```text
lastLocation success
  -> currentPosition.value = LatLng(...)
  -> HomeFragment observer
  -> viewModel.updateCurrentLocation(it)
  -> uiState.userLocation 업데이트
  -> NearStoreNaverMapFragment.onMyLocationLoaded(position)
  -> viewModel.updateUserLocation(position)
  -> uiState.userLocation 업데이트
```

`updateCurrentLocation()`과 `updateUserLocation()`은 둘 다 `uiState.userLocation`만 갱신한다. 이름은 다르지만 현재 구현상 같은 역할이다.

### 주소 표시 위치

`HomeFragment.initFlow()`는 `viewModel.currentLocation`을 수집해서 주소 텍스트를 갱신한다.

```text
viewModel.currentLocation.collect {
  binding.tvAddress.text = getCurrentLocationName(it)
}
```

하지만 `HomeViewModel._currentLocation`은 다음 두 상황에서만 갱신된다.

- 초기값: `NaverMapUtils.DEFAULT_LOCATION`
- `fetchAroundStores()` 성공 후: `_currentLocation.emit(state.mapPosition)`

즉 실제 GPS 위치가 들어왔을 때 `currentLocation` flow가 직접 갱신되는 구조가 아니다. `fetchAroundStores()`가 기본 위치 상태로 먼저 성공하면 주소도 기본 위치 기준으로 표시될 수 있다.

## 주요 원인 후보

### 1. 위치 콜백보다 주변 가게 조회가 먼저 실행됨

위험도: 높음

권한이 있는 경우 `checkAndRequestLocationPermission()`은 `moveToCurrentLocation(false)` 다음 줄에서 바로 `viewModel.fetchAroundStores()`를 호출한다. 이때 `moveToCurrentLocation()` 내부 위치 요청은 비동기다.

가능한 실제 순서:

```text
T0: HomeFragment 진입
T1: uiState.mapPosition = DEFAULT_LOCATION
T2: uiState.userLocation = DEFAULT_LOCATION
T3: moveToCurrentLocation(false) 호출
T4: lastLocation 아직 응답 없음
T5: fetchAroundStores() 실행
T6: API request: mapLatitude=37.56, mapLongitude=126.97, deviceLatitude=37.56, deviceLongitude=126.97
T7: lastLocation success
T8: 지도는 내 위치로 움직이거나 currentPosition 갱신
T9: 이미 기본 위치 기준 주변 가게 목록/마커가 렌더링됨
```

이 경우 사용자에게는 "처음 들어왔는데 기본 위치가 떴다"로 보인다.

### 2. `lastLocation` 캐시가 비어 있으면 아무 일도 하지 않음

위험도: 높음

현재 위치 획득은 대부분 `fusedLocationProviderClient.lastLocation`만 사용한다. 홈 지도 경로도, 스플래시 광고 경로도, 메인 팝업 경로도 같은 패턴이다.

Android 공식 문서에 따르면 `getLastLocation()`은 다음 상황에서 `null`이 될 수 있다.

- 기기 설정에서 위치가 꺼져 있음
- 기기가 아직 위치를 기록한 적이 없음
- Google Play services가 재시작되었고 이후 활성 위치 요청이 없었음

현재 `moveToCurrentLocation()`은 `lastLocation` success callback에서 `it != null`일 때만 처리하고, `it == null`이면 fallback 없이 종료한다.

결과:

- 카메라 이동 없음
- `currentPosition` 갱신 없음
- `uiState.userLocation` 갱신 없음
- 홈 목록 조회가 이미 실행되었다면 기본 위치 기준 유지

참고 문서:

- Android Developers: https://developer.android.com/develop/sensors-and-location/location/retrieve-current
- FusedLocationProviderClient: https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderClient

### 3. 지도 준비 전 광고 요청이 기본 위치를 사용함

위험도: 중간

`HomeFragment.initViewModel()`은 `viewModel.getAdvertisement(latLng = naverMapFragment.getMapCenterLatLng())`를 호출한다. `getMapCenterLatLng()`는 `naverMap`이 아직 `null`이면 `NaverMapUtils.DEFAULT_LOCATION`을 반환한다.

따라서 홈 진입 직후 광고 요청은 실제 위치가 아니라 기본 위치 기준으로 나갈 수 있다.

주변 가게 조회와 별개로, 홈 카드 광고나 리스트 광고의 위치 파라미터가 실제 사용자 위치와 어긋날 수 있다.

### 4. `currentLocation` flow의 의미가 실제 현재 위치와 다름

위험도: 중간

`HomeViewModel.currentLocation`이라는 이름은 실제 사용자 위치처럼 보이지만, 현재 구현은 `fetchAroundStores()` 성공 시점의 `state.mapPosition`을 emit한다.

즉 이 flow는 "사용자 GPS 위치"가 아니라 "마지막 주변 가게 조회에 사용한 지도 중심 좌표"에 가깝다.

초기값도 기본 위치이므로, 화면이 시작되면 주소 텍스트가 기본 위치 주소로 먼저 표시될 수 있다. 이후 실제 위치가 잡혀도 `currentLocation`이 즉시 갱신되지 않으면 주소 표시가 늦거나 어긋날 수 있다.

### 5. Android 12 이상 대략적인 위치 권한 처리 부족

위험도: 중간

Manifest에는 `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`이 둘 다 선언되어 있다. 하지만 `isLocationAvailable()`은 `ACCESS_FINE_LOCATION`만 확인한다.

Android 12 이상에서 사용자가 정확한 위치 대신 대략적인 위치만 허용하면, 앱 입장에서는 coarse 위치 권한은 있지만 fine 위치 권한은 없을 수 있다. 이 경우 현재 `isLocationAvailable()`은 false를 반환한다.

가능한 결과:

- 권한이 있는데도 권한 없음 흐름으로 진입
- 위치 추적 비활성화
- 기본 위치 사용 또는 권한 다이얼로그 표시

서비스 정책상 정확한 위치가 반드시 필요하다면 fine만 보는 것이 의도일 수 있다. 다만 "내 위치를 가져오지 못함" 이슈 관점에서는 coarse-only 상태를 명시적으로 다루는지 확인해야 한다.

### 6. GPS 설정이 꺼진 경우 fallback UX가 없음

위험도: 중간

`moveToCurrentLocation()`은 `isLocationAvailable() && isGpsAvailable()`일 때만 `lastLocation`을 요청한다. GPS가 꺼져 있으면 아무 처리 없이 종료한다.

이때 사용자는 권한은 허용했지만 기기 위치 서비스가 꺼진 상태일 수 있다. 현재 홈 초기 진입 경로에는 GPS 설정을 켜도록 안내하거나 기본 위치 사용을 명시하는 UX가 명확하지 않다.

## 저장된 지도 위치와의 관계

`NearStoreNaverMapFragment.onMapReady()`는 `viewModel.getSavedMapPosition()`을 우선 확인한다.

```text
if (savedPosition != null) {
  moveCamera(savedPosition)
} else {
  if (isLocationAvailable()) {
    moveToCurrentLocation()
  }
}
```

이 로직은 화면 재생성이나 설정 화면 복귀 시 이전 지도 위치를 복원하기 위한 의도로 보인다. 다만 첫 진입 문제와 결합하면 다음을 확인해야 한다.

- `SavedStateHandle`이 정말 첫 앱 진입에서도 비어 있는가
- 이전 세션의 기본 위치가 저장되어 재사용되는 케이스가 없는가
- `mapPosition` 저장이 현재 위치 성공 전 기본 위치 카메라 이벤트로 먼저 발생하는가

현재 코드만 보면 `SavedStateHandle`은 같은 `HomeViewModel` 생명주기 안에서 유지되는 값이므로 앱 완전 재시작까지 영속되지는 않는다. 하지만 홈 탭 재진입, 설정 복귀, 프로세스 복원 시에는 위치 복원 로직이 실제 현재 위치 이동보다 우선할 수 있다.

## API 파라미터 영향

`HomeViewModel.fetchAroundStores()`는 다음 값을 사용한다.

```text
distanceM = state.currentDistanceM
mapLatitude = state.mapPosition.latitude
mapLongitude = state.mapPosition.longitude
deviceLatitude = state.userLocation.latitude
deviceLongitude = state.userLocation.longitude
```

초기 상태에서 위치가 아직 반영되지 않으면 네 값이 모두 기본값 또는 기본 위치 기반 값일 수 있다.

영향 범위:

- 주변 가게 목록
- 지도 마커
- 거리순 정렬
- 서버에서 기기 위치 기반으로 계산하는 응답 필드
- 홈 리스트 진입 시 공유되는 목록 상태

특히 `deviceLatitude/deviceLongitude`가 기본 위치로 들어가면, 지도 중심만 내 위치로 이동하더라도 서버 응답의 거리 계산은 실제 사용자 위치와 다를 수 있다.

## 재현 가능성이 높은 조건

다음 조건에서 문제가 더 잘 드러날 가능성이 있다.

1. 앱 설치 직후 첫 실행
2. 기기 재부팅 직후 앱 실행
3. Google Play services가 재시작된 뒤 앱 실행
4. 위치 서비스가 꺼져 있거나 방금 켠 직후
5. 실내/지하/약한 GPS 환경
6. 앱 권한은 허용했지만 최근 위치 캐시가 없는 상태
7. Android 12 이상에서 대략적인 위치만 허용한 상태
8. 홈 화면 진입 직후 네트워크 응답이 빠르고 위치 응답이 느린 상태

## 권장 수정 방향

### 1. 위치 획득 결과를 기다린 뒤 초기 홈 데이터를 조회한다

핵심은 `moveToCurrentLocation()` 호출 직후 바로 `fetchAroundStores()`를 호출하지 않는 것이다.

권장 흐름:

```text
Home initial load
  -> resolveInitialLocation()
    -> 권한 없음: default 또는 권한 요청 결과 처리
    -> 권한 있음:
       -> lastLocation 확인
       -> null이면 getCurrentLocation 또는 1회 location update
       -> 성공: userLocation/mapPosition 갱신
       -> 실패/timeout: default fallback 명시
  -> moveCamera(resolvedLocation)
  -> fetchAroundStores(resolvedLocation 기준)
  -> getAdvertisement(resolvedLocation 기준)
```

`fetchAroundStores()`가 내부 state를 읽는 구조를 유지하더라도, 호출 전에 `uiState.mapPosition`과 `uiState.userLocation`이 확실히 갱신되어야 한다.

더 안전한 방식은 초기 조회 함수가 좌표를 명시적으로 받도록 만드는 것이다.

```text
fetchAroundStores(
  mapPosition = resolvedLocation,
  userLocation = resolvedLocation,
)
```

이렇게 하면 state 갱신 타이밍과 API 호출 타이밍의 결합을 줄일 수 있다.

### 2. `lastLocation == null` fallback을 추가한다

`lastLocation`은 캐시 조회 API이므로 null 가능성을 정상 케이스로 다뤄야 한다.

권장 fallback 순서:

```text
1. getLastLocation()
2. null이면 getCurrentLocation()
3. 실패 또는 timeout이면 requestLocationUpdates() 1회
4. 그래도 실패하면 default location 사용 또는 사용자 안내
```

`getCurrentLocation()`은 단일 현재 위치 fix를 얻기 위한 API로, 공식 문서에서도 fresh location이 필요할 때 더 일관적인 방법으로 안내한다.

주의할 점:

- timeout을 둬야 한다.
- 화면이 사라지면 cancellation이 되어야 한다.
- 위치 요청 실패/취소/권한 없음/GPS 꺼짐을 구분해서 로그를 남기는 것이 좋다.

### 3. 초기 로딩 상태를 명시한다

현재는 기본 위치가 진짜 fallback인지, 위치를 기다리는 중인지 UI에서 구분하기 어렵다.

권장 상태:

```text
LocationResolveState
  - Idle
  - Resolving
  - Resolved(location)
  - PermissionDenied
  - GpsDisabled
  - UnavailableFallback(defaultLocation, reason)
```

이 상태를 두면 다음 동작이 명확해진다.

- 위치 확인 중에는 주변 가게 조회를 지연
- timeout 후 fallback 시에는 기본 위치를 의도적으로 사용
- 권한 거부와 GPS 꺼짐을 다른 UX로 안내

### 4. 주소 표시용 상태와 지도 조회 상태를 분리한다

현재 `currentLocation`은 이름과 실제 의미가 섞여 있다.

권장 분리:

- `userLocation`: GPS 기반 사용자 위치
- `mapPosition`: 현재 지도 중심
- `lastQueriedLocation`: 마지막 주변 가게 조회 기준 위치
- `addressLocation`: 주소 텍스트 표시 기준 위치

최소한 `currentLocation` flow가 실제 GPS 위치인지 지도 중심인지 이름과 쓰임을 맞춰야 한다.

### 5. 광고 조회도 위치 확정 이후로 이동한다

`initViewModel()`에서 지도 중심을 바로 읽어 광고를 요청하는 현재 구조는 초기 기본 위치 광고를 만들 수 있다.

권장 방식:

```text
initial location resolved
  -> fetchAroundStores(location)
  -> getAdvertisement(location)
```

지도 중심이 필요하다면 `onMapReady` 이후 또는 위치 resolve 이후에 호출해야 한다.

### 6. coarse-only 권한을 정책적으로 결정한다

정확한 위치가 필수라면 fine 권한이 없을 때 "정확한 위치 권한이 필요함"을 명확히 안내해야 한다.

대략적인 위치도 허용할 수 있다면 `isLocationAvailable()`은 coarse 권한도 고려해야 한다.

예시 정책:

```text
fine 허용: 정확한 위치 사용
coarse만 허용: 대략 위치 사용 + 필요한 기능에서 정확 위치 요청
둘 다 없음: 권한 요청 또는 default fallback
```

## 로깅/계측 권장 사항

원인 확인을 위해 아래 로그를 일시적으로 추가하면 좋다.

초기 위치 resolve:

```text
home_location_init_start
permissionFine=true/false
permissionCoarse=true/false
gpsEnabled=true/false
mapReady=true/false
savedPositionExists=true/false
```

`lastLocation` 결과:

```text
home_location_last_success
isNull=true/false
elapsedMs=...
latitude=...
longitude=...
```

fallback 결과:

```text
home_location_current_success
home_location_timeout_fallback
home_location_permission_denied
home_location_gps_disabled
```

주변 가게 조회 직전:

```text
home_fetch_around_stores
mapLatitude=...
mapLongitude=...
deviceLatitude=...
deviceLongitude=...
source=initial_location/current_button/retry/map_gesture/filter/search_address
```

이 로그가 있으면 "위치가 늦게 온 것인지", "아예 null인 것인지", "권한/GPS 문제인지", "저장 위치 복원 때문인지"를 분리해서 볼 수 있다.

## 검증 시나리오

### 수동 검증

1. 앱 데이터 삭제 후 첫 실행
   - 권한 요청 전 기본 위치 사용 여부 확인
   - 권한 허용 직후 실제 위치 기준 조회 여부 확인

2. 위치 권한 이미 허용 + 앱 강제 종료 후 실행
   - 홈 첫 조회 API의 `mapLatitude/deviceLatitude`가 실제 위치인지 확인

3. 기기 재부팅 직후 실행
   - `lastLocation == null` 가능성 확인
   - fallback이 동작하는지 확인

4. 위치 서비스 OFF 상태로 실행
   - GPS disabled UX 확인
   - 기본 위치 fallback이 의도대로 동작하는지 확인

5. Android 12 이상에서 대략적인 위치만 허용
   - 권한 없음으로 오판하지 않는지 확인
   - 정책에 맞는 안내 또는 대략 위치 사용 확인

6. 실내/약한 GPS 환경
   - timeout 이후 fallback이 과도하게 늦지 않은지 확인

7. 홈 진입 직후 내 위치 버튼 탭
   - 위치 요청 중복 호출 여부 확인
   - 목록/마커/주소가 같은 좌표 기준으로 갱신되는지 확인

8. 지도 이동 후 재검색
   - 사용자가 옮긴 지도 중심 기준 조회와 실제 기기 위치 헤더가 분리되어 유지되는지 확인

### 자동 테스트 후보

현재 위치 API는 Android framework/Google Play services 의존성이 있어 순수 unit test가 어렵다. 위치 획득 책임을 별도 abstraction으로 분리하면 다음 테스트가 가능하다.

1. `lastLocation` 성공 시
   - resolved location으로 `mapPosition/userLocation` 갱신
   - 해당 좌표로 `fetchAroundStores` 호출

2. `lastLocation == null`, `getCurrentLocation` 성공 시
   - current location fallback 결과 사용

3. 모든 위치 획득 실패 시
   - default fallback 사용
   - fallback reason 기록

4. 권한 없음 시
   - 권한 요청 flow 진입
   - 임의 API 조회가 먼저 나가지 않음

5. GPS 꺼짐 시
   - 위치 요청을 시도하지 않거나 settings 안내 상태로 전환
   - 기본 위치 사용 여부가 명시적으로 결정됨

6. 초기 위치 resolve 중 필터 변경 시
   - 기본 위치로 중복 조회하지 않음
   - resolve 완료 후 최신 필터 상태로 1회 조회

## 수정 시 주의사항

- `fetchAroundStores()`는 홈 필터, 카테고리, 지도 이동, 재검색, 검색 주소 선택 등 여러 진입점에서 호출된다. 초기 위치 문제를 고치면서 모든 호출 경로를 같은 방식으로 바꾸면 영향 범위가 커진다.
- 우선은 "홈 최초 진입"과 "권한 허용 직후" 경로만 좁게 수정하는 것이 안전하다.
- `SavedStateHandle`의 저장 위치 복원은 기존 버그 수정 이력이 있으므로 제거하지 말고, 초기 위치 resolve 정책과 우선순위를 명확히 해야 한다.
- `LocationTrackingMode.Follow/NoFollow/None` 변경은 지도 UX에 직접 영향이 있다. 위치 마커 표시와 카메라 이동을 분리해서 검증해야 한다.
- 기본 위치 fallback 자체는 필요하다. 문제는 fallback이 의도된 실패 처리인지, 비동기 타이밍 때문에 먼저 실행된 것인지 구분되지 않는 점이다.

## 제안 구현 순서

1. 홈 초기 위치 획득을 담당하는 함수 만들기
   - 예: `resolveInitialLocation(onResolved, onFallback)`
   - 기존 `moveToCurrentLocation()`을 바로 API 호출 트리거로 쓰지 않는다.

2. `NaverMapFragment`의 위치 요청 함수에 callback 또는 suspend wrapper 추가
   - `lastLocation` 성공/실패/null을 호출자에게 알려준다.
   - 필요하면 `getCurrentLocation()` fallback을 포함한다.

3. `HomeFragment` 초기 로딩 흐름 변경
   - 위치 resolve 완료 후 `viewModel.updateUserLocation()`
   - 지도 카메라 이동
   - `viewModel.updateMapPosition()`
   - `fetchAroundStores()`
   - `getAdvertisement()`

4. `fetchAroundStores()` 호출 시점 정리
   - 초기 자동 조회와 사용자 액션 조회를 구분한다.
   - 초기 자동 조회는 위치 resolve 이전에 실행되지 않게 한다.

5. 권한/GPS/coarse-only 상태 정리
   - 앱 정책에 맞춰 `isLocationAvailable()` 또는 별도 permission state 함수 추가

6. 로그 추가 후 QA
   - 실제 기기에서 재부팅/권한 변경/GPS OFF 케이스 확인

## 참고 자료

- Android Developers, Get the last known location: https://developer.android.com/develop/sensors-and-location/location/retrieve-current
- Google Play services, FusedLocationProviderClient reference: https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderClient

## 최종 판단

현재 증상은 단순히 "GPS가 늦다" 하나로 보기 어렵다. 더 정확히는 다음 세 문제가 겹쳐 있다.

1. 현재 위치 획득이 비동기인데 홈 초기 API 조회가 이를 기다리지 않는다.
2. fresh location 요청 없이 `lastLocation` 캐시에만 의존한다.
3. 기본 위치와 실제 fallback 상태가 UI/state/API 레벨에서 구분되지 않는다.

따라서 수정 방향은 "딜레이를 늘리는 것"이 아니라 "초기 위치 resolve를 명시적인 단계로 만들고, 성공/실패/fallback 이후에 홈 데이터를 조회하도록 순서를 재구성하는 것"이 맞다.
