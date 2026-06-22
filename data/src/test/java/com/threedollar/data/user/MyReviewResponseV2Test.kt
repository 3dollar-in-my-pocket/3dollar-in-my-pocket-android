package com.threedollar.data.user

import com.google.gson.Gson
import com.threedollar.network.data.user.MyReviewResponseV2
import org.junit.Assert.assertEquals
import org.junit.Test

class MyReviewResponseV2Test {

    @Test
    fun gsonParsesLongReviewIds() {
        val longReviewId = 855453324337299456L
        val response = Gson().fromJson(
            """
            {
              "contents": [
                {
                  "review": {
                    "reviewId": $longReviewId,
                    "storeId": $longReviewId,
                    "userId": $longReviewId,
                    "rating": 5,
                    "contents": "맛있어요"
                  },
                  "store": {
                    "storeId": "$longReviewId"
                  },
                  "reviewWriter": {
                    "userId": $longReviewId
                  }
                }
              ],
              "cursor": {
                "nextCursor": null,
                "hasMore": false
              }
            }
            """.trimIndent(),
            MyReviewResponseV2::class.java,
        )

        val content = response.contents.single()

        assertEquals(longReviewId, content.review.reviewId)
        assertEquals(longReviewId, content.review.storeId)
        assertEquals(longReviewId, content.review.userId)
        assertEquals(longReviewId, content.reviewWriter.userId)
    }
}
