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

package org.apache.uima.ruta.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.uima.ruta.RutaBlock;
import org.apache.uima.ruta.RutaElement;
import org.apache.uima.ruta.RutaStream;
import org.apache.uima.ruta.action.AbstractRutaAction;
import org.apache.uima.ruta.condition.AbstractRutaCondition;
import org.apache.uima.ruta.rule.quantifier.NormalQuantifier;
import org.apache.uima.ruta.rule.quantifier.RuleElementQuantifier;
import org.apache.uima.ruta.visitor.InferenceCrowd;

public abstract class AbstractRuleElement extends RutaElement implements RuleElement {

  protected RuleElementQuantifier quantifier;

  protected List<AbstractRutaCondition> conditions;

  protected List<AbstractRutaAction> actions;

  private RuleElementContainer container;

  protected RutaBlock parent;

  public AbstractRuleElement(RuleElementQuantifier quantifier,
          List<AbstractRutaCondition> conditions, List<AbstractRutaAction> actions,
          RuleElementContainer container, RutaBlock parent) {
    super();
    this.quantifier = quantifier;
    this.conditions = conditions;
    this.actions = actions;
    this.container = container;
    this.parent = parent;
    if (this.conditions == null) {
      this.conditions = new ArrayList<AbstractRutaCondition>();
    }
    if (this.actions == null) {
      this.actions = new ArrayList<AbstractRutaAction>();
    }
    if (this.quantifier == null) {
      this.quantifier = new NormalQuantifier();
    }
  }

  @SuppressWarnings("unchecked")
  protected final InferenceCrowd emptyCrowd = new InferenceCrowd(Collections.EMPTY_LIST);

  protected void doneMatching(RuleMatch ruleMatch, RuleApply ruleApply, RutaStream stream,
          InferenceCrowd crowd) {
    if (!ruleMatch.isApplied()) {
      ruleApply.add(ruleMatch);
      if (ruleMatch.matchedCompletely()) {
        ruleMatch.getRule().getRoot().applyRuleElements(ruleMatch, stream, crowd);
      }
      ruleMatch.setApplied(true);
    }
  }

  public void apply(RuleMatch ruleMatch, RutaStream stream, InferenceCrowd crowd) {
    for (AbstractRutaAction action : actions) {
      crowd.beginVisit(action, null);
      action.execute(ruleMatch, this, stream, crowd);
      crowd.endVisit(action, null);
    }
  }

  protected List<RuleElementMatch> getMatch(RuleMatch ruleMatch,
          ComposedRuleElementMatch containerMatch) {
    List<RuleElementMatch> matchInfo;
    if (containerMatch != null) {
      matchInfo = containerMatch.getInnerMatches().get(this);
    } else {
      matchInfo = ruleMatch.getMatchInfo(this).get(0);
    }
    return matchInfo;
  }

  public List<RuleElementMatch> evaluateMatches(List<RuleElementMatch> matches, RutaBlock parent,
          RutaStream stream) {
    return quantifier.evaluateMatches(matches, parent, stream, emptyCrowd);
  }

  public List<Integer> getSelfIndexList() {
    List<Integer> result = new ArrayList<Integer>(1);
    if (getContainer() == null) {
      return null;
    }
    int indexOf = getContainer().getRuleElements().indexOf(this);
    result.add(indexOf + 1);
    return result;
  }

  public boolean hasAncestor(boolean after) {
    RuleElementContainer c = getContainer();
    if (c == null) {
      return false;
    }
    RuleElement nextElement = c.getNextElement(after, this);
    if (nextElement != null) {
      return true;
    }
    if (c instanceof ComposedRuleElement) {
      return ((ComposedRuleElement) c).hasAncestor(after);
    }
    return false;
  }

  public RuleElementQuantifier getQuantifier() {
    return quantifier;
  }

  public RutaBlock getParent() {
    return parent;
  }

  public List<AbstractRutaCondition> getConditions() {
    return conditions;
  }

  public void setConditions(List<AbstractRutaCondition> conditions) {
    this.conditions = conditions;
  }

  public List<AbstractRutaAction> getActions() {
    return actions;
  }

  public void setActions(List<AbstractRutaAction> actions) {
    this.actions = actions;
  }

  public void setQuantifier(RuleElementQuantifier quantifier) {
    this.quantifier = quantifier;
  }

  public RutaRule getRule() {
    return container.getRule();
  }

  public RuleElementContainer getContainer() {
    return container;
  }

}