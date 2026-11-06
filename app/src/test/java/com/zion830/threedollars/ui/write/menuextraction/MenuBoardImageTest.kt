package com.zion830.threedollars.ui.write.menuextraction

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.File

class MenuBoardImageTest {

    // TH-1543 TC2
    @Test
    fun `TH1543_TC2_AI메뉴판을_등록하지_않았으면_올릴_사진이_없다`() {
        // Given
        val menuBoardImage = MenuBoardImage()

        // When
        menuBoardImage.register(null)

        // Then
        assertNull(menuBoardImage.consume())
    }

    // TH-1543 TC7
    @Test
    fun `TH1543_TC7_한번_꺼낸_메뉴판_사진은_같은_흐름에서_다시_올리지_않는다`() {
        // Given
        val menuBoardImage = MenuBoardImage()
        val file = File("upload_1.jpg")
        menuBoardImage.register(file)

        // When
        val first = menuBoardImage.consume()
        val second = menuBoardImage.consume()

        // Then
        assertEquals(file, first)
        assertNull(second)
    }
}
