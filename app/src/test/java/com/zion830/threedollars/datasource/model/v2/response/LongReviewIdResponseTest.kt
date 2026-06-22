package com.zion830.threedollars.datasource.model.v2.response

import com.google.gson.Gson
import com.zion830.threedollars.datasource.model.v2.response.my.MyReviewResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class LongReviewIdResponseTest {

    @Test
    fun gsonParsesLongReviewIdFromNewReviewResponse() {
        val longReviewId = 855453324337299456L
        val response = Gson().fromJson(
            """
            {
              "data": {
                "contents": "맛있어요",
                "createdAt": "2026-06-22T00:00:00",
                "rating": 5,
                "reviewId": $longReviewId,
                "storeId": 100186,
                "updatedAt": "2026-06-22T00:00:00"
              },
              "message": "success",
              "resultCode": "OK"
            }
            """.trimIndent(),
            NewReviewResponse::class.java,
        )

        assertEquals(longReviewId, response.data.reviewId)
    }

    @Test
    fun gsonParsesLongReviewIdFromMyReviewResponse() {
        val longReviewId = 855453324337299456L
        val response = Gson().fromJson(
            """
            {
              "data": {
                "contents": [
                  {
                    "categories": [],
                    "contents": "맛있어요",
                    "createdAt": "2026-06-22T00:00:00",
                    "rating": 5,
                    "reviewId": $longReviewId,
                    "store": {},
                    "storeName": "가게",
                    "updatedAt": "2026-06-22T00:00:00",
                    "user": {}
                  }
                ],
                "nextCursor": 0,
                "totalElements": 1
              },
              "message": "success",
              "resultCode": "OK"
            }
            """.trimIndent(),
            MyReviewResponse::class.java,
        )

        assertEquals(longReviewId, response.data.contents.single().reviewId)
    }
}
