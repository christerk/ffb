package com.balancedbytes.games.ffb.server.model;

import java.lang.reflect.ParameterizedType;
import java.util.Comparator;

import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.server.step.StepCommandStatus;

public abstract class StepModifier<T extends IStep, V> {
  private Class modifierType;
  private int priority = 0;

  protected StepModifier(int priority) {
    modifierType = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0].getClass();
    this.priority = priority;
  }

  protected void setPriority(int priority) {
    this.priority = priority;
  }
  protected int getPriority() {
    return priority;
  }

  public boolean appliesTo(IStep step) {
    return step.getClass().equals(modifierType);
  }
  
  public Class getConcreteClass() {
    return modifierType;
  }
  
  @SuppressWarnings("unchecked")
  public StepCommandStatus handleCommand(IStep step, Object state, NetCommand netCommand) {
    return handleCommandHook((T) step, (V) state, netCommand);
  }
  
  @SuppressWarnings("unchecked")
  public boolean handleExecuteStep(IStep step, Object state) {
    return handleExecuteStepHook((T) step, (V) state);
  }
    
  abstract public StepCommandStatus handleCommandHook(T step, V state, NetCommand netCommand);
  abstract public boolean handleExecuteStepHook(T step, V state);
  
  public static final Comparator<StepModifier> Comparator = new Comparator<StepModifier>() {
    @Override
    public int compare(StepModifier a, StepModifier b) {
      return Integer.compare(a.getPriority(), b.getPriority());
    }
  };  
}
