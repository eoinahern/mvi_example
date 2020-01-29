package com.raywenderlich.android.creaturemon.allcreatures

import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesAction.*
import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesResult.LoadAllCreaturesResult
import com.raywenderlich.android.creaturemon.data.repository.CreatureRepository
import com.raywenderlich.android.creaturemon.util.schedulers.BaseSchedulerProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import java.lang.IllegalArgumentException

class AllCreaturesProcessorHolder(private val creatureRepository: CreatureRepository,
								  private val schedulerProvider: BaseSchedulerProvider) {


	private val loadAllCreaturesProcessor: ObservableTransformer<LoadAllCreaturesAction,
			LoadAllCreaturesResult> = ObservableTransformer {
		it.flatMap {
			creatureRepository.getAllCreatures()
					.map { creatures -> LoadAllCreaturesResult.Success(creatures) }
					.cast(LoadAllCreaturesResult::class.java)
					.onErrorReturn(LoadAllCreaturesResult::Failure)
					.subscribeOn(schedulerProvider.io())
					.observeOn(schedulerProvider.ui())
					.startWith(LoadAllCreaturesResult.Loading)

		}
	}

	private val clearAllCreaturesProcessor:
			ObservableTransformer<ClearAllCreaturesAction, AllCreaturesResult.ClearAllCreaturesResult> =
			ObservableTransformer {
				it.flatMap {
					creatureRepository.clearAllCreatures()
							.map {
								AllCreaturesResult.ClearAllCreaturesResult.Success
							}
							.cast(AllCreaturesResult.ClearAllCreaturesResult::class.java)
							.onErrorReturn(AllCreaturesResult.ClearAllCreaturesResult::Failure)
							.subscribeOn(schedulerProvider.io())
							.observeOn(schedulerProvider.ui())
							.startWith(AllCreaturesResult.ClearAllCreaturesResult.Clearing)
				}
			}

	internal var actionProcessor = ObservableTransformer<AllCreaturesAction, AllCreaturesResult> { action ->
		action.publish { shared ->
			Observable.merge(
					shared.ofType(LoadAllCreaturesAction::class.java).compose(loadAllCreaturesProcessor),
					shared.ofType(ClearAllCreaturesAction::class.java).compose(clearAllCreaturesProcessor)
			).mergeWith(shared.filter { v ->
				v !is LoadAllCreaturesAction || v !is ClearAllCreaturesAction
			}.flatMap { m -> Observable.error<AllCreaturesResult>(IllegalArgumentException("gay creature res $m")) })
		}
	}
}

