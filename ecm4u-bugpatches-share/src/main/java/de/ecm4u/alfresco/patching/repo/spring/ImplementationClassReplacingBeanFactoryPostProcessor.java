package de.ecm4u.alfresco.patching.repo.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * Bean post processor to alter the implementation class of a bean definition
 * without requiring an override that may conflict with custom Spring
 * configuration.
 *
 * @author Axel Faust
 * @author Lutz Horn
 * @since 3.0
 */
public class ImplementationClassReplacingBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    private static final Log LOGGER = LogFactory.getLog(ImplementationClassReplacingBeanFactoryPostProcessor.class);

    protected String originalClassName;

    protected String replacementClassName;

    protected String targetBeanName;

    protected boolean active;

    /**
     * @param originalClassName the originalClassName to set
     */
    public void setOriginalClassName(final String originalClassName) {
        this.originalClassName = originalClassName;
    }

    /**
     * @param replacementClassName the replacementClassName to set
     */
    public void setReplacementClassName(final String replacementClassName) {
        this.replacementClassName = replacementClassName;
    }

    /**
     * @param targetBeanName the targetBeanName to set
     */
    public void setTargetBeanName(final String targetBeanName) {
        this.targetBeanName = targetBeanName;
    }

    /**
     * @param active the active to set
     */
    public void setActive(final boolean active) {
        this.active = active;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (LOGGER.isInfoEnabled()) {
            String msg = String.format("postProcessBeanFactory: active=%s, targetBeanName=%s, originalClassName=%s, replacementClassName=%s",
                    active, targetBeanName, originalClassName, replacementClassName);
            LOGGER.info(msg);
        }
        if (this.active && this.targetBeanName != null && this.replacementClassName != null) {
            final BeanDefinition beanDefinition = beanFactory.getBeanDefinition(this.targetBeanName);
            if (beanDefinition != null
                    && (this.originalClassName == null || this.originalClassName.equals(beanDefinition.getBeanClassName()))) {
                beanDefinition.setBeanClassName(this.replacementClassName);
            }
        }
    }

}
