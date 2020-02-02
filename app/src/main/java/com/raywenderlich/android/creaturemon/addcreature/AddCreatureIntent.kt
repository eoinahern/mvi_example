package com.raywenderlich.android.creaturemon.addcreature

import com.raywenderlich.android.creaturemon.mvibase.MviIntent


sealed class AddCreatureIntent : MviIntent {

	data class AvatarIntent(val drawable: Int) : AddCreatureIntent()
	data class NameIntent(val name: String) : AddCreatureIntent()
	data class IntelligenceIntent(val intelligence: Int) : AddCreatureIntent()
	data class StrengthIntent(val strength: Int) : AddCreatureIntent()
	data class EnduranceIntent(val endurance: Int) : AddCreatureIntent()
	data class SaveIntent(val drawable: Int,
						  val name: String,
						  val intelligence: Int,
						  val strength: Int,
						  val endurance: Int) : AddCreatureIntent()
}