package com.raywenderlich.android.creaturemon.allcreatures

import com.raywenderlich.android.creaturemon.data.model.Creature
import com.raywenderlich.android.creaturemon.data.model.CreatureAttributes
import com.raywenderlich.android.creaturemon.data.model.CreatureGenerator
import com.raywenderlich.android.creaturemon.data.repository.CreatureRepository
import com.raywenderlich.android.creaturemon.util.schedulers.BaseSchedulerProvider
import com.raywenderlich.android.creaturemon.util.schedulers.ImmediateSchedulerProvider
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class AllCreaturesViewModelTest {


	@Mock
	private lateinit var mockRepo: CreatureRepository
	private lateinit var schedulerProvider: BaseSchedulerProvider
	private lateinit var generator: CreatureGenerator
	private lateinit var viewModel: AllCreaturesViewModel
	private lateinit var testObserver: TestObserver<AllCreaturesViewState>
	private lateinit var creatures: List<Creature>


	@Before
	fun setUp() {
		MockitoAnnotations.initMocks(this)
		schedulerProvider = ImmediateSchedulerProvider()
		generator = CreatureGenerator()
		viewModel = AllCreaturesViewModel(AllCreaturesProcessorHolder(mockRepo, schedulerProvider))


		creatures = listOf(
				generator.generateCreature(CreatureAttributes(3, 5, 7),
						"creature 1", 1),
				generator.generateCreature(CreatureAttributes(4, 5, 6),
						"creature 2", 2),
				generator.generateCreature(CreatureAttributes(1, 2, 3),
						"creature 3", 3)
		)


		testObserver = viewModel.states().test()

	}


	@Test
	fun getAllCreaturesTest() {
		`when`(mockRepo.getAllCreatures()).thenReturn(Observable.just(creatures))

		viewModel.processIntents(Observable.just(AllCreaturesIntent.LoadAllCreatures))

		testObserver.assertValueAt(1, AllCreaturesViewState::isLoading)
		testObserver.assertValueAt(2) { t: AllCreaturesViewState ->
			!t.isLoading
		}
	}

	@Test
	fun errorFromRepo() {
		`when`(mockRepo.getAllCreatures()).thenReturn(Observable.error(Exception()))
		viewModel.processIntents(Observable.just(AllCreaturesIntent.LoadAllCreatures))
		testObserver.assertValueAt(2) { state ->
			state.error != null
		}
	}
}