package com.raywenderlich.android.creaturemon.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raywenderlich.android.creaturemon.addcreature.AddCreatureProcessorHolder
import com.raywenderlich.android.creaturemon.addcreature.AddCreatureViewModel
import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesProcessorHolder
import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesViewModel
import com.raywenderlich.android.creaturemon.app.Injection
import java.lang.IllegalArgumentException

class CreaturemonViewModelFactory : ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel?> create(modelClass: Class<T>): T {
		if (modelClass == AllCreaturesViewModel::class.java) {
			return AllCreaturesViewModel(AllCreaturesProcessorHolder(
					Injection.provideCreatureRepository(),
					Injection.provideSchedulerProvider()
			)) as T
		}

		if (modelClass == AddCreatureViewModel::class.java) {
			return AddCreatureViewModel(
					AddCreatureProcessorHolder(
							Injection.provideCreatureRepository(),
							Injection.provideCreatureGenerator(),
							Injection.provideSchedulerProvider())
			) as T
		}

		throw IllegalArgumentException("no such class ${modelClass.canonicalName}")
	}


	// not using this. not sure how neccessary it is.
	//companion object : SingletonHolderSingleArg<CreaturemonViewModelFactory, Context>(::CreaturemonViewModelFactory)
}