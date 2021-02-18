/* Copyright (C) 2013-2020 TU Dortmund
 * This file is part of LearnLib, http://www.learnlib.de/.
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
package de.learnlib.buildtool.refinement.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Definition of an additional inferface.
 *
 * @author frohme
 */
@Retention(RetentionPolicy.SOURCE)
@Target({})
public @interface Interface {

    /**
     * A reference to the interface.
     *
     * @return a reference to the interface
     */
    Class<?> clazz();

    /**
     * Potential nested type parameters of the referenced (cf. {@link #clazz()}) interface.
     *
     * @return potential nested type parameters of the referenced interface
     */
    String[] generics() default {};

}
