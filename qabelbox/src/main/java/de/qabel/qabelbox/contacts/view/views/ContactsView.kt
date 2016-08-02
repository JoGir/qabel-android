package de.qabel.qabelbox.contacts.view.views

import de.qabel.qabelbox.contacts.dto.ContactDto
import java.io.File


interface ContactsView {

    var searchString: String?
    var isMainView: Boolean

    fun showEmpty()

    fun loadData(data: List<ContactDto>)

    fun startExportFileChooser(filename: String, requestCode: Int)
    fun startImportFileChooser(requestCode: Int)

    fun startShareDialog(targetFile: File)

    fun showExportFailed()
    fun showExportSuccess(size: Int)

    fun showImportFailed()
    fun showImportSuccess(imported: Int, size: Int)

    fun showContactDeletedMessage(contact: ContactDto)

    fun startQRScan()

    open fun showContactMenu(contact: ContactDto)
    open fun showConfirmShareMessage(contact: ContactDto)
}

