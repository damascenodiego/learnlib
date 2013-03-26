/* Copyright (C) 2013 TU Dortmund
 * This file is part of LearnLib, http://www.learnlib.de/.
 * 
 * LearnLib is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 3.0 as published by the Free Software Foundation.
 * 
 * LearnLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with LearnLib; if not, see
 * <http://www.gnu.de/documents/lgpl.en.html>.
 */

package de.learnlib.oracles;

import de.learnlib.api.MembershipOracle;
import de.learnlib.oracles.HistogramOracle;
import de.learnlib.api.LLComponentFactory;
import de.learnlib.api.LLComponentParameter;
import de.learnlib.api.LLComponent;
import de.learnlib.api.LLComponentType;

/**
 *
 * @author falkhowar
 */
@LLComponent(
    name = "HistogramOracle",
    description = "A simple oracle that collects infos about query sizes",
    type = LLComponentType.MEMBERSHIP_ORACLE)
public class HistogramOracleFactory<I, O> implements LLComponentFactory<HistogramOracle<I, O>> {

    private MembershipOracle<I, O> next = null;
    private String name = "queries";

    @LLComponentParameter(name = "next", description = "oracle to actually use", required = true)
    public void setNext(MembershipOracle<I, O> next) {
        this.next = next;
    }

    @LLComponentParameter(name = "name", description = "name of the histogram")
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public HistogramOracle<I, O> instantiate() {
        if (next == null) {
            throw new IllegalStateException("next cannot be null");
        }
        return new HistogramOracle<>(next, name);
    }
}