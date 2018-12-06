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

import de.learnlib.api.SUL;
import de.learnlib.api.oracle.MembershipOracle;
import de.learnlib.api.statistic.StatisticSUL;
import de.learnlib.datastructure.observationtable.DynamicObservationTable;
import de.learnlib.datastructure.observationtable.GenericObservationTable;
import de.learnlib.datastructure.observationtable.MutableObservationTable;
import de.learnlib.datastructure.observationtable.ObservationTable;
import de.learnlib.datastructure.observationtable.writer.ObservationTableASCIIWriter;
import de.learnlib.driver.util.MealySimulatorSUL;
import de.learnlib.filter.statistic.Counter;
import de.learnlib.filter.statistic.sul.ResetCounterSUL;
import de.learnlib.filter.statistic.sul.SymbolCounterSUL;
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

    private CompactMealy<String, Integer> mealym;
    private Alphabet<String> alphabet;

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

    @Test
    public void testRemoveRedundancy(){

        setUpModel();

        // SUL simulator
        SUL<String,Integer> sulSim = new MealySimulatorSUL<>(mealym);

        // Counter for MQs
        StatisticSUL<String, Integer> mq_sym = new SymbolCounterSUL<>("MQ", sulSim);
        // Counter for EQs
        StatisticSUL<String, Integer>  mq_rst = new ResetCounterSUL<>("MQ", mq_sym);

        // Membership oracle used by the observation table
        MembershipOracle<String,Integer> oracleForLearner  = new SULOracle(mq_rst);

        // setup initial prefix set
        List<Word<String>> initPrefixes = new ArrayList<>();
        List<Word<String>> initSuffixes = new ArrayList<>();

        setUpInitSets(initPrefixes,initSuffixes);

        MutableObservationTable<String,Integer> dOT = new DynamicObservationTable<>(alphabet);
//        MutableObservationTable<String,Integer> dOT = new GenericObservationTable<>(alphabet);

        dOT.initialize(initPrefixes,initSuffixes,oracleForLearner);

        long tot_mq_rst  = ((Counter)mq_rst.getStatisticalData()).getCount();
        long tot_mq_symb = ((Counter)mq_sym.getStatisticalData()).getCount();

        System.out.println(mq_rst.getStatisticalData());
        System.out.println(mq_sym.getStatisticalData());

        ObservationTableASCIIWriter<String,Integer> ot_writer = new ObservationTableASCIIWriter<>();

        ot_writer.write(dOT,System.out);


    }



}
