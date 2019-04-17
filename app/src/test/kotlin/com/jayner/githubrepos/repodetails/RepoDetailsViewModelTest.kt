package com.jayner.githubrepos.repodetails

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.reflect.TypeToken
import com.jayner.githubrepos.data.GitHubRepository
import com.jayner.githubrepos.data.TrendingReposResponse
import com.jayner.githubrepos.model.Contributor
import com.jayner.githubrepos.model.Repo
import com.jayner.githubrepos.test.RxSchedulerRule
import com.jayner.githubrepos.trending.TrendingReposViewModel
import com.jayner.githubrepos.utils.TestUtils
import com.jraska.livedata.test
import io.reactivex.Single
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.internal.matchers.Any
import org.mockito.junit.MockitoJUnit

class RepoDetailsViewModelTest {
    @get:Rule
    val mockitoRule = MockitoJUnit.rule()

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val rxSchedulerRule = RxSchedulerRule()

    @Mock
    lateinit var mockGitHubRepository: GitHubRepository

    @Mock
    lateinit var mockBundle: Bundle

    private lateinit var viewModel: RepoDetailsViewModel
    private lateinit var contributorsList: List<Contributor>
    private lateinit var repo: Repo

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        val listType = object : TypeToken<ArrayList<Contributor>>(){}.type
        contributorsList = TestUtils.loadJson("mock/contributors.json", listType)
        repo = TestUtils.loadJson("mock/repo.json", Repo::class.java)

        viewModel = RepoDetailsViewModel(mockGitHubRepository)
    }

    /**
     * Asserting that the LiveData values have no value at the start (before repo has been set).
     */
    @Test
    fun testNull() {
        // Test
        viewModel.getRepo().test().assertNoValue()
        viewModel.hasError().test().assertNoValue()
        viewModel.isLoading().test().assertNoValue()
        viewModel.getContributors().test().assertNoValue()
        viewModel.hasContributorsError().test().assertNoValue()
        viewModel.isContributorsLoading().test().assertNoValue()

        Mockito.verify(mockGitHubRepository, Mockito.never()).getRepo(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
        Mockito.verify(mockGitHubRepository, Mockito.never()).getContributors(ArgumentMatchers.anyString())
    }

    /**
     * When we observe after setting the repo on the viewModel we only get notified about the last values set.
     */
    @Test
    fun testObserveAfterSetRepo() {
        // Setup
        Mockito.`when`(mockGitHubRepository.getContributors(repo.contributorsUrl)).thenReturn(Single.just(contributorsList))

        // Test
        viewModel.setRepo(repo)

        var loadingObserver = viewModel.isLoading().test()
        var repoObserver = viewModel.getRepo().test()
        var errorObserver = viewModel.hasError().test()
        var contributorsObserver = viewModel.getContributors().test()
        var contributorsErrorObserver = viewModel.hasContributorsError().test()
        var contributorsLoadingObserver = viewModel.isContributorsLoading().test()

        repoObserver.assertHasValue().assertHistorySize(1).assertValue(repo)
        contributorsObserver.assertHasValue().assertHistorySize(1).assertValue(contributorsList)
        contributorsLoadingObserver.assertHasValue().assertHistorySize(1).assertValue(false)
        contributorsErrorObserver.assertHasValue().assertHistorySize(1).assertValue(false)

        loadingObserver.assertNoValue()
        errorObserver.assertNoValue()

        Mockito.verify(mockGitHubRepository, Mockito.only()).getContributors(repo.contributorsUrl)
    }

    /**
     * When we observe before setting the repo on the viewModel, we get notified about the liveData changes during the
     * process.
     */
    @Test
    fun testObserveBeforeSetRepo() {
        // Setup
        Mockito.`when`(mockGitHubRepository.getContributors(repo.contributorsUrl)).thenReturn(Single.just(contributorsList))

        var loadingObserver = viewModel.isLoading().test()
        var repoObserver = viewModel.getRepo().test()
        var errorObserver = viewModel.hasError().test()
        var contributorsObserver = viewModel.getContributors().test()
        var contributorsErrorObserver = viewModel.hasContributorsError().test()
        var contributorsLoadingObserver = viewModel.isContributorsLoading().test()

        // Test
        viewModel.setRepo(repo)

        repoObserver.assertHasValue().assertHistorySize(1).assertValue(repo)
        contributorsObserver.assertHasValue().assertHistorySize(2).assertValueHistory(emptyList(), contributorsList).assertValue(contributorsList)
        contributorsLoadingObserver.assertHasValue().assertHistorySize(2).assertValueHistory(true, false).assertValue(false)
        contributorsErrorObserver.assertHasValue().assertHistorySize(2).assertValueHistory(false, false).assertValue(false)

        loadingObserver.assertNoValue()
        errorObserver.assertNoValue()

        Mockito.verify(mockGitHubRepository, Mockito.only()).getContributors(repo.contributorsUrl)
    }

    /**
     * Test the liveData values when an error occurred during the loading of the contributors.
     */
    @Test
    fun testContributorsError() {
        // Setup
        Mockito.`when`(mockGitHubRepository.getContributors(repo.contributorsUrl)).thenReturn(Single.error(Exception("Test Exception")))

        var loadingObserver = viewModel.isLoading().test()
        var repoObserver = viewModel.getRepo().test()
        var errorObserver = viewModel.hasError().test()
        var contributorsObserver = viewModel.getContributors().test()
        var contributorsErrorObserver = viewModel.hasContributorsError().test()
        var contributorsLoadingObserver = viewModel.isContributorsLoading().test()

        // Test
        viewModel.setRepo(repo)

        repoObserver.assertHasValue().assertHistorySize(1).assertValue(repo)
        contributorsObserver.assertValue(emptyList())
        contributorsLoadingObserver.assertHasValue().assertHistorySize(2).assertValueHistory(true, false).assertValue(false)
        contributorsErrorObserver.assertHasValue().assertHistorySize(2).assertValueHistory(false, true).assertValue(true)

        loadingObserver.assertNoValue()
        errorObserver.assertNoValue()

        Mockito.verify(mockGitHubRepository, Mockito.only()).getContributors(repo.contributorsUrl)
    }

    /**
     * Ensure that the instance state is saved when a repo is set.
     */
    @Test
    fun testSaveInstanceState_RepoSet() {
        // Setup
        Mockito.`when`(mockGitHubRepository.getContributors(repo.contributorsUrl)).thenReturn(Single.just(contributorsList))

        viewModel.setRepo(repo)

        // Test
        viewModel.saveToBundle(mockBundle)

        Mockito.verify(mockBundle, Mockito.only()).putStringArray(ArgumentMatchers.anyString(), ArgumentMatchers.any())
    }

    /**
     * Ensure that the no instance state is saved when no repo is set.
     */
    @Test
    fun testSaveInstanceState_NoRepoSet() {
        // Test
        viewModel.saveToBundle(mockBundle)

        Mockito.verify(mockBundle, Mockito.never()).putStringArray(ArgumentMatchers.anyString(), ArgumentMatchers.any())
    }

    /**
     * After a successful fetch the repo from the server, fetch the contributors for that repo.
     */
    @Test
    fun testFetchRepo() {
        // Setup
        Mockito.`when`(mockGitHubRepository.getRepo(repo.owner.login, repo.name)).thenReturn(Single.just(repo))
        Mockito.`when`(mockGitHubRepository.getContributors(repo.contributorsUrl)).thenReturn(Single.just(contributorsList))

        var repoDetails = arrayOf(repo.owner.login, repo.name)

        var loadingObserver = viewModel.isLoading().test()
        var repoObserver = viewModel.getRepo().test()
        var errorObserver = viewModel.hasError().test()
        var contributorsObserver = viewModel.getContributors().test()
        var contributorsErrorObserver = viewModel.hasContributorsError().test()
        var contributorsLoadingObserver = viewModel.isContributorsLoading().test()

        // Test
        viewModel.fetchRepo(repoDetails)

        loadingObserver.assertHasValue().assertHistorySize(2).assertValueHistory(true, false).assertValue(false)
        errorObserver.assertHasValue().assertHistorySize(2).assertValueHistory(false, false).assertValue(false)
        repoObserver.assertHasValue().assertHistorySize(1).assertValue(repo)
        contributorsObserver.assertHasValue().assertHistorySize(1).assertValueHistory(contributorsList).assertValue(contributorsList)
        contributorsLoadingObserver.assertHasValue().assertHistorySize(2).assertValueHistory(true, false).assertValue(false)
        contributorsErrorObserver.assertHasValue().assertHistorySize(2).assertValueHistory(false, false).assertValue(false)

        Mockito.verify(mockGitHubRepository, Mockito.times(1)).getRepo(repo.owner.login, repo.name)
        Mockito.verify(mockGitHubRepository, Mockito.times(1)).getContributors(repo.contributorsUrl)
    }

    /**
     * When a repo is set, restoreState does nothing
     */
    @Test
    fun testRestoreInstanceState_ExistingRepo() {
        // Setup
        Mockito.`when`(mockBundle.getStringArray("repo_details")).thenReturn(arrayOf(repo.owner.login, repo.name))
        Mockito.`when`(mockGitHubRepository.getRepo(repo.owner.login, repo.name)).thenReturn(Single.just(repo))
        Mockito.`when`(mockGitHubRepository.getContributors(repo.contributorsUrl)).thenReturn(Single.just(contributorsList))

        viewModel.setRepo(repo)

        // Test
        viewModel.restoreFromBundle(mockBundle)

        Mockito.verify(mockBundle, Mockito.never()).getStringArray(ArgumentMatchers.anyString())

        // Assert no repo retrieved
        viewModel.hasError().test().assertNoValue()
        viewModel.isLoading().test().assertNoValue()

        Mockito.verify(mockGitHubRepository, Mockito.never()).getRepo(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
    }

    /**
     * When no repo is set AND Bundle does not contain repo details, restoreState does nothing
     */
    @Test
    fun testRestoreInstanceState_InvalidStateBundle() {
        // Setup
        Mockito.`when`(mockBundle.getStringArray("repo_details")).thenReturn(null)
        Mockito.`when`(mockGitHubRepository.getRepo(repo.owner.login, repo.name)).thenReturn(Single.just(repo))
        Mockito.`when`(mockGitHubRepository.getContributors(repo.contributorsUrl)).thenReturn(Single.just(contributorsList))

        // Test
        viewModel.restoreFromBundle(mockBundle)

        // Assert no repo retrieved
        viewModel.hasError().test().assertNoValue()
        viewModel.isLoading().test().assertNoValue()

        Mockito.verify(mockGitHubRepository, Mockito.never()).getRepo(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
        Mockito.verify(mockBundle, Mockito.times(1)).getStringArray("repo_details")
    }

    /**
     * When no repo is set AND Bundle does contain repo details, restoreState calls fetch repo
     */
    @Test
    fun testRestoreInstanceState_FetchRepo() {
        // Setup
        Mockito.`when`(mockBundle.getStringArray("repo_details")).thenReturn(arrayOf(repo.owner.login, repo.name))
        Mockito.`when`(mockGitHubRepository.getRepo(repo.owner.login, repo.name)).thenReturn(Single.just(repo))
        Mockito.`when`(mockGitHubRepository.getContributors(repo.contributorsUrl)).thenReturn(Single.just(contributorsList))

        var loadingObserver = viewModel.isLoading().test()
        var repoObserver = viewModel.getRepo().test()
        var errorObserver = viewModel.hasError().test()
        var contributorsObserver = viewModel.getContributors().test()
        var contributorsErrorObserver = viewModel.hasContributorsError().test()
        var contributorsLoadingObserver = viewModel.isContributorsLoading().test()

        // Test
        viewModel.restoreFromBundle(mockBundle)

        loadingObserver.assertHasValue().assertHistorySize(2).assertValueHistory(true, false).assertValue(false)
        errorObserver.assertHasValue().assertHistorySize(2).assertValueHistory(false, false).assertValue(false)
        repoObserver.assertHasValue().assertHistorySize(1).assertValue(repo)
        contributorsObserver.assertHasValue().assertHistorySize(1).assertValueHistory(contributorsList).assertValue(contributorsList)
        contributorsLoadingObserver.assertHasValue().assertHistorySize(2).assertValueHistory(true, false).assertValue(false)
        contributorsErrorObserver.assertHasValue().assertHistorySize(2).assertValueHistory(false, false).assertValue(false)

        Mockito.verify(mockBundle, Mockito.times(1)).getStringArray("repo_details")
        Mockito.verify(mockGitHubRepository, Mockito.times(1)).getRepo(repo.owner.login, repo.name)
        Mockito.verify(mockGitHubRepository, Mockito.times(1)).getContributors(repo.contributorsUrl)
    }

    /**
     * Test the liveData values when an error occurred during the loading of the repo.
     */
    @Test
    fun testRepoError() {
        // Setup
        Mockito.`when`(mockGitHubRepository.getRepo(repo.owner.login, repo.name)).thenReturn(Single.error(Exception("Test Exception")))

        var loadingObserver = viewModel.isLoading().test()
        var repoObserver = viewModel.getRepo().test()
        var errorObserver = viewModel.hasError().test()
        var contributorsObserver = viewModel.getContributors().test()
        var contributorsErrorObserver = viewModel.hasContributorsError().test()
        var contributorsLoadingObserver = viewModel.isContributorsLoading().test()

        // Test
        viewModel.fetchRepo(arrayOf(repo.owner.login, repo.name))

        loadingObserver.assertHasValue().assertHistorySize(2).assertValueHistory(true, false).assertValue(false)
        errorObserver.assertHasValue().assertHistorySize(2).assertValueHistory(false, true).assertValue(true)
        repoObserver.assertNoValue()
        contributorsObserver.assertNoValue()
        contributorsLoadingObserver.assertNoValue()
        contributorsErrorObserver.assertNoValue()

        Mockito.verify(mockGitHubRepository, Mockito.only()).getRepo(repo.owner.login, repo.name)
        Mockito.verify(mockGitHubRepository, Mockito.never()).getContributors(repo.contributorsUrl)
    }
}