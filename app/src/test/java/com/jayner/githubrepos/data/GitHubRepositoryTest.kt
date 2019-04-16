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
        repository.scheduler = Schedulers.trampoline() // queues work on the main thread while the test is running, so test waits for it to complete before performing assertions.
    }

    /**
     * The setup above ensures that the values in trending_repos.json is returned for the apiService.getTrendingRepos API call.
     * 1. Test that those values are the same ones returned by the repository.
     * 2. Create a different list and have the API call return that on subsequent calls. Test that the cached list is used and not the newly mocked API list.
     */
    @Test
    @Throws(Exception::class)
    fun getTrendingRepos_ApiThenCache() {
        // Test
        // 1. The values from the API call are returned
        repository.getTrendingRepos().test().assertValue(trendingReposResponse.items)

        // 2. Setup
        val modifiedList = ArrayList(trendingReposResponse.items)
        modifiedList.removeAt(0)
        `when`(mockGitHubApiService.getTrendingRepos()).thenReturn(Single.just<List<Repo>>(modifiedList))

        // 2. Test: Verify we still get the cached list rather than the different API response
        repository.getTrendingRepos().test().assertValue(trendingReposResponse.items)
    }

    /**
     * Load trending repos to start to mimic the initial state the app is in before we look up a repo.
     * To ensure that the mocked api call is/is not called, set it up to return a specific value to look for.
     * 1. Test to see that cache is used to retrieve the "kotlin" repo.
     * 2. Test to see that we perform an api call if the requested repo is not in the cache.
     */
    @Test
    @Throws(Exception::class)
    fun getRepo_AllFromCache() {
        // Set up
        repository.getTrendingRepos().subscribe()
        `when`(mockGitHubApiService.getRepo(anyString(), anyString())).thenReturn(Single.just<Repo>(shadowsocksAndroidRepo))

        // Test
        // 1. Verify we still get the kotlin repo, which is cached from our trending repos call above
        repository.getRepo("JetBrains", "kotlin").test().assertValue(kotlinRepo)

        // 2. Fetch a repo that would not be in the cache and verify the API result is returned
        repository.getRepo("NotInCache", "NotInCache").test().assertValue(shadowsocksAndroidRepo) // this is the mocked api call return value from above
    }
}