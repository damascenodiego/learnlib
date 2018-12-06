/* Copyright (C) 2018
 * This file is part of the PhD research project entitled
 * 'Inferring models from Evolving Systems and Product Families'
 * developed by Carlos Diego Nascimento Damasceno at the
 * University of Sao Paulo (ICMC-USP).
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
package de.learnlib.algorithms.dlstar;

import de.learnlib.algorithms.dlstar.mealy.ExtensibleDLStarMealy;
import de.learnlib.algorithms.lstar.LearningTest;
import de.learnlib.algorithms.lstar.ce.ObservationTableCEXHandler;
import de.learnlib.algorithms.lstar.closing.ClosingStrategy;
import de.learnlib.algorithms.lstar.mealy.ClassicLStarMealy;
import de.learnlib.algorithms.lstar.mealy.ExtensibleLStarMealy;
import de.learnlib.api.SUL;
import de.learnlib.api.algorithm.LearningAlgorithm;
import de.learnlib.api.oracle.EquivalenceOracle;
import de.learnlib.api.oracle.MembershipOracle;
import de.learnlib.api.oracle.MembershipOracle.MealyMembershipOracle;
import de.learnlib.api.statistic.StatisticSUL;
import de.learnlib.datastructure.observationtable.DynamicObservationTable;
import de.learnlib.datastructure.observationtable.ObservationTable;
import de.learnlib.datastructure.observationtable.writer.ObservationTableASCIIWriter;
import de.learnlib.driver.util.MealySimulatorSUL;
import de.learnlib.examples.mealy.ExampleStack;
import de.learnlib.filter.statistic.Counter;
import de.learnlib.filter.statistic.sul.ResetCounterSUL;
import de.learnlib.filter.statistic.sul.SymbolCounterSUL;
import de.learnlib.oracle.equivalence.SimulatorEQOracle;
import de.learnlib.oracle.equivalence.mealy.SymbolEQOracleWrapper;
import de.learnlib.oracle.membership.SULOracle;
import de.learnlib.oracle.membership.SimulatorOracle;
import de.learnlib.oracle.membership.SimulatorOracle.MealySimulatorOracle;
import de.learnlib.util.mealy.MealyUtil;
import net.automatalib.automata.transout.MealyMachine;
import net.automatalib.automata.transout.impl.compact.CompactMealy;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.Alphabets;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

@Test
public class ExtensibleDLStarMealyTest extends LearningTest {

    private Alphabet<String> alphabet;
    private CompactMealy<String, Integer> mealym;

    @Test
    public void testOptimizedLStarMealy() {
        setUpModel();
        MealyMachine<?, String, ?, Integer> mealy = mealym;

        // SUL simulator
        SUL<String,Integer> sulSim = new MealySimulatorSUL<>(mealy);

        // Counter for MQs
        StatisticSUL<String, Integer> mq_sym = new SymbolCounterSUL<>("MQ", sulSim);
        // Counter for EQs
        StatisticSUL<String, Integer>  mq_rst = new ResetCounterSUL<>("MQ", mq_sym);

        MembershipOracle<String, Word<Integer>> oracle = new SULOracle(mq_rst);

        EquivalenceOracle<? super MealyMachine<?, String, ?, Integer>, String, Word<Integer>>
                mealyEqOracle = new SimulatorEQOracle<>(mealy);


        for (ObservationTableCEXHandler<? super String, ? super Word<Integer>> handler : LearningTest.CEX_HANDLERS) {
            for (ClosingStrategy<? super String, ? super Word<Integer>> strategy : LearningTest.CLOSING_STRATEGIES) {

                // setup initial prefix set
                List<Word<String>> initPrefixes = new ArrayList<>();
                List<Word<String>> initSuffixes = new ArrayList<>();

                setUpInitSets(initPrefixes,initSuffixes);

                LearningAlgorithm<MealyMachine<?, String, ?, Integer>, String, Word<Integer>>
                        learner = new ExtensibleDLStarMealy<String,Integer>(alphabet, oracle, initPrefixes, initSuffixes, handler, strategy);

                testLearnModel(mealy, alphabet, learner, oracle, mealyEqOracle);

                long tot_mq_rst  = ((Counter)mq_rst.getStatisticalData()).getCount();
                long tot_mq_symb = ((Counter)mq_sym.getStatisticalData()).getCount();

                ObservationTableASCIIWriter<String,Word<Integer>> ot_writer = new ObservationTableASCIIWriter<>();
                StringBuilder sb_expected = new StringBuilder();
                StringBuilder sb_obtained = new StringBuilder();
                sb_expected.append("+=======+=====+\n" +
                        "|       | b c |\n" +
                        "+=======+=====+\n" +
                        "| ε     | 0 0 |\n" +
                        "+-------+-----+\n" +
                        "| a     | 1 1 |\n" +
                        "+-------+-----+\n" +
                        "| a b   | 0 1 |\n" +
                        "+=======+=====+\n" +
                        "| b     | 0 0 |\n" +
                        "+-------+-----+\n" +
                        "| c     | 0 0 |\n" +
                        "+-------+-----+\n" +
                        "| a a   | 1 1 |\n" +
                        "+-------+-----+\n" +
                        "| a c   | 1 1 |\n" +
                        "+-------+-----+\n" +
                        "| a b a | 1 1 |\n" +
                        "+-------+-----+\n" +
                        "| a b b | 0 1 |\n" +
                        "+-------+-----+\n" +
                        "| a b c | 0 0 |\n" +
                        "+=======+=====+\n");

                ObservationTable<String, Word<Integer>> dOT = ((ExtensibleDLStarMealy<String, Integer>) learner).getObservationTable();
                ot_writer.write(dOT,sb_obtained);

                Assert.assertEquals(sb_expected.toString(),sb_obtained.toString());

                Assert.assertEquals(dOT.getShortPrefixRows().size(),3);
                Assert.assertEquals(dOT.getRow(0).getLabel(),Word.epsilon());
                Assert.assertEquals(dOT.getRow(1).getLabel(),Word.epsilon().append("a"));
                Assert.assertEquals(dOT.getRow(2).getLabel(),Word.epsilon().append("a").append("b"));

                Assert.assertEquals(dOT.getSuffixes().size(),1);
                Assert.assertEquals(dOT.getSuffixes().get(0),Word.epsilon().append("b").append("c"));
            }
        }
    }

    public void setUpModel(){
        //setup input alphabet
        Set<String> abc = new HashSet<>();
        abc.add("a");
        abc.add("b");
        abc.add("c");

        // create fsm_agm_1
        alphabet = Alphabets.fromCollection(abc);
        mealym = new CompactMealy<>(alphabet);

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
//        5 -- a/_1() -> 4setup
        mealym.addTransition(s5, "a", s4, 1);
//        5 -- b/_0() -> 5
        mealym.addTransition(s5, "b", s5, 0);
//        5 -- c/_1() -> 1
        mealym.addTransition(s5, "c", s1, 1);

    }

    public void setUpInitSets(List<Word<String>> initPref, List<Word<String>> initSuf){
        initPref.clear();

        Word<String> in = Word.epsilon();
        // epsilon must be in the first position
        initPref.add(Word.epsilon());

        initPref.add(in.append("a"));
        initPref.add(in.append("a").append("b"));
        initPref.add(in.append("a").append("b").append("a"));
        initPref.add(in.append("a").append("b").append("c"));
        initPref.add(in.append("a").append("a"));
        initPref.add(in.append("a").append("a").append("a"));
        initPref.add(in.append("a").append("a").append("a").append("a"));
        initPref.add(in.append("a").append("a").append("a").append("b"));

        initSuf.clear();
        initSuf.add(in.append("a"));
        initSuf.add(in.append("b"));
        initSuf.add(in.append("c"));
        initSuf.add(in.append("b").append("c"));
        //initSuf.add(in.append("b").append("c"));
        initSuf.add(in.append("a").append("a").append("a"));
        initSuf.add(in.append("c").append("c"));

    }

}
