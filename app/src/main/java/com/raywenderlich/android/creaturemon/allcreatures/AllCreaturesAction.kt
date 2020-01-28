package com.raywenderlich.android.creaturemon.allcreatures

import com.raywenderlich.android.creaturemon.mvibase.MviAction

sealed class AllCreaturesAction : MviAction {
	object LoadCreaturesAction : AllCreaturesAction()
	object ClearAllCreaturesAction : AllCreaturesAction()
}