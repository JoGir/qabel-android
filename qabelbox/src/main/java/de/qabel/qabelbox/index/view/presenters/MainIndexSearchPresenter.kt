package de.qabel.qabelbox.index.view.presenters

import com.google.i18n.phonenumbers.NumberParseException
import de.qabel.core.index.formatPhoneNumber
import de.qabel.core.logging.QabelLog
import de.qabel.core.repository.ContactRepository
import de.qabel.qabelbox.contacts.dto.ContactDto
import de.qabel.qabelbox.index.interactor.IndexSearchUseCase
import de.qabel.qabelbox.index.view.views.IndexSearchView
import javax.inject.Inject

class MainIndexSearchPresenter @Inject constructor(
        val view: IndexSearchView,
        val useCase: IndexSearchUseCase,
        val contactRepository: ContactRepository)
: IndexSearchPresenter, QabelLog {

    override fun search() {
        view.searchString?.let {
            if (it.isNotBlank()) {
                val phone = try { formatPhoneNumber(it) } catch (e: NumberParseException) { "" }
                if (phone.isNotBlank()) {
                    view.searchString = phone
                }
                useCase.search(it, phone).subscribe({
                    info("Index search result length: ${it.size}")
                    if (it.size > 0) {
                        view.loadData(it)
                    } else {
                        view.showEmpty()
                    }
                }, { view.showError(it) })

            }
        }
    }

    override fun showDetails(contact: ContactDto) {
        contactRepository.persist(contact.contact, contact.identities)
        view.showDetails(contact)
    }

}
