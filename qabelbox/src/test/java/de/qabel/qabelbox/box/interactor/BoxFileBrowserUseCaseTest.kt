package de.qabel.qabelbox.box.interactor

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.should.shouldMatch
import de.qabel.box.storage.BoxVolume
import de.qabel.qabelbox.BuildConfig
import de.qabel.qabelbox.SimpleApplication
import de.qabel.qabelbox.box.backends.MockStorageBackend
import de.qabel.qabelbox.box.dto.BoxPath
import de.qabel.qabelbox.box.dto.BrowserEntry
import de.qabel.qabelbox.box.dto.UploadSource
import de.qabel.qabelbox.box.dto.VolumeRoot
import de.qabel.qabelbox.box.provider.DocumentId
import de.qabel.qabelbox.util.IdentityHelper
import de.qabel.qabelbox.util.asString
import de.qabel.qabelbox.util.toUploadSource
import de.qabel.qabelbox.util.waitFor
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.annotation.Config
import rx.Observable
import java.util.Date

@RunWith(RobolectricGradleTestRunner::class)
@Config(application = SimpleApplication::class, constants = BuildConfig::class)
class BoxFileBrowserUseCaseTest {

    val identity = IdentityHelper.createIdentity("identity", null)
    val storage = MockStorageBackend()
    val deviceId = byteArrayOf(1,2,3)
    val docId = DocumentId(identity.keyIdentifier, identity.prefixes.first(), BoxPath.Root)
    val volume = VolumeRoot(docId.toString().dropLast(1), docId.toString(), identity.alias)
    //val volume = BoxVolume(storage, storage, identityA.primaryKeyPair,
    //        deviceId, createTempDir(), "prefix")
    lateinit var useCase: FileBrowserUseCase

    val samplePayload = "payload"
    val sampleName = "sampleName"
    val sample = BrowserEntry.File(sampleName, 42, Date())

    @Before
    fun setUp() {
         useCase = BoxFileBrowserUseCase(identity, storage, storage, byteArrayOf(1), createTempDir())
    }

    @Test
    fun asDocumentId() {
        useCase.asDocumentId(BoxPath.Root) evalsTo docId
    }

    @Test
    fun roundTripFile() {
        val path = BoxPath.Root * sampleName
        useCase.upload(path, samplePayload.toUploadSource(sample)).waitFor()
        useCase.download(path).waitFor().apply {
            asString() shouldMatch equalTo(samplePayload)
        }
    }

    @Test
    fun createSubfolder() {
        val path = BoxPath.Root / "firstFolder" / "subFolder"
        useCase.createFolder(path).waitFor()
        storage.storage.size eq 3 // index and 2 folder metadata files
        useCase.query(path.parent) evalsTo BrowserEntry.Folder(path.parent.name)
        useCase.query(path) evalsTo BrowserEntry.Folder(path.name)
    }

    @Test
    fun uploadInSubfolder() {
        val path = BoxPath.Root / "firstFolder" / "subFolder" * sampleName
        useCase.upload(path, samplePayload.toUploadSource(sample)).waitFor()
        useCase.query(path.parent) evalsTo BrowserEntry.Folder(path.parent.name)
        useCase.download(path).waitFor().apply {
            asString() shouldMatch equalTo(samplePayload)
        }
        storage.storage.size eq 4 // index and 2 folder metadata files and 1 file
    }
}

infix fun <T> T.eq(thing: T) {
    assertThat(this, equalTo(thing))
}

infix fun <T> Observable<T>.evalsTo(thing: T) {
    assertThat(this.toBlocking().first(), equalTo(thing))
}
