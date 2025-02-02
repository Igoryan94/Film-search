package com.igoryan94.remote_module

interface RemoteProvider {
    fun provideRemote(): TmdbApi
}