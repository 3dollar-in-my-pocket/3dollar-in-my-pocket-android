package com.zion830.threedollars.ui.write.menuextraction

import java.io.File

class MenuBoardImage {
    private var file: File? = null

    fun register(image: File?) {
        if (image != null) file = image
    }

    fun consume(): File? = file.also { file = null }
}
