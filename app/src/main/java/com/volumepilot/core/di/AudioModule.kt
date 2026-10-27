package com.volumepilot.core.di

import android.content.Context
import android.media.AudioManager
import com.volumepilot.service.volume.AndroidVolumeController
import com.volumepilot.service.volume.VolumeController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AudioModule {

    @Binds
    @Singleton
    abstract fun bindVolumeController(
        impl: AndroidVolumeController
    ): VolumeController

    companion object {
        @Provides
        @Singleton
        fun provideAudioManager(
            @ApplicationContext context: Context
        ): AudioManager {
            return context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        }
    }
}
