/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.iamacat.multithreading.utils.apache.commons.math3.analysis.function;

import fr.iamacat.multithreading.utils.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import fr.iamacat.multithreading.utils.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import fr.iamacat.multithreading.utils.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

/**
 * Constant function.
 *
 * @since 3.0
 */
public class Constant implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction {
    /** Constant. */
    private final double c;

    /**
     * @param c Constant.
     */
    public Constant(double c) {
        this.c = c;
    }

    /** {@inheritDoc} */
    public double value(double x) {
        return c;
    }

    /** {@inheritDoc}
     * @deprecated as of 3.1, replaced by {@link #value(DerivativeStructure)}
     */
    @Deprecated
    public DifferentiableUnivariateFunction derivative() {
        return new Constant(0);
    }

    /** {@inheritDoc}
     * @since 3.1
     */
    public DerivativeStructure value(final DerivativeStructure t) {
        return new DerivativeStructure(t.getFreeParameters(), t.getOrder(), c);
    }

}
