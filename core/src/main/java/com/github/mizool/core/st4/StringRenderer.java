/**
 *  Copyright 2017 incub8 Software Labs GmbH
 *  Copyright 2017 protel Hotelsoftware GmbH
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
package com.github.mizool.core.st4;

import java.util.Locale;

public class StringRenderer extends org.stringtemplate.v4.StringRenderer
{
    @Override
    public String toString(Object o, String formatString, Locale locale)
    {
        String s = (String) o;
        String result;

        if (formatString == null)
        {
            result = s;
        }
        else if (formatString.equals("uncap"))
        {
            result = (s.length() > 0) ? Character.toLowerCase(s.charAt(0)) + s.substring(1) : s;
        }
        else
        {
            result = super.toString(o, formatString, locale);
        }

        return result;
    }
}