/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.codeInsight

import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.rt.execution.junit.FileComparisonFailure
import com.intellij.testFramework.ExpectedHighlightingData
import com.intellij.util.io.createFile
import com.intellij.util.io.write
import org.jetbrains.kotlin.idea.test.PluginTestCaseBase
import org.jetbrains.kotlin.idea.test.SdkAndMockLibraryProjectDescriptor
import org.jetbrains.kotlin.test.KotlinTestUtils
import org.jetbrains.kotlin.test.TagsTestDataUtil
import java.io.File
import java.nio.file.Files

abstract class AbstractLineMarkersTestInLibrarySources : AbstractLineMarkersTest() {

    private var libraryCleanPath: String? = null

    private fun getLibraryCleanPath(): String = libraryCleanPath!!

    private fun getLibraryOriginalPath(): String = PluginTestCaseBase.getTestDataPathBase() + "/codeInsightInLibrary/_library"

    override fun getProjectDescriptor(): SdkAndMockLibraryProjectDescriptor {
        if (libraryCleanPath == null) {
            val libraryClean = Files.createTempDirectory("lineMarkers_library")
            val libraryOriginal = File(getLibraryOriginalPath())
            libraryCleanPath = libraryClean.toString()

            for (file in libraryOriginal.walkTopDown().filter { !it.isDirectory }) {
                val text = file.readText().replace("<lineMarker>", "").replace("</lineMarker>", "")
                val cleanFile = libraryClean.resolve(file.relativeTo(libraryOriginal).path)
                cleanFile.createFile()
                cleanFile.write(text)
            }
        }
        return SdkAndMockLibraryProjectDescriptor(getLibraryCleanPath(), true)
    }

    fun doTestWithLibrary(path: String) {
        doTest(path) {
            val fileSystem = VirtualFileManager.getInstance().getFileSystem("file")
            for (file in File(getLibraryOriginalPath()).walkTopDown().filter { !it.isDirectory }) {
                val project = myFixture.project
                myFixture.openFileInEditor(fileSystem.findFileByPath(file.absolutePath)!!)
                //myFixture.configureByFile(file.absolutePath.replace("\\", "/"))
                val document = myFixture.editor.document
                val data = ExpectedHighlightingData(
                    document, false, false, false, myFixture.file
                )
                data.init()

                PsiDocumentManager.getInstance(project).commitAllDocuments()

                myFixture.doHighlighting()

                val markers = DaemonCodeAnalyzerImpl.getLineMarkers(document, project)

                try {
                    data.checkLineMarkers(markers, document.text)

                    // This is a workaround for sad bug in ExpectedHighlightingData:
                    // the latter doesn't throw assertion error when some line markers are expected, but none are present.
                    if (FileUtil.loadFile(file).contains("<lineMarker") && markers.isEmpty()) {
                        throw AssertionError("Some line markers are expected, but nothing is present at all")
                    }
                } catch (error: AssertionError) {
                    try {
                        val actualTextWithTestData = TagsTestDataUtil.insertInfoTags(markers, true, myFixture.file.text)
                        KotlinTestUtils.assertEqualsToFile(file, actualTextWithTestData)
                    } catch (failure: FileComparisonFailure) {
                        throw FileComparisonFailure(
                            error.message + "\n" + failure.message,
                            failure.expected,
                            failure.actual,
                            failure.filePath
                        )
                    }

                }

                assertNavigationElements(markers)
            }
        }
    }
}