package de.qabel.qabelbox.contacts.dagger;

import dagger.Module;
import dagger.Provides;
import de.qabel.qabelbox.contacts.interactor.ContactsUseCase;
import de.qabel.qabelbox.contacts.view.presenters.ContactsPresenter;
import de.qabel.qabelbox.contacts.view.presenters.MainContactsPresenter;
import de.qabel.qabelbox.contacts.view.presenters.SelectContactsPresenter;
import de.qabel.qabelbox.contacts.view.views.ContactsView;
import de.qabel.qabelbox.dagger.scopes.ActivityScope;
import de.qabel.qabelbox.navigation.MainNavigator;

@ActivityScope
@Module
public class ContactsModule extends ContactBaseModule {

    private ContactsView view;

    public ContactsModule(ContactsView view) {
        this.view = view;
    }

    @Provides
    public ContactsView provideContactsView() {
        return view;
    }

    @Provides
    public ContactsPresenter provideContactsPresenter(ContactsUseCase contactsUseCase, MainNavigator navigator) {
        if (view.isMainView()) {
            return new MainContactsPresenter(view, contactsUseCase, navigator);
        } else {
            return new SelectContactsPresenter(view, contactsUseCase, navigator);
        }
    }
}
