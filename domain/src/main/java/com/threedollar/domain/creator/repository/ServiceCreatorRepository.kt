package com.threedollar.domain.creator.repository

import com.threedollar.domain.creator.model.CreatorGroupModel

interface ServiceCreatorRepository {
    suspend fun listCreators(): Result<List<CreatorGroupModel>>
}
