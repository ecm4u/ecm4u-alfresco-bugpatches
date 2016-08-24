package de.axelfaust.alfresco.examples.patching.repo.content.transform;

import org.alfresco.repo.content.transform.OOoContentTransformerHelper;
import org.alfresco.repo.content.transform.OpenOfficeContentTransformerWorker;

/**
 * This patched sub-class aims to reduce issues with long file names (i.e. on Windows file systems) by limiting the temporary file prefix
 * this worker uses. Copied from <a href="https://github.com/angelborroy/oo-temp-filename-shortener">oo-temp-filename-shortener</a> GitHub
 * project. Note that this patch does not guarantee path length to be short enough - it only aims to add as short a prefix as possible
 * without a source-level patch of
 * {@link OOoContentTransformerHelper#transform(org.alfresco.service.cmr.repository.ContentReader, org.alfresco.service.cmr.repository.ContentWriter, org.alfresco.service.cmr.repository.TransformationOptions)
 * transform}.
 *
 * @author Angel Borroy
 */
public class OpenOfficeContentTransformerWorkerShortener extends OpenOfficeContentTransformerWorker
{

    /**
     *
     * {@inheritDoc}
     */
    @Override
    protected String getTempFilePrefix()
    {
        // shortest possible prefix that's still recognizable
        return "OOCT";
    }

}