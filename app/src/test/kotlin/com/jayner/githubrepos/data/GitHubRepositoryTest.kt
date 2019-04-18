package com.jayner.githubrepos.data

import com.google.gson.reflect.TypeToken
import com.jayner.githubrepos.model.Contributor
import com.jayner.githubrepos.model.Repo
import com.jayner.githubrepos.test.RxSchedulerRule
import com.jayner.githubrepos.utils.TestUtils
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class GitHubRepositoryTest {

    @get:Rule
    val rxSchedulerRule = RxSchedulerRule()

    @Mock lateinit var mockGitHubApiService: GitHubApiService

    private lateinit var repository: GitHubRepository
    private lateinit var trendingReposResponse: TrendingReposResponse
    private lateinit var kotlinRepo: Repo
    private lateinit var shadowsocksAndroidRepo: Repo

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        trendingReposResponse = TestUtils.loadJson("mock/trending_repos.json", TrendingReposResponse::class.java)
        `when`(mockGitHubApiService.getTrendingRepos()).thenReturn(Single.just(trendingReposResponse.items))

        kotlinRepo = trendingReposResponse.items.get(0)
        shadowsocksAndroidRepo = trendingReposResponse.items.get(1)

        repository = GitHubRepository()
        repository.gitHubApiService = mockGitHubApiService
    }

    /**
     * The setup above ensures that the values in trending_repos.json is returned for the apiService.getTrendingRepos API call.
     * 1. Test that those values are the same ones returned by the repository.
     * 2. Create a different list and have the API call return that on subsequent calls. Test that the cached list is used and not the newly mocked API list.
     */
    @Test
    fun testGetTrendingRepos_ApiThenCache() {
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
    fun testGetRepo() {
        // Set up
        repository.getTrendingRepos().subscribe()
        `when`(mockGitHubApiService.getRepo(anyString(), anyString())).thenReturn(Single.just<Repo>(shadowsocksAndroidRepo))

        // Test
        // 1. Verify we still get the kotlin repo, which is cached from our trending repos call above
        repository.getRepo("JetBrains", "kotlin").test().assertValue(kotlinRepo)

        // 2. Fetch a repo that would not be in the cache and verify the API result is returned
        repository.getRepo("NotInCache", "NotInCache").test().assertValue(shadowsocksAndroidRepo) // this is the mocked api call return value from above
    }

    /**
     * The setup ensures that the values in contributors.json is returned for the apiService.getContributors API call.
     * 1. Test that those values are the same ones returned by the repository.
     * 2. Create a different list and have the API call return that on subsequent calls. Test that the cached list is used and not the newly mocked API list.
     */
    @Test
    fun testGetContributors_ApiThenCache() {
        // 1. Set up
        val listType = object : TypeToken<ArrayList<Contributor>>(){}.type
        var contributorsList: List<Contributor> = TestUtils.loadJson("mock/contributors.json", listType)

        `when`(mockGitHubApiService.getContributors(kotlinRepo.contributorsUrl)).thenReturn(Single.just(contributorsList))

        // 1. Test
        // The values from the API call are returned
        repository.getContributors(kotlinRepo.contributorsUrl).test().assertValue(contributorsList)

        // 2. Setup
        val modifiedList = ArrayList(contributorsList)
        modifiedList.removeAt(0)
        `when`(mockGitHubApiService.getContributors(kotlinRepo.contributorsUrl)).thenReturn(Single.just(modifiedList))

        // 2. Test: Verify we still get the cached list rather than the different API response
        repository.getContributors(kotlinRepo.contributorsUrl).test().assertValue(contributorsList)
    }

    /**
     * The setup above ensures that the values in trending_repos.json are returned for the apiService.getTrendingRepos
     * API call.
     * 1. Populate the trendingRepos cache, check the values are there.
     * 2. Clear the cache.
     * 3. Create a different list and have the API call return that on subsequent calls. Test that the API is then
     * called and the newly mocked API list is returned.
     */
    @Test
    fun testClearCache_trendingRepos() {
        // Set up
        repository.getTrendingRepos().test().assertValue(trendingReposResponse.items)
        repository.clearCache()

        val modifiedList = ArrayList(trendingReposResponse.items)
        modifiedList.removeAt(0)
        `when`(mockGitHubApiService.getTrendingRepos()).thenReturn(Single.just<List<Repo>>(modifiedList))

        // Test
        repository.getTrendingRepos().test().assertValue(modifiedList)
    }

    /**
     * The setup ensures that the values in contributors.json are returned for the apiService.getContributors
     * API call.
     * 1. Populate the contributors cache, check the values are there.
     * 2. Clear the cache.
     * 3. Create a different list and have the API call return that on subsequent calls. Test that the API is then
     * called and the newly mocked API list is returned.
     */
    @Test
    fun testClearCache_contributors() {
        // Set up
        val listType = object : TypeToken<ArrayList<Contributor>>(){}.type
        var contributorsList: List<Contributor> = TestUtils.loadJson("mock/contributors.json", listType)
        `when`(mockGitHubApiService.getContributors(kotlinRepo.contributorsUrl)).thenReturn(Single.just(contributorsList))

        repository.getContributors(kotlinRepo.contributorsUrl).test().assertValue(contributorsList)
        repository.clearCache()

        val modifiedList = ArrayList(contributorsList)
        modifiedList.removeAt(0)
        `when`(mockGitHubApiService.getContributors(kotlinRepo.contributorsUrl)).thenReturn(Single.just(modifiedList))

        //Test
        repository.getContributors(kotlinRepo.contributorsUrl).test().assertValue(modifiedList)
    }
}