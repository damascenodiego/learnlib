/* Copyright (C) 2018
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

import de.learnlib.api.SUL;
import de.learnlib.api.oracle.MembershipOracle;
import de.learnlib.datastructure.observationtable.DynamicObservationTable;
import de.learnlib.driver.util.MealySimulatorSUL;
import de.learnlib.oracle.membership.SULOracle;
import net.automatalib.automata.transout.impl.compact.CompactMealy;
import net.automatalib.commons.util.comparison.CmpUtil;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.Alphabets;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

/**
 * Test class for the DynamicObservationTable class.
 *
 * @author Carlos Diego Nascimento Damasceno (damascenodiego@usp.br)
 */
public class DynamicObservationTableTest {

    @Test
    public void testRemoveRedundancy(){
        //setup input alphabet
        Set<String> abc = new HashSet<>();
        abc.add("a");
        abc.add("b");
        abc.add("c");

        // create fsm_agm_1
        Alphabet<String> alphabet = Alphabets.fromCollection(abc);
        CompactMealy<String, Integer> mealym = new CompactMealy<String, Integer>(alphabet);

        int s1 = mealym.addState();
        int s4 = mealym.addState();
        int s5 = mealym.addState();

        // set s1 as initial state
        mealym.setInitialState(s1);

//        1 -- a/_1() -> 4
        mealym.addTransition(s1, "a", s4, 1);
//        1 -- b/_0() -> 1
        mealym.addTransition(s1, "b", s1, 0);
//        1 -- c/_0() -> 1
        mealym.addTransition(s1, "c", s1, 0);
//        4 -- a/_0() -> 4
        mealym.addTransition(s4, "a", s4, 0);
//        4 -- b/_1() -> 5
        mealym.addTransition(s4, "b", s5, 1);
//        4 -- c/_0() -> 4
        mealym.addTransition(s4, "c", s4, 0);
//        5 -- a/_1() -> 4
        mealym.addTransition(s5, "a", s4, 1);
//        5 -- b/_0() -> 5
        mealym.addTransition(s5, "b", s5, 0);
//        5 -- c/_1() -> 1
        mealym.addTransition(s5, "c", s1, 1);

        // SUL simulator
        SUL<String,Integer> sulSim = new MealySimulatorSUL(mealym);
        MembershipOracle<String,Integer> oracleForLearner  = new SULOracle(sulSim);


        // setup initial prefix set
        List<Word<String>> initPrefixes = new ArrayList<Word<String>>();

        Word<String> in = Word.epsilon();
        initPrefixes.add(in.append("a"));
        initPrefixes.add(in.append("a").append("b"));
        initPrefixes.add(in.append("a").append("b").append("a"));
        initPrefixes.add(in.append("a").append("b").append("c"));
        initPrefixes.add(in.append("a").append("a"));
        initPrefixes.add(Word.epsilon());
        // System.out.println(initPrefixes);
        // sort them all
        Collections.sort(initPrefixes, new Comparator<Word<String>>() {
            @Override
            public int compare(Word<String> o1, Word<String> o2) {
                return CmpUtil.lexCompare(o1, o2, alphabet);
            }
        });
        // System.out.println(initPrefixes);

        List<Word<String>> initSuffixes = new ArrayList<Word<String>>();
        initSuffixes.add(in.append("a"));
        initSuffixes.add(in.append("b"));
        initSuffixes.add(in.append("c"));
        initSuffixes.add(in.append("b").append("c"));
        initSuffixes.add(in.append("a").append("a").append("a"));
        initSuffixes.add(in.append("c").append("c"));

        DynamicObservationTable<String,Integer> dOT = new DynamicObservationTable<>(alphabet);

        dOT.initialize(initPrefixes,initSuffixes,oracleForLearner);

        System.out.println(dOT.getAllPrefixes());
        System.out.println(dOT.getSuffixes());


    }



}
