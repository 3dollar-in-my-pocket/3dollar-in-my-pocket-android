package com.zion830.threedollars.ui.my.page.team

import androidx.lifecycle.viewModelScope
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseViewModel
import com.threedollar.domain.creator.repository.ServiceCreatorRepository
import com.threedollar.network.result.ApiException
import com.zion830.threedollars.ui.my.page.data.TeamRow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageTeamViewModel @Inject constructor(
    private val serviceCreatorRepository: ServiceCreatorRepository,
) : BaseViewModel() {
    override val screenName: ScreenName = ScreenName.TEAM_INFO

    private val _teams = MutableStateFlow<ImmutableList<TeamRow>>(persistentListOf())
    val teams: StateFlow<ImmutableList<TeamRow>> = _teams.asStateFlow()

    private val _errorMessage = Channel<String?>(Channel.CONFLATED)
    val errorMessage: Flow<String?> = _errorMessage.receiveAsFlow()

    init {
        loadCreators()
    }

    private fun loadCreators() {
        viewModelScope.launch(coroutineExceptionHandler) {
            serviceCreatorRepository.listCreators()
                .onSuccess { groups -> _teams.value = TeamRow.from(groups) }
                .onFailure { error -> _errorMessage.trySend((error as? ApiException)?.message) }
        }
    }
}
