package com.raywenderlich.android.creaturemon.allcreatures

import androidx.lifecycle.ViewModel
import com.raywenderlich.android.creaturemon.mvibase.MviViewModel
import com.raywenderlich.android.creaturemon.mvibase.MviViewState
import io.reactivex.Observable
import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesResult.*
import java.util.function.BiFunction

class AllCreaturesViewModel(val actionProcessorHolder: AllCreaturesProcessorHolder) : ViewModel(), MviViewModel<AllCreaturesIntent, AllCreaturesViewState> {


	override fun processIntents(intents: Observable<AllCreaturesIntent>) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun states(): Observable<AllCreaturesViewState> {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}


	companion object {
		private val reducer = BiFunction { previousViewState: AllCreaturesViewState, result: AllCreaturesResult ->
			when (result) {
				is LoadAllCreaturesResult -> when (result) {
					is LoadAllCreaturesResult.Success -> previousViewState.copy(isLoading = false, creatures = result.creatures)
					is LoadAllCreaturesResult.Failure -> previousViewState.copy(isLoading = false, error = result.error)
					is LoadAllCreaturesResult.Loading -> previousViewState.copy(isLoading = true)
				}

				is ClearAllCreaturesResult -> when (result) {
					is ClearAllCreaturesResult.Success -> previousViewState.copy(isLoading = false, creatures = emptyList())
					is ClearAllCreaturesResult.Failure -> previousViewState.copy(isLoading = false, error = result.error)
					is ClearAllCreaturesResult.Clearing -> previousViewState.copy(isLoading = true)
				}
			}
		}

	}

}