/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.uima.ruta.expression.number;

import org.apache.uima.ruta.RutaStream;
import org.apache.uima.ruta.rule.MatchContext;

public class SimpleNumberExpression extends AbstractNumberExpression {

  private final Number number;

  public SimpleNumberExpression(Number number) {
    super();
    this.number = number;
  }

  @Override
  public double getDoubleValue(MatchContext context, RutaStream stream) {
    return number.doubleValue();
  }

  @Override
  public float getFloatValue(MatchContext context, RutaStream stream) {
    return number.floatValue();
  }

  @Override
  public int getIntegerValue(MatchContext context, RutaStream stream) {
    return number.intValue();
  }

  public Number getNumber() {
    return number;
  }

  @Override
  public String getStringValue(MatchContext context, RutaStream stream) {
    boolean floating = number.intValue() != number.doubleValue();
    if (floating) {
      return "" + getDoubleValue(context, stream);
    } else {
      return "" + getIntegerValue(context, stream);
    }
  }

}
