package de.qabel.qabelbox.contacts.interactor

import de.qabel.core.config.Contact
import de.qabel.qabelbox.contacts.dto.ContactDto
import de.qabel.qabelbox.contacts.dto.ContactParseResult
import de.qabel.qabelbox.contacts.dto.ContactsParseResult
import rx.Observable
import java.io.File
import java.io.FileDescriptor


interface ContactsUseCase {

    fun load(): Observable<ContactDto>
    fun search(filter : String): Observable<ContactDto>
    fun loadContact(keyIdentifier : String): Observable<ContactDto>

    fun saveContact(contact : Contact): Observable<Unit>
    fun deleteContact(contact : Contact): Observable<Unit>

    /**
     * Exports a contact to the given File.
     */
    fun exportContact(contactKey : String, targetFile: FileDescriptor) : Observable<Int>
    fun exportAllContacts(targetFile: FileDescriptor) : Observable<Int>

    /**
     * Exports a contact to the given directory with the default contactFileName (contact-{alias}.qco)
     */
    fun exportContact(contactKey: String, targetDirectory: File) : Observable<File>

    fun importContacts(file : FileDescriptor) : Observable<ContactsParseResult>
    fun importContactString(contactString : String) : Observable<ContactParseResult>

}
