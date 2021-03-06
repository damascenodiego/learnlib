/* Copyright (C) 2013-2018 TU Dortmund
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
package de.learnlib.oracle.equivalence;

import de.learnlib.api.oracle.AutomatonOracle;
import de.learnlib.api.oracle.InclusionOracle;
import de.learnlib.api.oracle.MembershipOracle;
import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.words.Word;

public class MealyBFInclusionOracle<I, O> extends AbstractBFInclusionOracle<MealyMachine<?, I, ?, O>, I, Word<O>>
        implements InclusionOracle.MealyInclusionOracle<I, O>, AutomatonOracle.MealyOracle<I, O> {

    public MealyBFInclusionOracle(MembershipOracle<I, Word<O>> membershipOracle, double multiplier) {
        super(membershipOracle, multiplier);
    }
}
