package com.raywenderlich.android.creaturemon.allcreatures

import androidx.lifecycle.ViewModel
import com.raywenderlich.android.creaturemon.mvibase.MviViewModel
import io.reactivex.Observable
import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesResult.*
import com.raywenderlich.android.creaturemon.util.notOfType
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

class AllCreaturesViewModel(private val actionProcessorHolder: AllCreaturesProcessorHolder) : ViewModel(), MviViewModel<AllCreaturesIntent, AllCreaturesViewState> {


	private val intentsSubject: PublishSubject<AllCreaturesIntent> = PublishSubject.create()
	private val statesObservale: Observable<AllCreaturesViewState> = compose()
	private val loadingIntentFilter: ObservableTransformer<AllCreaturesIntent, AllCreaturesIntent>
		get() = ObservableTransformer { intents ->
			intents.publish { shared ->
				Observable.merge(shared.ofType(AllCreaturesIntent.LoadAllCreatures::class.java).take(1),
						shared.notOfType(AllCreaturesIntent.LoadAllCreatures::class.java))
			}
		}

	override fun processIntents(intents: Observable<AllCreaturesIntent>) {
		intents.subscribe(intentsSubject)
	}

	private fun compose(): Observable<AllCreaturesViewState> {
		return intentsSubject.compose(loadingIntentFilter)
				.map { intent ->
					createActionFromIntent(intent)
				}
				.compose(actionProcessorHolder.actionProcessor)
				.scan(AllCreaturesViewState.idle(), reducer)
				.distinctUntilChanged()
				.replay(1)
				.autoConnect(0)
	}

	private fun createActionFromIntent(intent: AllCreaturesIntent): AllCreaturesAction = when (intent) {
		is AllCreaturesIntent.LoadAllCreatures -> {
			AllCreaturesAction.LoadAllCreaturesAction
		}
		is AllCreaturesIntent.ClearAllCreatures -> AllCreaturesAction.ClearAllCreaturesAction
	}

	override fun states(): Observable<AllCreaturesViewState> = statesObservale

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