package com.threedollar.data.creator.repository

import com.threedollar.domain.creator.model.CreatorGroupModel
import com.threedollar.domain.creator.repository.ServiceCreatorRepository
import com.threedollar.network.api.ServiceCreatorApi
import com.threedollar.network.data.creator.ContentListCreatorGroupResponse
import com.threedollar.network.util.runApi
import javax.inject.Inject

class ServiceCreatorRepositoryImpl @Inject constructor(
    private val serviceCreatorApi: ServiceCreatorApi,
) : ServiceCreatorRepository {

    override suspend fun listCreators(): Result<List<CreatorGroupModel>> =
        runApi { serviceCreatorApi.listCreators() }.map { it.asModel() }

    private fun ContentListCreatorGroupResponse.asModel(): List<CreatorGroupModel> =
        contents.orEmpty().filterNotNull().mapNotNull { group ->
            val role = group.role?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
            CreatorGroupModel(
                role = role,
                creatorNames = group.creatorNames.orEmpty().filterNotNull().filter { it.isNotBlank() },
            )
        }
}
