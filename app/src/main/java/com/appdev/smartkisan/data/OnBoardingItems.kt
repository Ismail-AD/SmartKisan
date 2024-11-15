package com.appdev.smartkisan.data

import com.appdev.smartkisan.R

class OnBoardingItems(
    val image: Int,
    val title: Int,
    val desc: Int
) {
    companion object{
        fun getData(): List<OnBoardingItems> {
            return listOf(
                OnBoardingItems(R.drawable.ic_launcher_background, R.string.intro_title_1, R.string.intro_desc_1),
                OnBoardingItems(R.drawable.ic_launcher_background, R.string.intro_title_2, R.string.intro_desc_2),
                OnBoardingItems(R.drawable.ic_launcher_background, R.string.intro_title_3, R.string.intro_desc_3)
            )
        }
    }
}