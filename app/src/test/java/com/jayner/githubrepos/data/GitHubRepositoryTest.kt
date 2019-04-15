package com.jayner.githubrepos.data

import com.jayner.githubrepos.model.Repo
import com.jayner.githubrepos.utils.TestUtils
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class GitHubRepositoryTest {

    @Mock lateinit var mockGitHubApiService: GitHubApiService

    private lateinit var repository: GitHubRepository
    private lateinit var trendingReposResponse: TrendingReposResponse
    private lateinit var kotlinRepo: Repo
    private lateinit var shadowsocksAndroidRepo: Repo

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        trendingReposResponse = TestUtils.loadJson("mock/trending_repos.json", TrendingReposResponse::class.java)
        `when`(mockGitHubApiService.getTrendingRepos()).thenReturn(Single.just(trendingReposResponse.items))

        kotlinRepo = trendingReposResponse.items.get(0)
        shadowsocksAndroidRepo = trendingReposResponse.items.get(1)

        repository = GitHubRepository()
        repository.gitHubApiService = mockGitHubApiService
        repository.scheduler = Schedulers.trampoline()
    }

    @Test
    @Throws(Exception::class)
    fun getTrendingRepos_ApiThenCache() {
        repository.getTrendingRepos().test().assertValue(trendingReposResponse.items)

//        verify(mockGitHubApiService).getTrendingRepos()

        // Create a different list and have the API call return that on subsequent calls
        val modifiedList = ArrayList(trendingReposResponse.items)
        modifiedList.removeAt(0)
        `when`(mockGitHubApiService.getTrendingRepos()).thenReturn(Single.just<List<Repo>>(modifiedList))

        // Verify we still get the cached list rather than the different API response
        repository.getTrendingRepos().test().assertValue(trendingReposResponse.items)

//        verifyZeroInteractions(mockGitHubApiService)
    }

    @Test
    @Throws(Exception::class)
    fun getRepo_AllFromCache() {
        // Load trending repos to mimic initial state of app
        repository.getTrendingRepos().subscribe()

//        verify(mockGitHubApiService).getTrendingRepos()

        // Change requester to return a different repo if ever invoked
        `when`(mockGitHubApiService.getRepo(anyString(), anyString())).thenReturn(Single.just<Repo>(shadowsocksAndroidRepo))

        // Verify we still get the RxJava repo, which is cached from our trending repos call above
        repository.getRepo("JetBrains", "kotlin").test().assertValue(kotlinRepo)

        // Fetch a repo that would not be in the cache and verify the API result is returned
        repository.getRepo("NotInCache", "NotInCache").test().assertValue(shadowsocksAndroidRepo)

//        verifyZeroInteractions(mockGitHubApiService)
    }
}