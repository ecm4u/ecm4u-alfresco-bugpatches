/*
 * #%L
 * Alfresco Repository
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * Copyright (C) 2016 Lutz Horn and Axel Faust
 * %% *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package de.ecm4u.alfresco.bugpatches.org.alfresco.repo.quickshare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.quickshare.InvalidSharedIdException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Fixes a bug when a QuickShare is used in Multi Tenant setup.
 * 
 * @see https://github.com/ecm4u/ecm4u-alfresco-bugpatches/issues/2
 * @author Lutz Horn
 * @author Axel Faust
 * @since 3.0
 */
public class QuickShareServiceImpl extends org.alfresco.repo.quickshare.QuickShareServiceImpl
{

    private static final Log LOGGER = LogFactory.getLog(QuickShareServiceImpl.class);

    private SearchService searchService;

    private TenantService tenantService;

    @Override
    public Pair<String, NodeRef> getTenantNodeRefFromSharedId(final String sharedId)
    {
        Pair<String, NodeRef> result;

        try
        {
            result = super.getTenantNodeRefFromSharedId(sharedId);
        }
        catch (final InvalidSharedIdException isiex)
        {
            // thrown when attribute for sharedId returns null
            LOGGER.info("Invalid share ID encountered - running fallback logic to compensate for RA-1093 and MNT-16224", isiex);
            /*
             * TODO Temporary fix for RA-1093 and MNT-16224. The extra lookup should be removed when we have a system wide patch to remove
             * the 'shared' aspect of the nodes that have been archived while shared.
             */

            // TMDQ
            final String query = "+TYPE:\"cm:content\" AND +ASPECT:\"qshare:shared\" AND =qshare:sharedId:\"" + sharedId + "\"";
            final SearchParameters sp = new SearchParameters();
            sp.setLanguage(SearchService.LANGUAGE_FTS_ALFRESCO);
            sp.setQuery(query);
            sp.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);

            List<NodeRef> nodeRefs = new ArrayList<>();
            ResultSet results = null;
            try
            {
                results = this.searchService.query(sp);
                nodeRefs = results.getNodeRefs();
            }
            catch (final Exception ex)
            {
                throw new InvalidSharedIdException(sharedId);
            }
            finally
            {
                if (results != null)
                {
                    results.close();
                }
            }
            if (nodeRefs.size() != 1)
            {
                throw new InvalidSharedIdException(sharedId);
            }

            final NodeRef nodeRef = this.tenantService.getName(nodeRefs.get(0));

            // note: relies on tenant-specific (ie. mangled) nodeRef
            final String tenantDomain = this.tenantService.getDomain(nodeRef.getStoreRef().getIdentifier());

            result = new Pair<>(tenantDomain, this.tenantService.getBaseName(nodeRef));
        }

        result.setSecond(this.unmangleNodeRef(result.getSecond()));

        return result;
    }

    private NodeRef unmangleNodeRef(NodeRef nodeRef)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("nodeRef=" + nodeRef);
        }
        String nodeRefAsString = nodeRef.toString();
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("nodeRefAsString=" + nodeRefAsString);
        }
        final String[] parts = nodeRefAsString.split(TenantService.SEPARATOR);
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("parts=" + Arrays.toString(parts));
        }
        if (parts.length == 3)
        {
            nodeRefAsString = parts[0] + parts[2];
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("nodeRefAsString=" + nodeRefAsString);
            }
            nodeRef = new NodeRef(nodeRefAsString);
        }
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("nodeRef=" + nodeRef);
        }
        return nodeRef;
    }

    /**
     * Spring configuration
     *
     * @param searchService
     *            the searchService to set
     */
    public void setSearchService(final SearchService searchService)
    {
        this.searchService = searchService;
    }

    /**
     * Set the tenant service
     *
     * @param tenantService
     *            the tenantService to set
     */
    @Override
    public void setTenantService(final TenantService tenantService)
    {
        super.setTenantService(tenantService);
        this.tenantService = tenantService;
    }
}
