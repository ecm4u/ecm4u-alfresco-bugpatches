/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
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
 */
package de.ecm4u.alfresco.bugpatches.org.alfresco.repo.web.scripts.datalist;

import org.alfresco.model.DataListModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;


/**
 * Data List Download
 * 
 * Exports the contents of a Data List as an Excel file
 * 
 * @author Nick Burch
 */
public class DataListDownloadWebScript extends org.alfresco.repo.web.scripts.datalist.DataListDownloadWebScript {

    private NodeService nodeService;
    private NamespaceService namespaceService;

    private static final QName DATA_LIST_ITEM_TYPE = DataListModel.PROP_DATALIST_ITEM_TYPE;

    /**
     * @param nodeService NodeService
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    private QName buildType(NodeRef list)
    {
        return QName.createQName((String) nodeService.getProperty(list, DATA_LIST_ITEM_TYPE), this.namespaceService);
    }
}
