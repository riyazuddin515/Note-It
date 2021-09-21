package com.riyazuddin.noteit.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.riyazuddin.noteit.NoteItApplication
import com.riyazuddin.noteit.common.Constants.BASE_URL
import com.riyazuddin.noteit.common.Constants.NOTES_DB_NAME
import com.riyazuddin.noteit.data.local.NoteDao
import com.riyazuddin.noteit.data.local.NotesDB
import com.riyazuddin.noteit.data.remote.NoteItApi
import com.riyazuddin.noteit.data.repository.AuthRepositoryImp
import com.riyazuddin.noteit.data.repository.NotesRepositoryImp
import com.riyazuddin.noteit.domain.repository.IAuthRepository
import com.riyazuddin.noteit.domain.repository.INoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplication(
        @ApplicationContext context: Context
    ): NoteItApplication = context as NoteItApplication

    @Provides
    @Singleton
    fun provideNoteItApi(): NoteItApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NoteItApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(noteItApi: NoteItApi): IAuthRepository {
        return AuthRepositoryImp(noteItApi)
    }

    @Provides
    @Singleton
    fun provideNoteDB(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, NotesDB::class.java, NOTES_DB_NAME).build()

    @Provides
    @Singleton
    fun provideNoteDao(
        notesDB: NotesDB
    ) = notesDB.notedDao()

    @Provides
    @Singleton
    fun provideNoteRepository(
        noteDao: NoteDao,
        noteItApi: NoteItApi,
        @ApplicationContext context: Context
    ): INoteRepository = NotesRepositoryImp(noteDao, noteItApi, context)
}
