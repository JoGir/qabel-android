package de.qabel.qabelbox.dagger.modules;

import android.content.Context;
import android.support.v7.app.NotificationCompat;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.internal.Factory;
import de.qabel.core.http.MainDropConnector;
import de.qabel.core.http.MainDropServer;
import de.qabel.core.repository.ChatDropMessageRepository;
import de.qabel.core.repository.ContactRepository;
import de.qabel.core.repository.DropStateRepository;
import de.qabel.core.repository.IdentityRepository;
import de.qabel.core.service.ChatService;
import de.qabel.core.service.MainChatService;
import de.qabel.qabelbox.QabelBoxApplication;
import de.qabel.qabelbox.chat.notifications.presenter.AndroidChatNotificationPresenter;
import de.qabel.qabelbox.chat.notifications.ChatNotificationManager;
import de.qabel.qabelbox.chat.notifications.presenter.ChatNotificationPresenter;
import de.qabel.qabelbox.chat.notifications.MainChatNotificationManager;
import de.qabel.qabelbox.chat.interactor.ChatServiceUseCase;
import de.qabel.qabelbox.chat.interactor.MainChatServiceUseCase;
import de.qabel.qabelbox.chat.transformers.ChatMessageTransformer;

@Module
public class ApplicationModule extends ContextModule {

    public ApplicationModule(QabelBoxApplication application) {
        super(application);
    }

    @Singleton @Provides ChatNotificationManager providesChatNotificationManager(
            MainChatNotificationManager manager) {
        return manager;
    }

    @Singleton @Provides ChatNotificationPresenter providesChatNotificationPresenter(
            AndroidChatNotificationPresenter presenter) {
        return presenter;
    }

    @Singleton
    @Provides
    ChatService providesChatService(IdentityRepository identityRepository, ContactRepository contactRepository,
                                    DropStateRepository dropStateRepository,
                                    ChatDropMessageRepository chatDropMessageRepository) {
        return new MainChatService(new MainDropConnector(new MainDropServer()), identityRepository,
                contactRepository, chatDropMessageRepository, dropStateRepository);
    }

    @Singleton
    @Provides
    ChatServiceUseCase providesChatManager(ContactRepository contactRepo, ChatDropMessageRepository chatDropMessageRepository,
                                           IdentityRepository identityRepo, ChatMessageTransformer msgTransformer){
        return new MainChatServiceUseCase(chatDropMessageRepository, contactRepo, identityRepo, msgTransformer);
    }

    @Provides
    Factory<NotificationCompat.Builder> providesNotificationBuilder(
            final Context context) {
        return new Factory<NotificationCompat.Builder>() {
            @Override
            public NotificationCompat.Builder get() {
                return new NotificationCompat.Builder(context);
            }
        };
    }

}
