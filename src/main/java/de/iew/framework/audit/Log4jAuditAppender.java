/*
 * Copyright 2012-2013 Manuel Schulze <manuel_schulze@i-entwicklung.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.iew.framework.audit;

import de.iew.framework.domain.audit.Severity;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.message.GenericMessage;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Implements a Log4j appender to fire Log4j messages as audit events.
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 02.02.13 - 21:51
 */
@ManagedResource(objectName = "de.iew:name=Log4jAuditAppender", description = "Log4jAuditAppender to log important Log4j messages through the audit service.")
public class Log4jAuditAppender extends AppenderSkeleton implements ApplicationListener<ApplicationContextEvent>, DisposableBean {

    /**
     * The constant DEFAULT_AUDIT_SERVICE_APPENDER_LOG4J_PRIORITY.
     */
    public static final Level DEFAULT_AUDIT_SERVICE_APPENDER_LOG4J_PRIORITY = Level.INFO;

    /**
     * The name of the log4j appender logging level.
     */
    private Level appenderLog4jPriority = Log4jAuditAppender.DEFAULT_AUDIT_SERVICE_APPENDER_LOG4J_PRIORITY;

    public void destroy() throws Exception {
        if (isLog4jConfiguredForAuditLogging()) {
            Logger logger = Logger.getRootLogger();
            logger.removeAppender(getName());
        }
    }

    public synchronized void onApplicationEvent(ApplicationContextEvent contextStartedEvent) {
        if (!isLog4jConfiguredForAuditLogging()
                && (contextStartedEvent instanceof ContextStartedEvent
                || contextStartedEvent instanceof ContextRefreshedEvent)) {
            Logger logger = Logger.getRootLogger();

            logger.addAppender(this);
        } else if (isLog4jConfiguredForAuditLogging()
                && contextStartedEvent instanceof ContextStoppedEvent) {
            Logger logger = Logger.getRootLogger();
            logger.removeAppender(getName());

            this.applicationEventChannel = null;
        }
    }

    /**
     * Is Log4j configured for audit logging.
     *
     * @return the boolean
     */
    protected synchronized boolean isLog4jConfiguredForAuditLogging() {
        Logger logger = Logger.getRootLogger();
        return (logger.getAppender(getName()) != null);
    }

    @Override
    protected void append(LoggingEvent loggingEvent) {
        if (loggingEvent.getLevel().isGreaterOrEqual(this.appenderLog4jPriority)) {
            Severity severity = log4jLevelToSeverity(loggingEvent.getLevel());
            if (severity != null) {
                log(loggingEvent, getAuthentication(), severity);
            }
        }
    }

    protected void log(LoggingEvent loggingEvent, Authentication auth, Severity severity) {
        MessageChannel messageChannel;

        synchronized (this) {
            messageChannel = this.applicationEventChannel;
        }

        if (messageChannel != null) {
            ThrowableInformation ti = loggingEvent.getThrowableInformation();
            Throwable t = null;
            if (ti != null) {
                t = ti.getThrowable();
            }


            String caller = loggingEvent.getLoggerName();

            Log4jAuditEvent event = new Log4jAuditEvent(caller, loggingEvent.getTimeStamp(), auth, severity, loggingEvent.getRenderedMessage(), t);
            Message<Log4jAuditEvent> eventMessage = new GenericMessage<Log4jAuditEvent>(event);
            messageChannel.send(eventMessage);
        }
    }

    protected Authentication getAuthentication() {
        SecurityContext securityContextHolder = SecurityContextHolder.getContext();
        return securityContextHolder.getAuthentication();
    }

    protected Severity log4jLevelToSeverity(Level level) {
        if (Level.TRACE.equals(level)) {
            return Severity.TRACE;
        } else if (Level.DEBUG.equals(level)) {
            return Severity.DEBUG;
        } else if (Level.INFO.equals(level)) {
            return Severity.INFO;
        } else if (Level.WARN.equals(level)) {
            return Severity.WARN;
        } else if (Level.ERROR.equals(level)) {
            return Severity.CRITICAL;
        } else if (Level.FATAL.equals(level)) {
            return Severity.CRITICAL;
        } else {
            return null;
        }
    }

    public void close() {
    }

    public boolean requiresLayout() {
        return false;
    }

    @ManagedAttribute(description = "The name of the log4j appender logging level.",
            defaultValue = "WARN")
    public void setAppenderLog4jPriority(String appenderLog4jPriority) {
        this.appenderLog4jPriority = Level.toLevel(appenderLog4jPriority, Log4jAuditAppender.DEFAULT_AUDIT_SERVICE_APPENDER_LOG4J_PRIORITY);
    }

    @ManagedAttribute
    public String getAppenderLog4jPriority() {
        return this.appenderLog4jPriority.toString();
    }

    // Spring und Dao Abhängigkeiten

    private MessageChannel applicationEventChannel;

    public void setApplicationEventChannel(MessageChannel applicationEventChannel) {
        this.applicationEventChannel = applicationEventChannel;
    }
}
