package com.gildedrose;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GildedRoseTest {

    private static final String NORMAL = "+5 Dexterity Vest";
    private static final String BRIE = "Aged Brie";
    private static final String BACKSTAGE = "Backstage passes to a TAFKAL80ETC concert";
    private static final String SULFURAS = "Sulfuras, Hand of Ragnaros";

    @Mock
    private ItemRepository repository;

    // Given: 아이템 1개를 담은 저장소 stub, When: 하루 경과, 갱신된 아이템을 반환
    private Item updateOneDay(String name, int sellIn, int quality) {
        Item item = new Item(name, sellIn, quality);
        when(repository.findAll()).thenReturn(Arrays.asList(item));

        new GildedRose(repository).updateQuality();

        return item;
    }

    private void assertItem(Item item, int expectedSellIn, int expectedQuality) {
        assertEquals(expectedSellIn, item.sellIn, "sellIn");
        assertEquals(expectedQuality, item.quality, "quality");
    }

    // ---------- 일반 아이템 ----------

    @Test
    @DisplayName("#1 일반 아이템은 유통기한 전에는 품질이 1 감소한다")
    void normalItem_qualityDecreasesByOne_beforeSellDate() {
        Item item = updateOneDay(NORMAL, 10, 20);

        assertItem(item, 9, 19);
    }

    @Test
    @DisplayName("#2 일반 아이템은 유통기한이 1이면 품질이 1 감소한다")
    void normalItem_qualityDecreasesByOne_whenSellInIsOne() {
        Item item = updateOneDay(NORMAL, 1, 20);

        assertItem(item, 0, 19);
    }

    @Test
    @DisplayName("#3 일반 아이템은 유통기한이 0이면 품질이 2 감소한다")
    void normalItem_qualityDecreasesByTwo_whenSellInIsZero() {
        Item item = updateOneDay(NORMAL, 0, 20);

        assertItem(item, -1, 18);
    }

    @Test
    @DisplayName("#4 일반 아이템은 유통기한이 지난 후에도 품질이 2 감소한다")
    void normalItem_qualityDecreasesByTwo_afterSellDate() {
        Item item = updateOneDay(NORMAL, -1, 20);

        assertItem(item, -2, 18);
    }

    @Test
    @DisplayName("#5 일반 아이템의 품질은 0 아래로 감소하지 않는다")
    void normalItem_qualityNeverNegative() {
        Item item = updateOneDay(NORMAL, 5, 0);

        assertItem(item, 4, 0);
    }

    @Test
    @DisplayName("#6 일반 아이템은 품질이 1일 때 2 감소해야 해도 0이 된다")
    void normalItem_qualityStopsAtZero_whenDecreaseExceedsQuality() {
        Item item = updateOneDay(NORMAL, 0, 1);

        assertItem(item, -1, 0);
    }

    // ---------- Aged Brie ----------

    @Test
    @DisplayName("#7 Aged Brie는 유통기한 전에는 품질이 1 증가한다")
    void agedBrie_qualityIncreasesByOne_beforeSellDate() {
        Item item = updateOneDay(BRIE, 5, 10);

        assertItem(item, 4, 11);
    }

    @Test
    @DisplayName("#8 Aged Brie는 유통기한이 지나면 품질이 2 증가한다")
    void agedBrie_qualityIncreasesByTwo_afterSellDate() {
        Item item = updateOneDay(BRIE, 0, 10);

        assertItem(item, -1, 12);
    }

    @Test
    @DisplayName("#9 Aged Brie의 품질은 50을 초과하지 않는다")
    void agedBrie_qualityNeverExceedsFifty() {
        Item item = updateOneDay(BRIE, 5, 50);

        assertItem(item, 4, 50);
    }

    @Test
    @DisplayName("#10 Aged Brie는 품질이 49일 때 2 증가해야 해도 50까지만 증가한다")
    void agedBrie_qualityStopsAtFifty_whenIncreaseExceedsMax() {
        Item item = updateOneDay(BRIE, 0, 49);

        assertItem(item, -1, 50);
    }

    @Test
    @DisplayName("#23 Aged Brie는 유통기한이 지나도 품질이 50이면 유지된다")
    void agedBrie_qualityStaysFifty_afterSellDate() {
        Item item = updateOneDay(BRIE, 0, 50);

        assertItem(item, -1, 50);
    }

    // ---------- Backstage passes ----------

    @Test
    @DisplayName("#11 Backstage passes는 공연까지 11일 남으면 품질이 1 증가한다")
    void backstage_qualityIncreasesByOne_whenElevenDaysLeft() {
        Item item = updateOneDay(BACKSTAGE, 11, 20);

        assertItem(item, 10, 21);
    }

    @Test
    @DisplayName("#12 Backstage passes는 공연까지 10일 남으면 품질이 2 증가한다")
    void backstage_qualityIncreasesByTwo_whenTenDaysLeft() {
        Item item = updateOneDay(BACKSTAGE, 10, 20);

        assertItem(item, 9, 22);
    }

    @Test
    @DisplayName("#13 Backstage passes는 공연까지 6일 남으면 품질이 2 증가한다")
    void backstage_qualityIncreasesByTwo_whenSixDaysLeft() {
        Item item = updateOneDay(BACKSTAGE, 6, 20);

        assertItem(item, 5, 22);
    }

    @Test
    @DisplayName("#14 Backstage passes는 공연까지 5일 남으면 품질이 3 증가한다")
    void backstage_qualityIncreasesByThree_whenFiveDaysLeft() {
        Item item = updateOneDay(BACKSTAGE, 5, 20);

        assertItem(item, 4, 23);
    }

    @Test
    @DisplayName("#15 Backstage passes는 공연까지 1일 남으면 품질이 3 증가한다")
    void backstage_qualityIncreasesByThree_whenOneDayLeft() {
        Item item = updateOneDay(BACKSTAGE, 1, 20);

        assertItem(item, 0, 23);
    }

    @Test
    @DisplayName("#16 Backstage passes는 공연이 끝나면 품질이 0이 된다")
    void backstage_qualityDropsToZero_afterConcert() {
        Item item = updateOneDay(BACKSTAGE, 0, 20);

        assertItem(item, -1, 0);
    }

    @Test
    @DisplayName("#17-1 Backstage passes는 품질이 49일 때 2 증가해야 해도 50까지만 증가한다")
    void backstage_qualityStopsAtFifty_whenTenDaysLeftAndQualityIs49() {
        Item item = updateOneDay(BACKSTAGE, 10, 49);

        assertItem(item, 9, 50);
    }

    @Test
    @DisplayName("#17-2 Backstage passes는 품질이 48일 때 3 증가해야 해도 50까지만 증가한다")
    void backstage_qualityStopsAtFifty_whenFiveDaysLeftAndQualityIs48() {
        Item item = updateOneDay(BACKSTAGE, 5, 48);

        assertItem(item, 4, 50);
    }

    @Test
    @DisplayName("#18 Backstage passes는 품질이 이미 50이면 증가하지 않는다")
    void backstage_qualityStaysFifty_whenAlreadyMax() {
        Item item = updateOneDay(BACKSTAGE, 5, 50);

        assertItem(item, 4, 50);
    }

    @Test
    @DisplayName("#24 Backstage passes는 이미 공연이 끝난 상태에서도 품질이 0이 된다")
    void backstage_qualityIsZero_whenConcertAlreadyOver() {
        Item item = updateOneDay(BACKSTAGE, -1, 20);

        assertItem(item, -2, 0);
    }

    @Test
    @DisplayName("#25 Backstage passes는 품질이 50이어도 공연이 끝나면 0이 된다")
    void backstage_qualityDropsToZero_afterConcertEvenIfQualityIsFifty() {
        Item item = updateOneDay(BACKSTAGE, 0, 50);

        assertItem(item, -1, 0);
    }

    // ---------- Sulfuras ----------

    @Test
    @DisplayName("#19 Sulfuras는 유통기한과 품질이 변하지 않는다")
    void sulfuras_neverChanges() {
        Item item = updateOneDay(SULFURAS, 10, 80);

        assertItem(item, 10, 80);
    }

    @Test
    @DisplayName("#20 Sulfuras는 유통기한이 지난 상태에서도 변하지 않는다")
    void sulfuras_neverChanges_afterSellDate() {
        Item item = updateOneDay(SULFURAS, -1, 80);

        assertItem(item, -1, 80);
    }

    // ---------- 공통 동작 / Repository 상호작용 ----------

    @Test
    @DisplayName("#21 아이템이 여러 개면 각각의 규칙에 따라 갱신한다")
    void multipleItems_eachUpdatedByItsOwnRule() {
        Item vest = new Item(NORMAL, 10, 20);
        Item brie = new Item(BRIE, 5, 10);
        when(repository.findAll()).thenReturn(Arrays.asList(vest, brie));

        new GildedRose(repository).updateQuality();

        assertItem(vest, 9, 19);
        assertItem(brie, 4, 11);
    }

    @Test
    @DisplayName("#22 갱신된 아이템을 Repository에 저장하도록 요청한다")
    void updateQuality_requestsSavingUpdatedItems() {
        Item vest = new Item(NORMAL, 10, 20);
        when(repository.findAll()).thenReturn(Arrays.asList(vest));

        new GildedRose(repository).updateQuality();

        verify(repository).saveAll(Arrays.asList(vest));
    }
}
