/*
 *  Copyright 2018 incub8 Software Labs GmbH
 *  Copyright 2018 protel Hotelsoftware GmbH
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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