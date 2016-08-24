package de.axelfaust.alfresco.examples.patching.repo.opencmis;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.opencmis.CMISConnector;
import org.alfresco.opencmis.dictionary.PropertyDefinitionWrapper;
import org.alfresco.opencmis.dictionary.TypeDefinitionWrapper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.Updatability;

/**
 * This patched class addresses <a href="https://issues.alfresco.com/jira/browse/MNT-16641">ACE-5373 (now MNT-16641)</a>. It is inspired by
 * <a href="https://github.com/angelborroy/alfresco-ace-5373">Angel Borroy's alfresco-ace-5373</a> patch but uses a different approach with
 * reduced amounts of copy&pasted original source code.
 *
 * @author Axel Faust
 */
public class PatchedCMISConnector extends CMISConnector
{

    private final ThreadLocal<Boolean> inSetProperties = new ThreadLocal<Boolean>();

    private final ThreadLocal<Map<String, PropertyData<?>>> userProvidedProperties = new ThreadLocal<Map<String, PropertyData<?>>>();

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void setProperties(final NodeRef nodeRef, final TypeDefinitionWrapper type, final Properties properties, final String... exclude)
    {
        final Map<String, PropertyData<?>> incomingPropsMap = properties.getProperties();

        this.userProvidedProperties.set(incomingPropsMap);
        this.inSetProperties.set(Boolean.TRUE);
        try
        {
            super.setProperties(nodeRef, type, properties, exclude);
        }
        finally
        {
            this.inSetProperties.remove();
            this.userProvidedProperties.remove();
        }
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void setProperty(final NodeRef nodeRef, final TypeDefinitionWrapper type, final String propertyId, final Serializable value)
    {
        if (propertyId == null)
        {
            if (Boolean.TRUE.equals(this.inSetProperties.get()))
            {
                final Map<String, PropertyData<?>> map = this.userProvidedProperties.get();

                // check for special bug case: property not provided by user, defined by secondary type and now attempted to be set to null
                if ((map == null || !map.containsKey(propertyId)) && value == null && type.getBaseTypeId() == BaseTypeId.CMIS_SECONDARY)
                {
                    final PropertyDefinitionWrapper propDef = type.getPropertyById(propertyId);

                    if (propDef != null
                            && (propDef.getPropertyDefinition().getUpdatability() == Updatability.READONLY || propDef
                                    .getPropertyDefinition().getDefaultValue() != null))
                    {
                        // processSecondaryTypes in base class just sets EVERY property to null if not provided by user
                        // ignore for read-only properties and those with a default value
                        return;
                    }
                }
            }
        }

        // continue with real logic
        super.setProperty(nodeRef, type, propertyId, value);
    }
}
