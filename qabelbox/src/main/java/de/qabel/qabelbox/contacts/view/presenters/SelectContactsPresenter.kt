package de.qabel.qabelbox.contacts.view.presenters

import de.qabel.qabelbox.contacts.dto.ContactDto
import de.qabel.qabelbox.contacts.interactor.ContactsUseCase
import de.qabel.qabelbox.contacts.view.views.ContactsView
import de.qabel.qabelbox.navigation.MainNavigator


class SelectContactsPresenter(private val view: ContactsView,
                              private val useCase: ContactsUseCase,
                              private val navigator: MainNavigator) : MainContactsPresenter(view, useCase, navigator) {

    override fun onClick(contact: ContactDto) = view.showConfirmShareMessage(contact)

    override fun onLongClick(contact: ContactDto): Boolean = false

}
