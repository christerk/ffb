package com.balancedbytes.games.ffb.dialog;

import com.balancedbytes.games.ffb.IDialogParameter;
import com.balancedbytes.games.ffb.json.IJsonOption;
import com.balancedbytes.games.ffb.json.UtilJson;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class DialogSwarmingPlayersParameter implements IDialogParameter {

  private int amount;

  @Override
  public DialogId getId() {
    return DialogId.SWARMING;
  }

  public DialogSwarmingPlayersParameter() {
  }

  public DialogSwarmingPlayersParameter(int amount) {
    this.amount = amount;
  }

  @Override
  public IDialogParameter transform() {
    return new DialogSwarmingPlayersParameter(amount);
  }

  public int getAmount() {
    return amount;
  }

  @Override
  public IDialogParameter initFrom(JsonValue pJsonValue) {
    JsonObject jsonObject = UtilJson.toJsonObject(pJsonValue);
    UtilDialogParameter.validateDialogId(this, (DialogId) IJsonOption.DIALOG_ID.getFrom(jsonObject));
    amount = IJsonOption.SWARMING_PLAYER_AMOUNT.getFrom(jsonObject);
    return this;
  }

  @Override
  public JsonObject toJsonValue() {
    JsonObject jsonObject = new JsonObject();
    IJsonOption.DIALOG_ID.addTo(jsonObject, getId());
    IJsonOption.SWARMING_PLAYER_AMOUNT.addTo(jsonObject, amount);
    return jsonObject;
  }
}
