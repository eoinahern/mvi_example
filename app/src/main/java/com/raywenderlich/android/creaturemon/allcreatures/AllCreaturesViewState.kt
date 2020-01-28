package com.raywenderlich.android.creaturemon.allcreatures

import com.raywenderlich.android.creaturemon.data.model.Creature
import com.raywenderlich.android.creaturemon.mvibase.MviViewState

data class AllCreaturesViewState(val isLoading: Boolean,
								 val creatures: List<Creature>,
								 val error: Throwable?) : MviViewState {

	companion object {
		fun idle(): MviViewState = AllCreaturesViewState(isLoading = false,
				creatures = emptyList(), error = null)
	}

}