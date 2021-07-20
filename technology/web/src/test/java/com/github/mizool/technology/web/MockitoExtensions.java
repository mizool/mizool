package com.github.mizool.technology.web;

import static org.mockito.Mockito.mock;

import lombok.experimental.UtilityClass;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.SubjectThreadState;

@UtilityClass
public class MockitoExtensions
{
    public Subject mockSubject()
    {
        SecurityManager securityManager = mock(SecurityManager.class);
        SecurityUtils.setSecurityManager(securityManager);

        Subject subject = mock(Subject.class);
        SubjectThreadState subjectThreadState = new SubjectThreadState(subject);
        subjectThreadState.bind();
        return subject;
    }
}
