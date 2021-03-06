package crazypants.enderio.machine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MachineRecipeRegistry {

  public static final MachineRecipeRegistry instance = new MachineRecipeRegistry();

  private final Map<String, Map<String, IMachineRecipe>> machineRecipes = new HashMap<String, Map<String, IMachineRecipe>>();

  public void registerRecipe(String machine, IMachineRecipe recipe) {
    getRecipesForMachine(machine).put(recipe.getUid(), recipe);
  }

  public Map<String, IMachineRecipe> getRecipesForMachine(String machineName) {
    Map<String, IMachineRecipe> res = machineRecipes.get(machineName);
    if (res == null) {
      res = new HashMap<String, IMachineRecipe>();
      machineRecipes.put(machineName, res);
    }
    return res;
  }

  public IMachineRecipe getRecipeForUid(String uid) {
    for (Map<String, IMachineRecipe> recipes : machineRecipes.values()) {
      for (IMachineRecipe recipe : recipes.values()) {
        if (uid.equals(recipe.getUid())) {
          return recipe;
        }
      }
    }
    return null;
  }

  public IMachineRecipe getRecipeForInputs(String machineName, RecipeInput... inputs) {
    Map<String, IMachineRecipe> recipes = getRecipesForMachine(machineName);
    if (recipes == null) {
      return null;
    }
    for (IMachineRecipe recipe : recipes.values()) {
      if (recipe.isRecipe(inputs)) {
        return recipe;
      }
    }
    return null;
  }

  public List<IMachineRecipe> getRecipesForInput(String machineName, RecipeInput input) {
    if (input == null) {
      return Collections.emptyList();
    }
    List<IMachineRecipe> result = new ArrayList<IMachineRecipe>();
    Map<String, IMachineRecipe> recipes = getRecipesForMachine(machineName);
    for (IMachineRecipe recipe : recipes.values()) {
      if (recipe.isValidInput(input.slotNumber, input.item)) {
        result.add(recipe);
      }
    }
    return result;
  }

}
