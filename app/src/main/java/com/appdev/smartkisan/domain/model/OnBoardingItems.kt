package com.appdev.smartkisan.domain.model

import com.appdev.smartkisan.R

class OnBoardingItems(
    val animationId: Int,
    val title: Int,
    val desc: Int
) {
    companion object{
        fun getData(): List<OnBoardingItems> {
            return listOf(
                OnBoardingItems(R.raw.newsanimation, R.string.intro_title_1, R.string.intro_desc_1),
                OnBoardingItems(R.raw.newplantanimation, R.string.intro_title_2, R.string.intro_desc_2),
                OnBoardingItems(R.raw.chatbot, R.string.intro_title_3, R.string.intro_desc_3)
            )
        }
    }
}