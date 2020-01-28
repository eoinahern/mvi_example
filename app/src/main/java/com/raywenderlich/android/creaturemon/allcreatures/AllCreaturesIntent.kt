package com.raywenderlich.android.creaturemon.allcreatures

import com.raywenderlich.android.creaturemon.mvibase.MviIntent


sealed class AllCreaturesIntent : MviIntent {

	object LoadAllCreatures : AllCreaturesIntent()
	object ClearAllCreatures : AllCreaturesIntent()

}