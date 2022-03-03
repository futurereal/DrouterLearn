package com.viomi.ovenso.bean;

import com.viomi.ovenso.microwave.R;

/**
 * 预置菜谱
 */
public enum OvenRecipeStepEnum {
    STEP_NO(0, R.string.recipe_step_no, 0),
    STEP_PREHEARTING(1, R.string.recipe_step_prehearting, 0),
    STEP_PREHEART_FINISH(2, R.string.recipe_step_preheartfinish, R.raw.recipestep_prehearting_finish),
    STEP_ADD_EGG(3, R.string.recipe_step_addegg, R.raw.recipestep_add_egg),
    STEP_RECIPE_TWO(4, 0, 0);
    public int stepValue;
    public int stepResourceId;
    public int voiceResource;

    OvenRecipeStepEnum(int stepValue, int stepResourceId, int voiceResource) {
        this.stepValue = stepValue;
        this.stepResourceId = stepResourceId;
        this.voiceResource = voiceResource;
    }
}

