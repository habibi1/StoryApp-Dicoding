package com.habibi.core.domain.story

import androidx.paging.PagingData
import com.habibi.core.data.Resource
import com.habibi.core.data.source.local.entity.StoriesEntity
import com.habibi.core.domain.repository.IStoryRepository
import com.habibi.core.domain.repository.IUserSessionRepository
import com.habibi.core.domain.story.data.StoryItem
import com.habibi.core.domain.story.usecase.IStoryUseCase
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class StoryInteractor @Inject constructor(
    private val storyRepository: IStoryRepository,
    private val userSessionRepository: IUserSessionRepository
): IStoryUseCase {

    override suspend fun setUserLogout() {
        userSessionRepository.setUserLogout()
    }

    override suspend fun getUserName(): String =
        userSessionRepository.getUserName()

    override suspend fun getListStoryWithLocation(): Resource<List<StoryItem>> =
        storyRepository.getListStoryWithLocation()

    override suspend fun postNewStory(
        photoFile: File,
        description: String,
        latitude: Float?,
        longitude: Float?
    ): Resource<Unit> {
        return storyRepository.postNewStory(photoFile, description, latitude, longitude)
    }

    override fun getStoryPaging(): Flow<PagingData<StoriesEntity>> {
        return storyRepository.getStoryPaging()
    }

}