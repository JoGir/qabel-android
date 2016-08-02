package de.qabel.qabelbox.contacts.dto

import de.qabel.qabelbox.chat.dto.SymmetricKey
import java.net.URL


sealed class SelectContactAction {

    class ShareTextAction(val text: String) : SelectContactAction()
    class ShareFileAction(val fileName: String, val url: URL, val key: SymmetricKey) : SelectContactAction()

}
