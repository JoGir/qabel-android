package de.qabel.qabelbox.contacts.view.presenter

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import de.qabel.core.config.Contact
import de.qabel.core.config.Identities
import de.qabel.core.repository.ContactRepository
import de.qabel.core.repository.IdentityRepository
import de.qabel.qabelbox.BuildConfig
import de.qabel.qabelbox.SimpleApplication
import de.qabel.qabelbox.contacts.dto.ContactDto
import de.qabel.qabelbox.contacts.interactor.ContactsUseCase
import de.qabel.qabelbox.contacts.view.presenters.ContactEditPresenter
import de.qabel.qabelbox.contacts.view.presenters.MainContactEditPresenter
import de.qabel.qabelbox.contacts.view.views.ContactEditView
import de.qabel.qabelbox.navigation.Navigator
import de.qabel.qabelbox.test.TestConstants
import de.qabel.qabelbox.tmp_core.InMemoryContactRepository
import de.qabel.qabelbox.tmp_core.InMemoryIdentityRepository
import de.qabel.qabelbox.util.IdentityHelper
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.annotation.Config
import rx.lang.kotlin.toSingletonObservable

@RunWith(RobolectricGradleTestRunner::class)
@Config(application = SimpleApplication::class, constants = BuildConfig::class)
class ContactEditPresenterTest {

    val identity = IdentityHelper.createIdentity("Identity", TestConstants.PREFIX);
    val contactA = IdentityHelper.createContact("ContactA");
    val contactADto = ContactDto(contactA, listOf(identity));

    val identityB = IdentityHelper.createIdentity("IdentityB", TestConstants.PREFIX)

    val identities: Identities = Identities().apply {
        put(identity)
        put(identityB)
    }

    val contactRepo: ContactRepository = InMemoryContactRepository()
    val identityRepo: IdentityRepository = InMemoryIdentityRepository()

    init {
        identityRepo.save(identity)
        identityRepo.save(identityB)
        contactRepo.save(contactA, identity);
    }

    lateinit var contactUseCase: ContactsUseCase
    lateinit var detailsView: ContactEditView
    lateinit var presenter: ContactEditPresenter
    lateinit var navigator: Navigator

    @Before
    fun setUp() {
        contactUseCase = mock()
        whenever(contactUseCase.loadContactAndIdentities(contactA.keyIdentifier)).
                thenReturn(Pair(contactADto, identities).toSingletonObservable())
        whenever(contactUseCase.saveContact(contactADto)).then {
            contactRepo.update(contactADto.contact, contactADto.identities).toSingletonObservable()
        }

        detailsView = mock()
        whenever(detailsView.getEditLabel()).thenReturn("EDIT LABEL")
        whenever(detailsView.getNewLabel()).thenReturn("NEW LABEL")

        navigator = mock()
        whenever(detailsView.contactKeyId).thenAnswer { contactA.keyIdentifier }
        presenter = MainContactEditPresenter(detailsView, contactUseCase, navigator)
    }

    @Test
    fun testRefresh() {
        presenter.loadContact()
        verify(contactUseCase).loadContactAndIdentities(contactA.keyIdentifier);
        assertThat(presenter.title, equalTo("EDIT LABEL"));
        verify(detailsView).loadContact(contactADto, identities);

        contactA.status = Contact.ContactStatus.UNKNOWN
        presenter.loadContact()
        assertThat(presenter.title, equalTo("NEW LABEL"))
    }

    @Test
    fun testEmptyName() {
        whenever(detailsView.getCurrentNick()).thenReturn("")
        presenter.onSaveClick()
        verify(detailsView).showEnterNameToast()
    }

    @Test
    fun testSave() {
        presenter.loadContact()
        verify(contactUseCase).loadContactAndIdentities(contactA.keyIdentifier)
        whenever(detailsView.getCurrentNick()).thenReturn("TestNick")
        whenever(detailsView.getCurrentIdentityIds()).thenReturn(listOf(identityB.id))

        presenter.onSaveClick()

        verify(contactUseCase).saveContact(contactADto)
        verify(navigator).popBackStack()

        val contactPair = contactRepo.findContactWithIdentities(contactA.keyIdentifier)
        assertThat(contactPair.first.nickName, equalTo("TestNick"))
        assertThat(contactPair.second, hasSize(1))
        assertThat(contactPair.second.first(), equalTo(identityB))
    }

}
