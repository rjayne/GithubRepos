package com.jayner.githubrepos.data

import com.google.gson.reflect.TypeToken
import com.jayner.githubrepos.model.Contributor
import com.jayner.githubrepos.model.Repo
import com.jayner.githubrepos.model.TrendingReposResponse
import com.jayner.githubrepos.utils.TestUtils
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class GitHubApiServiceTest {

    @Mock
    lateinit var mockRestServiceAccessor: RestServiceAccessor

    @Mock
    lateinit var mockGitHubRestClient: GitHubRestClient

    private lateinit var gitHubApiService: GitHubApiService
    private lateinit var trendingReposResponse: TrendingReposResponse
    private lateinit var kotlinRepo: Repo

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(mockRestServiceAccessor.gitHubRestClient).thenReturn(mockGitHubRestClient)

        trendingReposResponse = TestUtils.loadJson("mock/trending_repos.json", TrendingReposResponse::class.java)
        kotlinRepo = trendingReposResponse.items.get(0)

        gitHubApiService = GitHubApiService()
        gitHubApiService.restServiceAccessor = mockRestServiceAccessor // inject our mock
        gitHubApiService.initAfterInject() // Would normally be called by AndroidAnnotations after Injection performed
    }

    @Test
    fun testGetTrendingRepos() {
        // Setup
        Mockito.`when`(mockGitHubRestClient.getTrendingRepos()).thenReturn(Single.just(trendingReposResponse))

        // Test
        gitHubApiService.getTrendingRepos().test().assertValue(trendingReposResponse.items)
    }

    @Test
    fun testGetRepo() {
        // Setup
        Mockito.`when`(mockGitHubRestClient.getRepo(kotlinRepo.owner.login, kotlinRepo.name)).thenReturn(Single.just(kotlinRepo))

        // Test
        gitHubApiService.getRepo(kotlinRepo.owner.login, kotlinRepo.name).test().assertValue(kotlinRepo)
    }

    @Test
    fun testGetContributors() {
        // Setup
        val listType = object : TypeToken<ArrayList<Contributor>>(){}.type
        var contributorsList: List<Contributor> = TestUtils.loadJson("mock/contributors.json", listType)

        Mockito.`when`(mockGitHubRestClient.getContributors(kotlinRepo.contributorsUrl+GitHubApiService.PARAM_NON_ANON_USERS)).thenReturn(Single.just(contributorsList))

        // Test
        gitHubApiService.getContributors(kotlinRepo.contributorsUrl).test().assertValue(contributorsList)
    }
}