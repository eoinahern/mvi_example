package com.raywenderlich.android.creaturemon.addcreature

import com.raywenderlich.android.creaturemon.data.model.CreatureGenerator
import com.raywenderlich.android.creaturemon.data.repository.CreatureRepository
import com.raywenderlich.android.creaturemon.util.schedulers.SchedulerProvider
import io.reactivex.ObservableTransformer
import com.raywenderlich.android.creaturemon.addcreature.AddCreatureAction.*
import com.raywenderlich.android.creaturemon.addcreature.AddCreatureResult.*
import com.raywenderlich.android.creaturemon.data.model.AttributeStore
import com.raywenderlich.android.creaturemon.data.model.CreatureAttributes
import io.reactivex.Observable
import java.lang.IllegalArgumentException


class AddCreatureProcessorHolder(private val creatureRepo: CreatureRepository,
								 private val creatureGenerator: CreatureGenerator,
								 private val schedulerProvider: SchedulerProvider) {

	private val avatarProcessor = ObservableTransformer<AvatarAction, AvatarResult> { actions ->
		actions.map { avatar ->
			AvatarResult.Success(avatar.drawable)
		}.cast(AvatarResult::class.java).onErrorReturn(AvatarResult::Failure)
				.subscribeOn(schedulerProvider.io())
				.observeOn(schedulerProvider.ui())
				.startWith(AvatarResult.Processing)

	}

	private val nameProcessor = ObservableTransformer<NameAction, NameResult> { action ->
		action.map { NameResult.Success(it.name) }
				.cast(NameResult::class.java)
				.onErrorReturn(NameResult::Failure)
				.subscribeOn(schedulerProvider.io())
				.observeOn(schedulerProvider.ui())
				.startWith(NameResult.Processing)
	}

	private val intelligenceProcessor = ObservableTransformer<IntelligenceAction, IntelligenceResult> { action ->
		action.map { IntelligenceResult.Success(AttributeStore.INTELLIGENCE[it.intelligenceIndex].value) }
				.cast(IntelligenceResult::class.java)
				.onErrorReturn(IntelligenceResult::Failure)
				.subscribeOn(schedulerProvider.io())
				.observeOn(schedulerProvider.ui())
				.startWith(IntelligenceResult.Processing)
	}

	private val strengthProcessor = ObservableTransformer<StrengthAction, StrengthResult> { action ->
		action.map { StrengthResult.Success(AttributeStore.STRENGTH[it.strengthIndex].value) }
				.cast(StrengthResult::class.java)
				.onErrorReturn(StrengthResult::Failure)
				.subscribeOn(schedulerProvider.io())
				.observeOn(schedulerProvider.ui())
				.startWith(StrengthResult.Processing)
	}

	private val enduranceProcessor = ObservableTransformer<EnduranceAction, EnduranceResult> { action ->
		action.map { EnduranceResult.Success(AttributeStore.ENDURANCE[it.enduranceIndex].value) }
				.cast(EnduranceResult::class.java)
				.onErrorReturn(EnduranceResult::Failure)
				.subscribeOn(schedulerProvider.io())
				.observeOn(schedulerProvider.ui())
				.startWith(EnduranceResult.Processing)
	}

	private val saveProcessor = ObservableTransformer<SaveAction, SaveResult> { actions ->
		actions.flatMap {

			val attributes = CreatureAttributes(
					AttributeStore.INTELLIGENCE[it.intellingenceIndex].value,
					AttributeStore.STRENGTH[it.strengthIndex].value,
					AttributeStore.ENDURANCE[it.enduranceIndex].value
			)

			val creature = creatureGenerator.generateCreature(attributes, it.name, it.drawable)
			creatureRepo.saveCreature(creature)
					.map { SaveResult.Success }
					.cast(SaveResult::class.java)
					.onErrorReturn(SaveResult::Failure)
					.subscribeOn(schedulerProvider.io())
					.observeOn(schedulerProvider.ui())
					.startWith(SaveResult.Processing)
		}
	}

	internal var actionProcessor = ObservableTransformer<AddCreatureAction, AddCreatureResult> { actions ->
		actions.publish { shared ->
			Observable.merge(
					shared.ofType(AvatarAction::class.java).compose(avatarProcessor),
					shared.ofType(NameAction::class.java).compose(nameProcessor),
					shared.ofType(IntelligenceAction::class.java).compose(intelligenceProcessor),
					shared.ofType(StrengthAction::class.java).compose(strengthProcessor))
					.mergeWith(shared.ofType(EnduranceAction::class.java).compose(enduranceProcessor))
					.mergeWith(shared.ofType(SaveAction::class.java).compose(saveProcessor))
					.mergeWith(shared.filter { v ->
						v !is AvatarAction &&
								v !is NameAction &&
								v !is SaveAction &&
								v !is IntelligenceAction &&
								v !is StrengthAction &&
								v !is SaveAction &&
								v !is EnduranceAction
					}.flatMap {
						Observable.error<AddCreatureResult>(
								IllegalArgumentException("ah balls")
						)
					})
		}
	}

}