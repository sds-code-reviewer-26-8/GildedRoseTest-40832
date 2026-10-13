## 1. 테스트 결과

### Coverage

(IntelliJ `GildedRose` Line / Branch Coverage 캡처 첨부)

> 참고(JaCoCo 기준): Line 100% (35/35), Branch 100% (36/36)

### 테스트 실행 시간

(`GildedRoseTest` 전체 실행 결과 캡처 첨부)

> 참고: 26개 테스트 전부 통과, 전체 약 1.7초 (기준 3초 이내)

## 2. 테스트 보고서

### 스펙과 테스트 매핑

| 검증한 스펙 | 테스트 메서드 |
|---|---|
| 일반 아이템은 유통기한 전에는 품질이 1 감소한다 | `normalItem_qualityDecreasesByOne_beforeSellDate` |
| 일반 아이템은 유통기한이 1이면 품질이 1 감소한다 | `normalItem_qualityDecreasesByOne_whenSellInIsOne` |
| 일반 아이템은 유통기한이 0이면 품질이 2 감소한다 | `normalItem_qualityDecreasesByTwo_whenSellInIsZero` |
| 일반 아이템은 유통기한이 지난 후에도 품질이 2 감소한다 | `normalItem_qualityDecreasesByTwo_afterSellDate` |
| 일반 아이템의 품질은 0 아래로 감소하지 않는다 | `normalItem_qualityNeverNegative` |
| 일반 아이템은 품질이 1일 때 2 감소해야 해도 0이 된다 | `normalItem_qualityStopsAtZero_whenDecreaseExceedsQuality` |
| Aged Brie는 유통기한 전에는 품질이 1 증가한다 | `agedBrie_qualityIncreasesByOne_beforeSellDate` |
| Aged Brie는 유통기한이 지나면 품질이 2 증가한다 | `agedBrie_qualityIncreasesByTwo_afterSellDate` |
| Aged Brie의 품질은 50을 초과하지 않는다 | `agedBrie_qualityNeverExceedsFifty` |
| Aged Brie는 품질이 49일 때 2 증가해야 해도 50까지만 증가한다 | `agedBrie_qualityStopsAtFifty_whenIncreaseExceedsMax` |
| Backstage passes는 공연까지 11일 남으면 품질이 1 증가한다 | `backstage_qualityIncreasesByOne_whenElevenDaysLeft` |
| Backstage passes는 공연까지 10일 남으면 품질이 2 증가한다 | `backstage_qualityIncreasesByTwo_whenTenDaysLeft` |
| Backstage passes는 공연까지 6일 남으면 품질이 2 증가한다 | `backstage_qualityIncreasesByTwo_whenSixDaysLeft` |
| Backstage passes는 공연까지 5일 남으면 품질이 3 증가한다 | `backstage_qualityIncreasesByThree_whenFiveDaysLeft` |
| Backstage passes는 공연까지 1일 남으면 품질이 3 증가한다 | `backstage_qualityIncreasesByThree_whenOneDayLeft` |
| Backstage passes는 공연이 끝나면 품질이 0이 된다 | `backstage_qualityDropsToZero_afterConcert` |
| Backstage passes는 품질이 49일 때 2 증가해야 해도 50까지만 증가한다 | `backstage_qualityStopsAtFifty_whenTenDaysLeftAndQualityIs49` |
| Backstage passes는 품질이 48일 때 3 증가해야 해도 50까지만 증가한다 | `backstage_qualityStopsAtFifty_whenFiveDaysLeftAndQualityIs48` |
| Backstage passes는 품질이 이미 50이면 증가하지 않는다 | `backstage_qualityStaysFifty_whenAlreadyMax` |
| Sulfuras는 유통기한과 품질이 변하지 않는다 | `sulfuras_neverChanges` |
| Sulfuras는 유통기한이 지난 상태에서도 변하지 않는다 | `sulfuras_neverChanges_afterSellDate` |
| 아이템이 여러 개면 각각의 규칙에 따라 갱신한다 | `multipleItems_eachUpdatedByItsOwnRule` |
| 갱신된 아이템을 Repository에 저장하도록 요청한다 | `updateQuality_requestsSavingUpdatedItems` |
| Aged Brie는 유통기한이 지나도 품질이 50이면 유지된다 | `agedBrie_qualityStaysFifty_afterSellDate` |
| Backstage passes는 이미 공연이 끝난 상태에서도 품질이 0이 된다 | `backstage_qualityIsZero_whenConcertAlreadyOver` |
| Backstage passes는 품질이 50이어도 공연이 끝나면 0이 된다 | `backstage_qualityDropsToZero_afterConcertEvenIfQualityIsFifty` |

- 번호는 `ASSIGNMENT.md`의 스펙 번호를 따랐다. #17은 두 경계(품질 49/+2, 48/+3)를 각각 별도 테스트로 나누었고, #23~#25는 힌트에 없는 경계를 직접 추가한 것이다.
- 모든 테스트는 한 테스트에 한 스펙만 검증하며, 본문을 Given / When / Then 구조로 작성했다. 모든 스펙에서 `sellIn`과 `quality`를 함께 검증했다.
- 경계값: 유통기한 1/0/-1, Backstage 11/10/6/5/1/0일, 품질 0/1/48/49/50.

### Test Double 사용

- **어떤 의존성을 대체했나요?**
  `GildedRose`가 의존하는 `ItemRepository`(저장소). 기존 구현체 `InMemoryItemRepository`는 호출마다 1초씩 지연된다.
- **어떤 Test Double을 사용했나요?**
  Mockito의 `@Mock ItemRepository`를 사용했다.
  - 스펙 검증 테스트: `when(repository.findAll()).thenReturn(...)`로 데이터를 공급하는 **Stub** 역할 + `assertEquals`로 결과 검증
  - 상호작용 검증 테스트(#22): 위 stub에 `verify(repository).saveAll(...)`를 더한 **Mock** 역할
- **사용한 이유는 무엇인가요?**
  1. **속도:** `updateQuality()` 한 번이 `findAll()` 2회(생성자 + 갱신)와 `saveAll()` 1회를 호출하므로 실제 구현체로는 테스트 하나에 3초 이상 걸린다. 26개를 돌리면 수 분이 걸린다. Test Double로 전체 약 1.7초에 끝난다.
  2. **격리:** 테스트가 저장소 구현이 아닌 `GildedRose`의 갱신 규칙만 검증한다. 저장소 상태에 영향받지 않아 결과가 결정적이다.
  3. **입력 제어:** 아이템별로 원하는 `sellIn`/`quality`를 stub으로 정확히 공급해 경계값을 쉽게 만든다.
  4. **협력 검증:** 실제 저장 대신 `verify`로 "갱신된 아이템을 `saveAll`로 저장 요청했는가"를 확인할 수 있다.

### 변경 사항

- `src/test/java/com/gildedrose/GildedRoseTest.java`: 기존 placeholder 테스트(`foo`)를 제거하고 위 26개 테스트로 교체
- `build.gradle`: Mockito 의존성(`mockito-core`, `mockito-junit-jupiter` 4.11.0) 추가, JaCoCo 플러그인(Coverage 수치 확인용), 컴파일 인코딩 UTF-8(한글 주석 경고 해결)
- `CLAUDE.md`, `ASSIGNMENT_LOG.md`: 코드 구조 정리와 과제 진행 로그
- `src/main`은 수정하지 않았다.
