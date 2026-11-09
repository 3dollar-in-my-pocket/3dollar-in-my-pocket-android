package com.zion830.threedollars.ui.my.page.data

import com.threedollar.domain.creator.model.CreatorGroupModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class TeamRow(val role: String, val members: ImmutableList<String>) {
    companion object {
        fun from(groups: List<CreatorGroupModel>): ImmutableList<TeamRow> =
            groups
                .filter { it.creatorNames.isNotEmpty() }
                .map { TeamRow(role = it.role, members = it.creatorNames.toImmutableList()) }
                .toImmutableList()
    }
}
