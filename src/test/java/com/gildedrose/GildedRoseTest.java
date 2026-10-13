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

    // ---------- 일반 아이템 ----------

    @Test
    @DisplayName("#1 일반 아이템은 유통기한 전에는 품질이 1 감소한다")
    void normalItem_qualityDecreasesByOne_beforeSellDate() {
        // Given
        Item item = new Item(NORMAL, 10, 20);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(9, item.sellIn);
        assertEquals(19, item.quality);
    }

    @Test
    @DisplayName("#2 일반 아이템은 유통기한이 1이면 품질이 1 감소한다")
    void normalItem_qualityDecreasesByOne_whenSellInIsOne() {
        // Given
        Item item = new Item(NORMAL, 1, 20);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(0, item.sellIn);
        assertEquals(19, item.quality);
    }

    @Test
    @DisplayName("#3 일반 아이템은 유통기한이 0이면 품질이 2 감소한다")
    void normalItem_qualityDecreasesByTwo_whenSellInIsZero() {
        // Given
        Item item = new Item(NORMAL, 0, 20);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(-1, item.sellIn);
        assertEquals(18, item.quality);
    }

    @Test
    @DisplayName("#4 일반 아이템은 유통기한이 지난 후에도 품질이 2 감소한다")
    void normalItem_qualityDecreasesByTwo_afterSellDate() {
        // Given
        Item item = new Item(NORMAL, -1, 20);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(-2, item.sellIn);
        assertEquals(18, item.quality);
    }

    @Test
    @DisplayName("#5 일반 아이템의 품질은 0 아래로 감소하지 않는다")
    void normalItem_qualityNeverNegative() {
        // Given
        Item item = new Item(NORMAL, 5, 0);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(4, item.sellIn);
        assertEquals(0, item.quality);
    }

    @Test
    @DisplayName("#6 일반 아이템은 품질이 1일 때 2 감소해야 해도 0이 된다")
    void normalItem_qualityStopsAtZero_whenDecreaseExceedsQuality() {
        // Given
        Item item = new Item(NORMAL, 0, 1);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(-1, item.sellIn);
        assertEquals(0, item.quality);
    }

    // ---------- Aged Brie ----------

    @Test
    @DisplayName("#7 Aged Brie는 유통기한 전에는 품질이 1 증가한다")
    void agedBrie_qualityIncreasesByOne_beforeSellDate() {
        // Given
        Item item = new Item(BRIE, 5, 10);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(4, item.sellIn);
        assertEquals(11, item.quality);
    }

    @Test
    @DisplayName("#8 Aged Brie는 유통기한이 지나면 품질이 2 증가한다")
    void agedBrie_qualityIncreasesByTwo_afterSellDate() {
        // Given
        Item item = new Item(BRIE, 0, 10);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(-1, item.sellIn);
        assertEquals(12, item.quality);
    }

    @Test
    @DisplayName("#9 Aged Brie의 품질은 50을 초과하지 않는다")
    void agedBrie_qualityNeverExceedsFifty() {
        // Given
        Item item = new Item(BRIE, 5, 50);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(4, item.sellIn);
        assertEquals(50, item.quality);
    }

    @Test
    @DisplayName("#10 Aged Brie는 품질이 49일 때 2 증가해야 해도 50까지만 증가한다")
    void agedBrie_qualityStopsAtFifty_whenIncreaseExceedsMax() {
        // Given
        Item item = new Item(BRIE, 0, 49);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(-1, item.sellIn);
        assertEquals(50, item.quality);
    }

    @Test
    @DisplayName("#23 Aged Brie는 유통기한이 지나도 품질이 50이면 유지된다")
    void agedBrie_qualityStaysFifty_afterSellDate() {
        // Given
        Item item = new Item(BRIE, 0, 50);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(-1, item.sellIn);
        assertEquals(50, item.quality);
    }

    // ---------- Backstage passes ----------

    @Test
    @DisplayName("#11 Backstage passes는 공연까지 11일 남으면 품질이 1 증가한다")
    void backstage_qualityIncreasesByOne_whenElevenDaysLeft() {
        // Given
        Item item = new Item(BACKSTAGE, 11, 20);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(10, item.sellIn);
        assertEquals(21, item.quality);
    }

    @Test
    @DisplayName("#12 Backstage passes는 공연까지 10일 남으면 품질이 2 증가한다")
    void backstage_qualityIncreasesByTwo_whenTenDaysLeft() {
        // Given
        Item item = new Item(BACKSTAGE, 10, 20);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(9, item.sellIn);
        assertEquals(22, item.quality);
    }

    @Test
    @DisplayName("#13 Backstage passes는 공연까지 6일 남으면 품질이 2 증가한다")
    void backstage_qualityIncreasesByTwo_whenSixDaysLeft() {
        // Given
        Item item = new Item(BACKSTAGE, 6, 20);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(5, item.sellIn);
        assertEquals(22, item.quality);
    }

    @Test
    @DisplayName("#14 Backstage passes는 공연까지 5일 남으면 품질이 3 증가한다")
    void backstage_qualityIncreasesByThree_whenFiveDaysLeft() {
        // Given
        Item item = new Item(BACKSTAGE, 5, 20);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(4, item.sellIn);
        assertEquals(23, item.quality);
    }

    @Test
    @DisplayName("#15 Backstage passes는 공연까지 1일 남으면 품질이 3 증가한다")
    void backstage_qualityIncreasesByThree_whenOneDayLeft() {
        // Given
        Item item = new Item(BACKSTAGE, 1, 20);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(0, item.sellIn);
        assertEquals(23, item.quality);
    }

    @Test
    @DisplayName("#16 Backstage passes는 공연이 끝나면 품질이 0이 된다")
    void backstage_qualityDropsToZero_afterConcert() {
        // Given
        Item item = new Item(BACKSTAGE, 0, 20);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(-1, item.sellIn);
        assertEquals(0, item.quality);
    }

    @Test
    @DisplayName("#17-1 Backstage passes는 품질이 49일 때 2 증가해야 해도 50까지만 증가한다")
    void backstage_qualityStopsAtFifty_whenTenDaysLeftAndQualityIs49() {
        // Given
        Item item = new Item(BACKSTAGE, 10, 49);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(9, item.sellIn);
        assertEquals(50, item.quality);
    }

    @Test
    @DisplayName("#17-2 Backstage passes는 품질이 48일 때 3 증가해야 해도 50까지만 증가한다")
    void backstage_qualityStopsAtFifty_whenFiveDaysLeftAndQualityIs48() {
        // Given
        Item item = new Item(BACKSTAGE, 5, 48);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(4, item.sellIn);
        assertEquals(50, item.quality);
    }

    @Test
    @DisplayName("#18 Backstage passes는 품질이 이미 50이면 증가하지 않는다")
    void backstage_qualityStaysFifty_whenAlreadyMax() {
        // Given
        Item item = new Item(BACKSTAGE, 5, 50);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(4, item.sellIn);
        assertEquals(50, item.quality);
    }

    @Test
    @DisplayName("#24 Backstage passes는 이미 공연이 끝난 상태에서도 품질이 0이 된다")
    void backstage_qualityIsZero_whenConcertAlreadyOver() {
        // Given
        Item item = new Item(BACKSTAGE, -1, 20);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(-2, item.sellIn);
        assertEquals(0, item.quality);
    }

    @Test
    @DisplayName("#25 Backstage passes는 품질이 50이어도 공연이 끝나면 0이 된다")
    void backstage_qualityDropsToZero_afterConcertEvenIfQualityIsFifty() {
        // Given
        Item item = new Item(BACKSTAGE, 0, 50);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(-1, item.sellIn);
        assertEquals(0, item.quality);
    }

    // ---------- Sulfuras ----------

    @Test
    @DisplayName("#19 Sulfuras는 유통기한과 품질이 변하지 않는다")
    void sulfuras_neverChanges() {
        // Given
        Item item = new Item(SULFURAS, 10, 80);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(10, item.sellIn);
        assertEquals(80, item.quality);
    }

    @Test
    @DisplayName("#20 Sulfuras는 유통기한이 지난 상태에서도 변하지 않는다")
    void sulfuras_neverChanges_afterSellDate() {
        // Given
        Item item = new Item(SULFURAS, -1, 80);
        when(repository.findAll()).thenReturn(Arrays.asList(item));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(-1, item.sellIn);
        assertEquals(80, item.quality);
    }

    // ---------- 공통 동작 / Repository 상호작용 ----------

    @Test
    @DisplayName("#21 아이템이 여러 개면 각각의 규칙에 따라 갱신한다")
    void multipleItems_eachUpdatedByItsOwnRule() {
        // Given
        Item vest = new Item(NORMAL, 10, 20);
        Item brie = new Item(BRIE, 5, 10);
        when(repository.findAll()).thenReturn(Arrays.asList(vest, brie));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        assertEquals(9, vest.sellIn);
        assertEquals(19, vest.quality);
        assertEquals(4, brie.sellIn);
        assertEquals(11, brie.quality);
    }

    @Test
    @DisplayName("#22 갱신된 아이템을 Repository에 저장하도록 요청한다")
    void updateQuality_requestsSavingUpdatedItems() {
        // Given
        Item vest = new Item(NORMAL, 10, 20);
        when(repository.findAll()).thenReturn(Arrays.asList(vest));
        GildedRose app = new GildedRose(repository);

        // When
        app.updateQuality();

        // Then
        verify(repository).saveAll(Arrays.asList(vest));
    }
}
