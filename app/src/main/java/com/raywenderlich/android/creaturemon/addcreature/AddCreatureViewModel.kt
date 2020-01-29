package com.raywenderlich.android.creaturemon.addcreature

import androidx.lifecycle.ViewModel
import com.raywenderlich.android.creaturemon.data.model.CreatureGenerator
import com.raywenderlich.android.creaturemon.mvibase.MviViewModel
import com.raywenderlich.android.creaturemon.addcreature.AddCreatureResult.*
import com.raywenderlich.android.creaturemon.data.model.CreatureAttributes
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.function.BiFunction

class AddCreatureViewModel() : ViewModel(), MviViewModel<AddCreatureIntent, AddCreatureViewState> {


	private val intentsSubject: PublishSubject<AddCreatureIntent> = PublishSubject.create()
	private val statesObservable: Observable<AddCreatureViewState> = compose()


	override fun processIntents(intents: Observable<AddCreatureIntent>) {

	}

	override fun states(): Observable<AddCreatureViewState> {

	}


	fun compose(): Observable<AddCreatureViewState> {

	}

	companion object {
		val generator = CreatureGenerator()

		private val reducer = BiFunction { viewState: AddCreatureViewState, result: AddCreatureResult ->
			when (result) {
				is AvatarResult -> reduceAvatar(viewState, result)
				is NameResult -> reduceName(viewState, result)
				is IntelligenceResult -> reduceIntelligence(viewState, result)
				is StrengthResult -> reduceStrength(viewState, result)
				is EnduranceResult -> reduceEndurance(viewState, result)
				is SaveResult -> reducerSave(viewState, result)
			}
		}

		private fun reduceAvatar(previousState: AddCreatureViewState,
								 result: AvatarResult): AddCreatureViewState {

			return when (result) {
				is AvatarResult.Success -> {
					previousState.copy(
							isProcessing = false,
							error = null,
							creature = generator.generateCreature(previousState.creature.attributes,
									previousState.creature.name, result.drawable),
							isDrawableSelected = (result.drawable != 0))
				}
				is AvatarResult.Failure -> {
					previousState.copy(isProcessing = false, error = result.error)
				}
				is AvatarResult.Processing -> {
					previousState.copy(isProcessing = true, error = null)
				}
			}
		}

		private fun reduceName(previousState: AddCreatureViewState, result: NameResult): AddCreatureViewState {

			return when (result) {
				is NameResult.Success -> {
					previousState.copy(isProcessing = false, error = null,
							creature = generator.generateCreature(previousState.creature.attributes, result.name,
									previousState.creature.drawable))
				}
				is NameResult.Failure -> {
					previousState.copy(isProcessing = false, error = result.error)
				}
				is NameResult.Processing -> {
					previousState.copy(isProcessing = true, error = null)
				}
			}
		}


		private fun reduceIntelligence(previousState: AddCreatureViewState, result: IntelligenceResult): AddCreatureViewState {

			return when (result) {
				is IntelligenceResult.Success -> {
					val attributes = CreatureAttributes(
							result.intelligence,
							previousState.creature.attributes.strength,
							previousState.creature.attributes.endurance)

					previousState.copy(isProcessing = false,
							error = null,
							creature = generator.generateCreature(attributes,
									name = previousState.creature.name,
									drawable = previousState.creature.drawable))
				}
				is IntelligenceResult.Failure -> {
					previousState.copy(isProcessing = false, error = result.error)
				}
				is IntelligenceResult.Processing -> {
					previousState.copy(isProcessing = true, error = null)
				}
			}
		}

		private fun reduceEndurance(previousState: AddCreatureViewState, result: EnduranceResult): AddCreatureViewState {
			return when (result) {
				is EnduranceResult.Success -> {
					val attributes = CreatureAttributes(
							previousState.creature.attributes.intelligence,
							previousState.creature.attributes.strength,
							result.endurance)

					previousState.copy(isProcessing = false,
							error = null,
							creature = generator.generateCreature(attributes,
									name = previousState.creature.name,
									drawable = previousState.creature.drawable))
				}
				is EnduranceResult.Failure -> {
					previousState.copy(isProcessing = false, error = result.error)
				}
				is EnduranceResult.Processing -> {
					previousState.copy(isProcessing = true, error = null)
				}
			}
		}

		private fun reduceStrength(previousState: AddCreatureViewState, result: StrengthResult): AddCreatureViewState {
			return when (result) {
				is StrengthResult.Success -> {
					val attributes = CreatureAttributes(
							previousState.creature.attributes.intelligence,
							result.strength,
							previousState.creature.attributes.endurance)

					previousState.copy(isProcessing = false,
							error = null,
							creature = generator.generateCreature(attributes,
									name = previousState.creature.name,
									drawable = previousState.creature.drawable))
				}
				is StrengthResult.Failure -> {
					previousState.copy(isProcessing = false, error = result.error)
				}
				is StrengthResult.Processing -> {
					previousState.copy(isProcessing = true, error = null)
				}

			}
		}

		private fun reducerSave(previousState: AddCreatureViewState, result: SaveResult): AddCreatureViewState {
			return when (result) {
				is SaveResult.Success -> {
					previousState.copy(isProcessing = false, isSaveComplete = true, error = null)
				}
				is SaveResult.Failure -> {
					previousState.copy(isProcessing = false, error = result.error)
				}
				is SaveResult.Processing -> {
					previousState.copy(isProcessing = true, error = null)
				}
			}
		}
	}


}