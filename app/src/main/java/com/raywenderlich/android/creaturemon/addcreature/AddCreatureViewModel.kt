package com.raywenderlich.android.creaturemon.addcreature

import androidx.lifecycle.ViewModel
import com.raywenderlich.android.creaturemon.data.model.CreatureGenerator
import com.raywenderlich.android.creaturemon.mvibase.MviViewModel
import com.raywenderlich.android.creaturemon.addcreature.AddCreatureResult.*
import io.reactivex.Observable

class AddCreatureViewModel() : ViewModel(), MviViewModel<AddCreatureIntent, AddCreatureViewState> {


	override fun processIntents(intents: Observable<AddCreatureIntent>) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun states(): Observable<AddCreatureViewState> {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	companion object {
		val generator = CreatureGenerator()

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

			return when(result){
				is IntelligenceResult.Success -> {
					previousState.copy(isProcessing = false, error = null,
							creature = generator.generateCreature(previousState.creature.attributes,
									previousState.creature.name, ))
				}
			}
		}
	}


}