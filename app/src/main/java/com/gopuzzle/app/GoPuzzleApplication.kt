package com.gopuzzle.app

import android.app.Application
import com.gopuzzle.app.data.repository.PuzzleRepository

class GoPuzzleApplication : Application() {
    lateinit var puzzleRepository: PuzzleRepository
        private set

    override fun onCreate() {
        super.onCreate()
        puzzleRepository = PuzzleRepository(this)
    }
}
